package io.quarkus.arc.processor.cdi.lite.ext;

import io.quarkus.arc.processor.BeanProcessor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;

public class CdiLiteExtProcessor {
    private final org.jboss.jandex.IndexView index;
    private final BeanProcessor.Builder builder;
    private final AllAnnotationOverlays annotationOverlays;
    private final AllAnnotationTransformations annotationTransformations;

    public CdiLiteExtProcessor(org.jboss.jandex.IndexView index, BeanProcessor.Builder builder) {
        this.index = index;
        this.builder = builder;
        this.annotationOverlays = new AllAnnotationOverlays();
        this.annotationTransformations = new AllAnnotationTransformations(index, annotationOverlays);
    }

    public void run() {
        try {
            builder.addAnnotationTransformer(annotationTransformations.classes);
            builder.addAnnotationTransformer(annotationTransformations.methods);
            builder.addAnnotationTransformer(annotationTransformations.fields);

            doRun();
        } catch (Exception e) {
            // TODO proper diagnostics system
            throw new RuntimeException(e);
        } finally {
            annotationOverlays.invalidate();
            annotationTransformations.freeze();
        }
    }

    private void doRun() throws ReflectiveOperationException {
        List<org.jboss.jandex.MethodInfo> extensionMethods = index.getAnnotations(DotNames.ENHANCEMENT)
                .stream()
                .map(it -> it.target().asMethod()) // the annotation can only be put on methods
                .sorted((m1, m2) -> {
                    if (m1 == m2) {
                        // at this particular point, two different org.jboss.jandex.MethodInfo instances are never equal
                        return 0;
                    }

                    OptionalInt p1 = getExtensionMethodPriority(m1);
                    OptionalInt p2 = getExtensionMethodPriority(m2);

                    if (p1.isPresent() && p2.isPresent()) {
                        // must _not_ return 0 if priorities are equal, because that isn't consistent
                        // with the `equals` method (see also above)
                        return p1.getAsInt() < p2.getAsInt() ? 1 : -1;
                    } else if (p1.isPresent()) {
                        return -1;
                    } else if (p2.isPresent()) {
                        return 1;
                    } else {
                        // must _not_ return 0 if both methods are missing a priority, because that isn't consistent
                        // with the `equals` method (see also above)
                        return -1;
                    }
                })
                .collect(Collectors.toList());

        for (org.jboss.jandex.MethodInfo method : extensionMethods) {
            processExtensionMethod(method);
        }
    }

    private OptionalInt getExtensionMethodPriority(org.jboss.jandex.MethodInfo method) {
        // the annotation can only be put on methods, so no need to filter out parameter annotations etc.
        org.jboss.jandex.AnnotationInstance priority = method.annotation(DotNames.EXTENSION_PRIORITY);
        if (priority != null) {
            return OptionalInt.of(priority.value().asInt());
        }
        return OptionalInt.empty();
    }

    private void processExtensionMethod(org.jboss.jandex.MethodInfo method) throws ReflectiveOperationException {
        // TODO
        //  - diagnostics

        List<Object> arguments = new ArrayList<>();
        int numParameters = method.parameters().size();
        for (int i = 0; i < numParameters; i++) {
            org.jboss.jandex.Type parameterType = method.parameters().get(i);
            ExtensionMethodParameterType kind = ExtensionMethodParameterType.of(parameterType);

            int parameterPosition = i;
            Map<DotName, org.jboss.jandex.AnnotationInstance> parameterAnnotations = method.annotations()
                    .stream()
                    .filter(it -> it.target().kind() == org.jboss.jandex.AnnotationTarget.Kind.METHOD_PARAMETER
                            && it.target().asMethodParameter().position() == parameterPosition)
                    .collect(Collectors.toMap(org.jboss.jandex.AnnotationInstance::name, Function.identity()));

            Collection<org.jboss.jandex.ClassInfo> matchingClasses = matchingClassesForExtensionMethodParameter(kind,
                    parameterAnnotations);

            Set<DotName> requiredAnnotations = requiredAnnotationsForExtensionMethodParameter(method, i);

            Object argument = createArgumentForExtensionMethodParameter(kind, requiredAnnotations, matchingClasses);

            arguments.add(argument);
        }

        callExtensionMethod(method, arguments);
    }

    private enum Phase {
        DISCOVERY,
        ENHANCEMENT,
        SYNTHESIS,
        VALIDATION
    }

