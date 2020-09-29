package io.quarkus.arc.processor.cdi.lite.ext;

import cdi.lite.extension.AppArchive;
import cdi.lite.extension.Types;
import cdi.lite.extension.model.declarations.ClassInfo;
import cdi.lite.extension.model.declarations.FieldInfo;
import cdi.lite.extension.model.declarations.MethodInfo;
import cdi.lite.extension.model.types.Type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import cdi.lite.extension.phases.enhancement.AppArchiveConfig;
import cdi.lite.extension.phases.enhancement.ClassConfig;
import cdi.lite.extension.phases.enhancement.FieldConfig;
import cdi.lite.extension.phases.enhancement.MethodConfig;
import org.jboss.jandex.DotName;

class AppArchiveImpl implements AppArchive {
    final org.jboss.jandex.IndexView jandexIndex;
    final AllAnnotationTransformations annotationTransformations;
    final AllAnnotationOverlays annotationOverlays;
    final AnnotationsOverlay.Classes classesOverlay;
    final AnnotationsOverlay.Methods methodsOverlay;
    final AnnotationsOverlay.Fields fieldsOverlay;
    final TypesImpl types;

    AppArchiveImpl(org.jboss.jandex.IndexView jandexIndex, AllAnnotationTransformations annotationTransformations) {
        this.jandexIndex = jandexIndex;
        this.annotationTransformations = annotationTransformations;
        this.annotationOverlays = annotationTransformations.annotationOverlays;
        this.classesOverlay = annotationTransformations.annotationOverlays.classes;
        this.methodsOverlay = annotationTransformations.annotationOverlays.methods;
        this.fieldsOverlay = annotationTransformations.annotationOverlays.fields;
        this.types = new TypesImpl(jandexIndex, annotationOverlays);
    }

    @Override
    public Types types() {
        return types;
    }

    @Override
    public ClassQuery classes() {
        return new ClassQueryImpl();
    }

    @Override
    public MethodQuery constructors() {
        return new MethodQueryImpl(true);
    }

    @Override
    public MethodQuery methods() {
        return new MethodQueryImpl(false);
    }

    @Override
    public FieldQuery fields() {
        return new FieldQueryImpl();
    }

    class ClassQueryImpl implements ClassQuery {
        protected Set<DotName> requiredJandexClasses;
        protected Set<DotName> requiredJandexAnnotations;

        @Override
        public ClassQuery exactly(String clazz) {
            if (requiredJandexClasses == null) {
                requiredJandexClasses = new HashSet<>();
            }

            requiredJandexClasses.add(DotName.createSimple(clazz));

            return this;
        }

        @Override
        public ClassQuery exactly(ClassInfo clazz) {
            if (requiredJandexClasses == null) {
                requiredJandexClasses = new HashSet<>();
            }

            requiredJandexClasses.add(((ClassInfoImpl) clazz).jandexDeclaration.name());

            return this;
        }

        @Override
        public ClassQuery exactly(Type ref) {
            if (requiredJandexClasses == null) {
                requiredJandexClasses = new HashSet<>();
            }

            requiredJandexClasses.add(((TypeImpl<?>)ref).jandexType.name());

            return this;
        }

        @Override
        public ClassQuery subtypeOf(String clazz) {
            if (requiredJandexClasses == null) {
                requiredJandexClasses = new HashSet<>();
            }

            DotName name = DotName.createSimple(clazz);
            boolean isInterface = Modifier.isInterface(jandexIndex.getClassByName(name).flags());
            addSubClassesToRequiredClassesSet(name, isInterface);

            return this;
        }

        @Override
        public ClassQuery subtypeOf(ClassInfo clazz) {
            if (requiredJandexClasses == null) {
                requiredJandexClasses = new HashSet<>();
            }

            DotName name = ((ClassInfoImpl) clazz).jandexDeclaration.name();
            addSubClassesToRequiredClassesSet(name, clazz.isInterface());

            return this;
        }

        @Override
        public ClassQuery subtypeOf(Type ref) {
            if (requiredJandexClasses == null) {
                requiredJandexClasses = new HashSet<>();
            }


            DotName name = ((TypeImpl<?>) ref).jandexType.name();
            boolean isInterface = ref.isClass() && ref.asClass().declaration().isInterface();
            addSubClassesToRequiredClassesSet(name, isInterface);
            return this;
        }

