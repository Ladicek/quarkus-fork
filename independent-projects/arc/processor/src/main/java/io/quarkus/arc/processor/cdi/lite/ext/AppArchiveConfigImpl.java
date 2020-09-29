package io.quarkus.arc.processor.cdi.lite.ext;

import cdi.lite.extension.model.declarations.ClassInfo;
import cdi.lite.extension.model.declarations.FieldInfo;
import cdi.lite.extension.model.declarations.MethodInfo;
import cdi.lite.extension.model.types.Type;
import cdi.lite.extension.phases.enhancement.AppArchiveConfig;
import cdi.lite.extension.phases.enhancement.ClassConfig;
import cdi.lite.extension.phases.enhancement.FieldConfig;
import cdi.lite.extension.phases.enhancement.MethodConfig;
import org.jboss.jandex.DotName;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AppArchiveConfigImpl extends AppArchiveImpl implements AppArchiveConfig {
    AppArchiveConfigImpl(org.jboss.jandex.IndexView jandexIndex, AllAnnotationTransformations annotationTransformations) {
        super(jandexIndex, annotationTransformations);
    }

    @Override
    public ClassConfigQuery classes() {
        return new ClassConfigQueryImpl();
    }

    @Override
    public MethodConfigQuery constructors() {
        return new MethodConfigQueryImpl(true);
    }

    @Override
    public MethodConfigQuery methods() {
        return new MethodConfigQueryImpl(false);
    }

    @Override
    public FieldConfigQuery fields() {
        return new FieldConfigQueryImpl();
    }

    private class ClassConfigQueryImpl extends ClassQueryImpl implements ClassConfigQuery {
        @Override
        public ClassConfigQuery exactly(String clazz) {
            super.exactly(clazz);
            return this;
        }

        @Override
        public ClassConfigQuery exactly(ClassInfo clazz) {
            super.exactly(clazz);
            return this;
        }

        @Override
        public ClassConfigQuery subtypeOf(String clazz) {
            super.subtypeOf(clazz);
            return this;
        }

        @Override
        public ClassConfigQuery subtypeOf(ClassInfo clazz) {
            super.subtypeOf(clazz);
            return this;
        }

        @Override
        public ClassConfigQuery supertypeOf(String clazz) {
            super.supertypeOf(clazz);
            return this;
        }

        @Override
        public ClassConfigQuery supertypeOf(ClassInfo clazz) {
            super.supertypeOf(clazz);
            return this;
        }

        @Override
        public ClassConfigQuery annotatedWith(Class<? extends Annotation> annotationType) {
            super.annotatedWith(annotationType);
            return this;
        }

        @Override
        public ClassConfigQuery annotatedWith(ClassInfo annotationType) {
            super.annotatedWith(annotationType);
            return this;
        }

        @Override
        public ClassQuery configure(Consumer<ClassConfig> annotationConfig) {
            stream()
                    .map(it -> new ClassConfigImpl(jandexIndex, annotationTransformations.classes,
                            ((ClassInfoImpl) it).jandexDeclaration))
                    .forEach(annotationConfig);
            return this;
        }

    }

    private class MethodConfigQueryImpl extends MethodQueryImpl implements MethodConfigQuery {
        MethodConfigQueryImpl(boolean constructors) {
            super(constructors);
        }

        @Override
        public MethodConfigQuery declaredOn(ClassQuery classes) {
            super.declaredOn(classes);
            return this;
        }

        @Override
        public MethodConfigQuery withReturnType(String type) {
            super.withReturnType(type);
            return this;
        }

        @Override
        public MethodConfigQuery withReturnType(Type type) {
            super.withReturnType(type);
            return this;
        }

        @Override
        public MethodConfigQuery annotatedWith(Class<? extends Annotation> annotationType) {
            super.annotatedWith(annotationType);
            return this;
        }

        @Override
        public MethodConfigQuery annotatedWith(ClassInfo annotationType) {
            super.annotatedWith(annotationType);
            return this;
        }
        @Override
        public MethodQuery configure(Consumer<MethodConfig> annotationConfig) {
            if (requiredDeclarationSites != null) {
                for (ClassQuery requiredDeclarationSite : requiredDeclarationSites) {
                    ((ClassConfigQuery) requiredDeclarationSite).configure(classConfig -> {
                        processMethodInfos(classConfig.methods().stream(), annotationConfig);
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

                processMethodInfos(methodInfos, annotationConfig);
            }
            return this;
        }

        private void processMethodInfos(Stream<MethodInfo> methods, Consumer<MethodConfig> configuration) {
            applyFilters(methods).map(fi -> new MethodConfigImpl(
                    jandexIndex,
                    annotationTransformations.methods,
                    ((MethodInfoImpl) fi).jandexDeclaration)
            ).forEach(configuration);
        }

    }

    private class FieldConfigQueryImpl extends FieldQueryImpl implements FieldConfigQuery {
        @Override
        public FieldConfigQuery declaredOn(ClassQuery classes) {
            super.declaredOn(classes);
            return this;
        }

        @Override
        public FieldConfigQuery ofType(String type) {
            super.ofType(type);
            return this;
        }

        @Override
        public FieldConfigQuery ofType(Type type) {
            super.ofType(type);
            return this;
        }

        @Override
        public FieldConfigQuery annotatedWith(Class<? extends Annotation> annotationType) {
            super.annotatedWith(annotationType);
            return this;
        }

        @Override
        public FieldConfigQuery annotatedWith(ClassInfo annotationType) {
            super.annotatedWith(annotationType);
            return this;
        }

        @Override
        public FieldQuery configure(Consumer<FieldConfig> annotationConfig) {
            if (requiredDeclarationSites != null) {
                for (ClassQuery requiredDeclarationSite : requiredDeclarationSites) {
                    ((ClassConfigQuery) requiredDeclarationSite).configure(classConfig -> {
                        processFieldInfos(classConfig.fields().stream(), annotationConfig);
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

                processFieldInfos(fieldInfo, annotationConfig);
            }

            return this;
        }

        private void processFieldInfos(Stream<FieldInfo> fields, Consumer<FieldConfig> configuration) {
            applyFilters(fields).map(fi -> new FieldConfigImpl(
                    jandexIndex,
                    annotationTransformations.fields,
                    ((FieldInfoImpl) fi).jandexDeclaration)
            ).forEach(configuration);
        }

    }
}