    private enum ExtensionMethodParameterType {
        CLASS_INFO(Phase.ENHANCEMENT, Phase.SYNTHESIS, Phase.VALIDATION),

        COLLECTION_CLASS_INFO(Phase.ENHANCEMENT, Phase.SYNTHESIS, Phase.VALIDATION),
        COLLECTION_METHOD_INFO(Phase.ENHANCEMENT, Phase.SYNTHESIS, Phase.VALIDATION),
        COLLECTION_FIELD_INFO(Phase.ENHANCEMENT, Phase.SYNTHESIS, Phase.VALIDATION),

        CLASS_CONFIG(Phase.ENHANCEMENT),

        COLLECTION_CLASS_CONFIG(Phase.ENHANCEMENT),
        COLLECTION_METHOD_CONFIG(Phase.ENHANCEMENT),
        COLLECTION_FIELD_CONFIG(Phase.ENHANCEMENT),

        COLLECTION_BEAN_INFO(Phase.SYNTHESIS, Phase.VALIDATION),
        COLLECTION_OBSERVER_INFO(Phase.SYNTHESIS, Phase.VALIDATION),

        ANNOTATIONS(Phase.ENHANCEMENT),
        APP_ARCHIVE(Phase.ENHANCEMENT, Phase.SYNTHESIS, Phase.VALIDATION),
        APP_ARCHIVE_BUILDER(Phase.DISCOVERY),
        APP_ARCHIVE_CONFIG(Phase.ENHANCEMENT),
        APP_DEPLOYMENT(Phase.SYNTHESIS, Phase.VALIDATION),
        CONTEXTS(Phase.DISCOVERY),
        ERRORS(Phase.VALIDATION),
        SYNTHETIC_COMPONENTS(Phase.SYNTHESIS),
        TYPES(Phase.ENHANCEMENT, Phase.SYNTHESIS, Phase.VALIDATION),

        UNKNOWN,
        ;

        private final Set<Phase> validPhases;

        ExtensionMethodParameterType(Phase... validPhases) {
            if (validPhases == null || validPhases.length == 0) {
                this.validPhases = EnumSet.noneOf(Phase.class);
            } else {
                this.validPhases = EnumSet.copyOf(Arrays.asList(validPhases));
            }
        }

        boolean isQuery() {
            return this != ANNOTATIONS
                    && this != APP_ARCHIVE
                    && this != APP_ARCHIVE_BUILDER
                    && this != APP_ARCHIVE_CONFIG
                    && this != APP_DEPLOYMENT
                    && this != CONTEXTS
                    && this != ERRORS
                    && this != SYNTHETIC_COMPONENTS
                    && this != TYPES
                    && this != UNKNOWN;
        }

        boolean isClassQuery() {
            return this == CLASS_INFO
                    || this == CLASS_CONFIG
                    || this == COLLECTION_CLASS_INFO
                    || this == COLLECTION_CLASS_CONFIG;
        }

        boolean isSingularQuery() {
            return this == CLASS_INFO
                    || this == CLASS_CONFIG;
        }

        boolean isAvailableIn(Phase phase) {
            return validPhases.contains(phase);
        }