        private void addSubClassesToRequiredClassesSet(DotName name, boolean isInterface) {
            // TODO getAllKnown* is not reflexive
            if (isInterface) {
                jandexIndex.getAllKnownImplementors(name)
                        .stream()
                        .map(org.jboss.jandex.ClassInfo::name)
                        .forEach(requiredJandexClasses::add);
            } else {
                jandexIndex.getAllKnownSubclasses(name)
                        .stream()
                        .map(org.jboss.jandex.ClassInfo::name)
                        .forEach(requiredJandexClasses::add);
            }
        }

        @Override
        public ClassQuery supertypeOf(String clazz) {
            if (requiredJandexClasses == null) {
                requiredJandexClasses = new HashSet<>();
            }

            DotName name = DotName.createSimple(clazz);
            addSuperClassesToRequiredClassesSet(name);

            return this;
        }

        @Override
        public ClassQuery supertypeOf(ClassInfo clazz) {
            if (requiredJandexClasses == null) {
                requiredJandexClasses = new HashSet<>();
            }

            DotName name = ((ClassInfoImpl) clazz).jandexDeclaration.name();
            addSuperClassesToRequiredClassesSet(name);

            return this;
        }

        @Override
        public ClassQuery supertypeOf(Type ref) {
            if (requiredJandexClasses == null) {
                requiredJandexClasses = new HashSet<>();
            }

            DotName name = ((TypeImpl<?>) ref).jandexType.name();
            addSuperClassesToRequiredClassesSet(name);
            return this;
        }

        private void addSuperClassesToRequiredClassesSet(DotName name) {
            while (name != null) {
                org.jboss.jandex.ClassInfo jandexClass = jandexIndex.getClassByName(name);
                if (jandexClass != null) {
                    requiredJandexClasses.add(jandexClass.name());
                    name = jandexClass.superName();
                } else {
                    // should report an error here
                    name = null;
                }
            }
        }

        @Override
        public ClassQuery annotatedWith(Class<? extends Annotation> annotationType) {
            if (requiredJandexAnnotations == null) {
                requiredJandexAnnotations = new HashSet<>();
            }

            requiredJandexAnnotations.add(DotName.createSimple(annotationType.getName()));

            return this;
        }

        @Override
        public ClassQuery annotatedWith(String annotationName) {
            if (requiredJandexAnnotations == null) {
                requiredJandexAnnotations = new HashSet<>();
            }

            requiredJandexAnnotations.add(DotName.createSimple(annotationName));

            return this;
        }

        @Override
        public ClassQuery annotatedWith(ClassInfo annotationType) {
            if (requiredJandexAnnotations == null) {
                requiredJandexAnnotations = new HashSet<>();
            }

            requiredJandexAnnotations.add(((ClassInfoImpl) annotationType).jandexDeclaration.name());

            return this;
        }

        @Override
        public ClassQuery annotatedWith(Type annotationType) {
            if (requiredJandexAnnotations == null) {
                requiredJandexAnnotations = new HashSet<>();
            }

            requiredJandexAnnotations.add(((TypeImpl) annotationType).jandexType.name());

            return this;
        }

        protected Stream<ClassInfo> stream() {
            if (requiredJandexClasses != null && requiredJandexAnnotations != null) {
                return requiredJandexClasses.stream()
                        .map(jandexIndex::getClassByName)
                        .filter(jandexClass -> {
                            for (DotName requiredJandexAnnotation : requiredJandexAnnotations) {
                                if (classesOverlay.hasAnnotation(jandexClass, requiredJandexAnnotation)) {
                                    return true;
                                }
                            }
                            return false;
                        })
                        .map(it -> new ClassInfoImpl(jandexIndex, annotationOverlays, it));
            } else if (requiredJandexClasses != null) {
                return requiredJandexClasses.stream()
                        .map(jandexIndex::getClassByName)
                        .map(it -> new ClassInfoImpl(jandexIndex, annotationOverlays, it));
            } else if (requiredJandexAnnotations != null) {
                Stream<ClassInfo> result = null;
                for (DotName requiredJandexAnnotation : requiredJandexAnnotations) {
                    Stream<ClassInfo> partialResult = jandexIndex.getAnnotations(requiredJandexAnnotation)
                            .stream()
                            .filter(it -> it.target().kind() == org.jboss.jandex.AnnotationTarget.Kind.CLASS)
                            .map(it -> it.target().asClass())
                            .filter(it -> classesOverlay.hasAnnotation(it, requiredJandexAnnotation))
                            .map(it -> new ClassInfoImpl(jandexIndex, annotationOverlays, it));
                    if (result == null) {
                        result = partialResult;
                    } else {
                        result = Stream.concat(result, partialResult);
                    }

                    Stream<ClassInfoImpl> fromOverlay = classesOverlay
                            .overlaidDeclarationsWithAnnotation(requiredJandexAnnotation)
                            .stream()
                            .map(it -> new ClassInfoImpl(jandexIndex, annotationOverlays, it));
                    result = Stream.concat(result, fromOverlay);
                }
                return result == null ? Stream.empty() : result.distinct();
            } else {
                return jandexIndex.getKnownClasses()
                        .stream()
                        .map(it -> new ClassInfoImpl(jandexIndex, annotationOverlays, it));
            }
        }