        static ExtensionMethodParameterType of(org.jboss.jandex.Type type) {
            if (type.kind() == org.jboss.jandex.Type.Kind.PARAMETERIZED_TYPE) {
                if (type.name().equals(DotNames.COLLECTION)) {
                    org.jboss.jandex.Type collectionElement = type.asParameterizedType().arguments().get(0);
                    if (collectionElement.name().equals(DotNames.CLASS_INFO)) {
                        return COLLECTION_CLASS_INFO;
                    } else if (collectionElement.name().equals(DotNames.METHOD_INFO)) {
                        return COLLECTION_METHOD_INFO;
                    } else if (collectionElement.name().equals(DotNames.FIELD_INFO)) {
                        return COLLECTION_FIELD_INFO;
                    } else if (collectionElement.name().equals(DotNames.CLASS_CONFIG)) {
                        return COLLECTION_CLASS_CONFIG;
                    } else if (collectionElement.name().equals(DotNames.METHOD_CONFIG)) {
                        return COLLECTION_METHOD_CONFIG;
                    } else if (collectionElement.name().equals(DotNames.FIELD_CONFIG)) {
                        return COLLECTION_FIELD_CONFIG;
                    } else if (collectionElement.name().equals(DotNames.BEAN_INFO)) {
                        return COLLECTION_BEAN_INFO;
                    } else if (collectionElement.name().equals(DotNames.OBSERVER_INFO)) {
                        return COLLECTION_OBSERVER_INFO;
                    }
                }
            } else if (type.kind() == org.jboss.jandex.Type.Kind.CLASS) {
                if (type.name().equals(DotNames.ANNOTATIONS)) {
                    return ANNOTATIONS;
                } else if (type.name().equals(DotNames.APP_ARCHIVE)) {
                    return APP_ARCHIVE;
                } else if (type.name().equals(DotNames.APP_ARCHIVE_BUILDER)) {
                    return APP_ARCHIVE_BUILDER;
                } else if (type.name().equals(DotNames.APP_ARCHIVE_CONFIG)) {
                    return APP_ARCHIVE_CONFIG;
                } else if (type.name().equals(DotNames.APP_DEPLOYMENT)) {
                    return APP_DEPLOYMENT;
                } else if (type.name().equals(DotNames.CLASS_INFO)) {
                    return CLASS_INFO;
                } else if (type.name().equals(DotNames.CLASS_CONFIG)) {
                    return CLASS_CONFIG;
                } else if (type.name().equals(DotNames.CONTEXTS)) {
                    return CONTEXTS;
                } else if (type.name().equals(DotNames.ERRORS)) {
                    return ERRORS;
                } else if (type.name().equals(DotNames.SYNTHETIC_COMPONENTS)) {
                    return SYNTHETIC_COMPONENTS;
                } else if (type.name().equals(DotNames.TYPES)) {
                    return TYPES;
                }
            }

            return UNKNOWN;
        }
    }

    private Set<DotName> requiredAnnotationsForExtensionMethodParameter(org.jboss.jandex.MethodInfo jandexMethod,
            int parameterPosition) {
        Set<DotName> requiredAnnotations = null;

        Optional<org.jboss.jandex.AnnotationInstance> jandexAnnotation = jandexMethod.annotations(DotNames.WITH_ANNOTATIONS)
                .stream()
                .filter(it -> it.target().kind() == org.jboss.jandex.AnnotationTarget.Kind.METHOD_PARAMETER
                        && it.target().asMethodParameter().position() == parameterPosition)
                .findAny();

        if (jandexAnnotation.isPresent()) {
            org.jboss.jandex.AnnotationValue jandexAnnotationAttribute = jandexAnnotation.get().value();
            if (jandexAnnotationAttribute != null) {
                org.jboss.jandex.Type[] jandexTypes = jandexAnnotationAttribute.asClassArray();
                if (jandexTypes.length > 0) {
                    requiredAnnotations = Arrays.stream(jandexTypes)
                            .map(org.jboss.jandex.Type::asClassType)
                            .map(org.jboss.jandex.Type::name)
                            .collect(Collectors.toSet());
                }
            }
        }

        return requiredAnnotations;
    }

    // TODO the query implementations here (in matchingClassesForExtensionMethodParameter
    //  and createArgumentForExtensionMethodParameter) duplicate AppArchiveImpl quite a bit!

    private Collection<org.jboss.jandex.ClassInfo> matchingClassesForExtensionMethodParameter(ExtensionMethodParameterType kind,
            Map<DotName, org.jboss.jandex.AnnotationInstance> jandexParameterAnnotations) {

        if (!kind.isQuery()) {
            return Collections.emptySet();
        }

        if (jandexParameterAnnotations.containsKey(DotNames.EXACT_TYPE)) {
            String typeName = jandexParameterAnnotations.get(DotNames.EXACT_TYPE).value().asString();
            org.jboss.jandex.ClassInfo jandexClass = index.getClassByName(DotName.createSimple(typeName));
            if (jandexClass == null) {
                // TODO proper diagnostics
                throw new NullPointerException("class " + typeName + " not found");
            }
            return Collections.singleton(jandexClass);
        } else if (jandexParameterAnnotations.containsKey(DotNames.SUBTYPES_OF)) {
            String typeName = jandexParameterAnnotations.get(DotNames.SUBTYPES_OF).value().asString();
            DotName upperBoundName = DotName.createSimple(typeName);
            org.jboss.jandex.ClassInfo jandexClass = index.getClassByName(upperBoundName);
            if (jandexClass == null) {
                // TODO proper diagnostics
                throw new NullPointerException("class " + typeName + " not found");
            }
            // TODO index.getAllKnown* is not reflexive; should add the original type ourselves?
            //  we do that for lower bound currently (see below)
            return Modifier.isInterface(jandexClass.flags())
                    ? index.getAllKnownImplementors(upperBoundName)
                    : index.getAllKnownSubclasses(upperBoundName);
        } else if (jandexParameterAnnotations.containsKey(DotNames.SUPERTYPES_OF)) {
            String typeName = jandexParameterAnnotations.get(DotNames.SUPERTYPES_OF).value().asString();
            DotName lowerBoundName = DotName.createSimple(typeName);

            List<org.jboss.jandex.ClassInfo> result = new ArrayList<>();
            DotName name = lowerBoundName;
            while (name != null) {
                org.jboss.jandex.ClassInfo jandexClass = index.getClassByName(name);
                if (jandexClass != null) {
                    result.add(jandexClass);
                    name = jandexClass.superName();
                } else {
                    // TODO proper diagnostics
                    throw new NullPointerException("class " + typeName + " not found");
                }
            }

            return result;
        } else {
            return index.getKnownClasses();
        }
    }

    private Object createArgumentForExtensionMethodParameter(ExtensionMethodParameterType kind,
            Set<DotName> requiredAnnotations, Collection<org.jboss.jandex.ClassInfo> matchingClasses) {
        switch (kind) {
            case CLASS_INFO:
                if (matchingClasses.size() == 1) {
                    return new ClassInfoImpl(index, annotationOverlays, matchingClasses.iterator().next());
                } else {
                    // TODO should report an error here
                    return null;
                }

            case CLASS_CONFIG:
                if (matchingClasses.size() == 1) {
                    return new ClassConfigImpl(index, annotationTransformations.classes, matchingClasses.iterator().next());
                } else {
                    // TODO should report an error here
                    return null;
                }

            case COLLECTION_CLASS_INFO:
                return matchingClasses.stream()
                        .filter(it -> hasRequiredAnnotations(it, requiredAnnotations))
                        .map(it -> new ClassInfoImpl(index, annotationOverlays, it))
                        .collect(Collectors.toList());
            case COLLECTION_METHOD_INFO:
                return matchingClasses.stream()
                        .flatMap(it -> it.methods().stream())
                        .filter(MethodPredicates.IS_METHOD_OR_CONSTRUCTOR_JANDEX)
                        .filter(it -> hasRequiredAnnotations(it, requiredAnnotations))
                        .map(it -> new MethodInfoImpl(index, annotationOverlays, it))
                        .collect(Collectors.toList());
            case COLLECTION_FIELD_INFO:
                return matchingClasses.stream()
                        .flatMap(it -> it.fields().stream())
                        .filter(it -> hasRequiredAnnotations(it, requiredAnnotations))
                        .map(it -> new FieldInfoImpl(index, annotationOverlays, it))
                        .collect(Collectors.toList());

            case COLLECTION_CLASS_CONFIG:
                return matchingClasses.stream()
                        .filter(it -> hasRequiredAnnotations(it, requiredAnnotations))
                        .map(it -> new ClassConfigImpl(index, annotationTransformations.classes, it))
                        .collect(Collectors.toList());
            case COLLECTION_METHOD_CONFIG:
                return matchingClasses.stream()
                        .flatMap(it -> it.methods().stream())
                        .filter(MethodPredicates.IS_METHOD_OR_CONSTRUCTOR_JANDEX)
                        .filter(it -> hasRequiredAnnotations(it, requiredAnnotations))
                        .map(it -> new MethodConfigImpl(index, annotationTransformations.methods, it))
                        .collect(Collectors.toList());
            case COLLECTION_FIELD_CONFIG:
                return matchingClasses.stream()
                        .flatMap(it -> it.fields().stream())
                        .filter(it -> hasRequiredAnnotations(it, requiredAnnotations))
                        .map(it -> new FieldConfigImpl(index, annotationTransformations.fields, it))
                        .collect(Collectors.toList());

            case ANNOTATIONS:
                return new AnnotationsImpl(index, annotationOverlays);
            case APP_ARCHIVE:
                return new AppArchiveImpl(index, annotationTransformations);
            case APP_ARCHIVE_CONFIG:
                return new AppArchiveConfigImpl(index, annotationTransformations);
            case TYPES:
                return new TypesImpl(index, annotationOverlays);

            default:
                // TODO should report an error here
                return null;
        }
    }