        @Override
        public ClassQuery process(Consumer<ClassInfo> annotationConfig) {
            stream()
                    .forEach(annotationConfig);
            return this;
        }
    }

    class MethodQueryImpl implements MethodQuery {
        final Predicate<String> nameFilter;
        List<ClassQuery> requiredDeclarationSites; // elements not guaranteed to be distinct!
        Set<org.jboss.jandex.Type> requiredJandexReturnTypes;
        Set<DotName> requiredJandexAnnotations;

        MethodQueryImpl(boolean constructors) {
            this.nameFilter = constructors ? MethodPredicates.IS_CONSTRUCTOR : MethodPredicates.IS_METHOD;
        }

        @Override
        public MethodQuery declaredOn(ClassQuery classes) {
            if (requiredDeclarationSites == null) {
                requiredDeclarationSites = new ArrayList<>();
            }
            requiredDeclarationSites.add(classes);
            return this;
        }

        @Override
        public MethodQuery withReturnType(String type) {
            if (requiredJandexReturnTypes == null) {
                requiredJandexReturnTypes = new HashSet<>();
            }

            requiredJandexReturnTypes.add(TypesReflection.jandexType(type));

            return this;
        }

        @Override
        public MethodQuery withReturnType(Type type) {
            if (requiredJandexReturnTypes == null) {
                requiredJandexReturnTypes = new HashSet<>();
            }

            requiredJandexReturnTypes.add(((TypeImpl<?>) type).jandexType);

            return this;
        }

        @Override
        public MethodQuery annotatedWith(Class<? extends Annotation> annotationType) {
            if (requiredJandexAnnotations == null) {
                requiredJandexAnnotations = new HashSet<>();
            }

            requiredJandexAnnotations.add(DotName.createSimple(annotationType.getName()));

            return this;
        }

        @Override
        public MethodQuery annotatedWith(ClassInfo annotationType) {
            if (requiredJandexAnnotations == null) {
                requiredJandexAnnotations = new HashSet<>();
            }

            requiredJandexAnnotations.add(((ClassInfoImpl) annotationType).jandexDeclaration.name());

            return this;
        }

        @Override
        public MethodQuery annotatedWith(Type annotationType) {
            if (requiredJandexAnnotations == null) {
                requiredJandexAnnotations = new HashSet<>();
            }

            requiredJandexAnnotations.add(((TypeImpl) annotationType).jandexType.name());

            return this;
        }

        protected Stream<MethodInfo> applyFilters(Stream<MethodInfo> methods) {
            methods = methods.filter(it -> nameFilter.test(it.name()));
            if (requiredJandexReturnTypes != null) {
                methods = methods.filter(it -> requiredJandexReturnTypes.contains(((MethodInfoImpl) it).jandexDeclaration.returnType()));
            }
            if (requiredJandexAnnotations != null) {
                methods = methods.filter(it -> {
                    for (DotName requiredJandexAnnotation : requiredJandexAnnotations) {
                        if (methodsOverlay.hasAnnotation(((MethodInfoImpl) it).jandexDeclaration,
                                requiredJandexAnnotation)) {
                            return true;
                        }
                    }
                    return false;
                });
            }
            return methods;
        }