    private static boolean hasRequiredAnnotations(org.jboss.jandex.ClassInfo jandexClass,
            Set<DotName> requiredJandexAnnotations) {
        return areAnnotationsPresent(jandexClass.classAnnotations().stream(), requiredJandexAnnotations);
    }

    private static boolean hasRequiredAnnotations(org.jboss.jandex.MethodInfo jandexMethod,
            Set<DotName> requiredJandexAnnotations) {
        Stream<org.jboss.jandex.AnnotationInstance> jandexAnnotations = jandexMethod.annotations()
                .stream()
                .filter(it -> it.target().kind() == org.jboss.jandex.AnnotationTarget.Kind.METHOD);

        return areAnnotationsPresent(jandexAnnotations, requiredJandexAnnotations);
    }

    private static boolean hasRequiredAnnotations(org.jboss.jandex.FieldInfo jandexField,
            Set<DotName> requiredJandexAnnotations) {
        return areAnnotationsPresent(jandexField.annotations().stream(), requiredJandexAnnotations);
    }

    private static boolean areAnnotationsPresent(Stream<org.jboss.jandex.AnnotationInstance> presentAnnotations,
            Set<DotName> expectedAnnotations) {
        if (expectedAnnotations == null || expectedAnnotations.isEmpty()) {
            return true;
        }

        return presentAnnotations
                .map(org.jboss.jandex.AnnotationInstance::name)
                .anyMatch(expectedAnnotations::contains);
    }

    // ---
    // the following methods use reflection, everything else in this class is reflection-free

    private final Map<String, Class<?>> extensionClasses = new HashMap<>();
    private final Map<Class<?>, Object> extensionClassInstances = new HashMap<>();

    private Class<?> getExtensionClass(String className) {
        return extensionClasses.computeIfAbsent(className, ignored -> {
            try {
                return Class.forName(className, true, Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Object getExtensionClassInstance(Class<?> clazz) {
        return extensionClassInstances.computeIfAbsent(clazz, ignored -> {
            try {
                return clazz.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void callExtensionMethod(org.jboss.jandex.MethodInfo jandexMethod, List<Object> arguments)
            throws ReflectiveOperationException {

        Class<?>[] parameterTypes = new Class[arguments.size()];

        for (int i = 0; i < parameterTypes.length; i++) {
            Object argument = arguments.get(i);
            Class<?> argumentClass = argument.getClass();

            // beware of ordering! subtypes must precede supertypes
            if (java.util.Collection.class.isAssignableFrom(argumentClass)) {
                parameterTypes[i] = java.util.Collection.class;
            } else if (cdi.lite.extension.phases.enhancement.Annotations.class.isAssignableFrom(argumentClass)) {
                parameterTypes[i] = cdi.lite.extension.phases.enhancement.Annotations.class;
            } else if (cdi.lite.extension.phases.enhancement.AppArchiveConfig.class.isAssignableFrom(argumentClass)) {
                parameterTypes[i] = cdi.lite.extension.phases.enhancement.AppArchiveConfig.class;
            } else if (cdi.lite.extension.AppArchive.class.isAssignableFrom(argumentClass)) {
                parameterTypes[i] = cdi.lite.extension.AppArchive.class;
            } else if (cdi.lite.extension.phases.enhancement.ClassConfig.class.isAssignableFrom(argumentClass)) {
                parameterTypes[i] = cdi.lite.extension.phases.enhancement.ClassConfig.class;
            } else if (cdi.lite.extension.model.declarations.ClassInfo.class.isAssignableFrom(argumentClass)) {
                parameterTypes[i] = cdi.lite.extension.model.declarations.ClassInfo.class;
            } else if (cdi.lite.extension.Types.class.isAssignableFrom(argumentClass)) {
                parameterTypes[i] = cdi.lite.extension.Types.class;
            } else {
                // should never happen, internal error (or missing error handling) if it does
                throw new IllegalArgumentException("Unexpected extension method argument: " + argument);
            }
        }

        Class<?> extensionClass = getExtensionClass(jandexMethod.declaringClass().name().toString());
        Object extensionClassInstance = getExtensionClassInstance(extensionClass);

        Method methodReflective = extensionClass.getMethod(jandexMethod.name(), parameterTypes);
        methodReflective.invoke(extensionClassInstance, arguments.toArray());
    }
}