        @Override
        public MethodQuery process(Consumer<MethodInfo> annotationConfig) {
            if (requiredDeclarationSites != null) {
                for (ClassQuery requiredDeclarationSite : requiredDeclarationSites) {
                    ((AppArchiveConfig.ClassConfigQuery) requiredDeclarationSite).configure(classConfig -> {
                        applyFilters(classConfig.methods().stream())
                                .forEach(annotationConfig);
                    });
                }
            } else {
                Stream<MethodInfo> methodInfos = jandexIndex.getKnownClasses()
                        .stream()
                        .flatMap(it -> it.methods().stream())
                        .map(it -> new MethodInfoImpl(
                                jandexIndex,
                                annotationOverlays,
                                it)
                        );

                applyFilters(methodInfos)
                        .forEach(annotationConfig);

            }

            return this;
        }
    }

    class FieldQueryImpl implements FieldQuery {
        List<ClassQuery> requiredDeclarationSites; // elements not guaranteed to be distinct!
        Set<org.jboss.jandex.Type> requiredJandexTypes;
        Set<DotName> requiredJandexAnnotations;

        @Override
        public FieldQuery declaredOn(ClassQuery classes) {
            if (requiredDeclarationSites == null) {
                requiredDeclarationSites = new ArrayList<>();
            }
            requiredDeclarationSites.add(classes);

            return this;
        }

        @Override
        public FieldQuery ofType(String type) {
            if (requiredJandexTypes == null) {
                requiredJandexTypes = new HashSet<>();
            }

            requiredJandexTypes.add(TypesReflection.jandexType(type));

            return this;
        }

        @Override
        public FieldQuery ofType(Type type) {
            if (requiredJandexTypes == null) {
                requiredJandexTypes = new HashSet<>();
            }

            requiredJandexTypes.add(((TypeImpl<?>) type).jandexType);

            return this;
        }

        @Override
        public FieldQuery annotatedWith(Class<? extends Annotation> annotationType) {
            if (requiredJandexAnnotations == null) {
                requiredJandexAnnotations = new HashSet<>();
            }

            requiredJandexAnnotations.add(DotName.createSimple(annotationType.getName()));

            return this;
        }

        @Override
        public FieldQuery annotatedWith(ClassInfo annotationType) {
            if (requiredJandexAnnotations == null) {
                requiredJandexAnnotations = new HashSet<>();
            }

            requiredJandexAnnotations.add(((ClassInfoImpl) annotationType).jandexDeclaration.name());

            return this;
        }

        @Override
        public FieldQuery annotatedWith(Type annotationType) {
            if (requiredJandexAnnotations == null) {
                requiredJandexAnnotations = new HashSet<>();
            }

            requiredJandexAnnotations.add(((TypeImpl) annotationType).jandexType.name());

            return this;
        }

        @Override
        public FieldQuery process(Consumer<FieldInfo> annotationConfig) {
            if (requiredDeclarationSites != null) {
                for (ClassQuery requiredDeclarationSite : requiredDeclarationSites) {
                    ((AppArchiveConfig.ClassConfigQuery) requiredDeclarationSite).configure(classConfig -> {
                        applyFilters(classConfig.fields().stream())
                                .forEach(annotationConfig);
                    });
                }
            } else {
                Stream<FieldInfo> fieldInfo = jandexIndex.getKnownClasses()
                        .stream()
                        .flatMap(it -> it.fields().stream())
                        .map(it -> new FieldInfoImpl(
                                jandexIndex,
                                annotationOverlays,
                                it)
                        );

                applyFilters(fieldInfo)
                        .forEach(annotationConfig);

            }

            return this;
        }

        protected Stream<FieldInfo> applyFilters(Stream<FieldInfo> fields) {
            if (requiredJandexTypes != null) {

                fields = fields
                        .filter(it -> requiredJandexTypes.contains(((FieldInfoImpl) it).jandexDeclaration.type()));
            }
            if (requiredJandexAnnotations != null) {
                fields = fields.filter(it -> {
                    for (DotName requiredJandexAnnotation : requiredJandexAnnotations) {
                        if (fieldsOverlay.hasAnnotation(((FieldInfoImpl) it).jandexDeclaration,
                                requiredJandexAnnotation)) {
                            return true;
                        }
                    }
                    return false;
                });
            }
            return fields;
        }
    }
}
