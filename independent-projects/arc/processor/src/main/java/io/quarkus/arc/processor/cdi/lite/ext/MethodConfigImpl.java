package io.quarkus.arc.processor.cdi.lite.ext;

import cdi.lite.extension.model.AnnotationAttribute;
import cdi.lite.extension.model.AnnotationInfo;
import cdi.lite.extension.model.declarations.ClassInfo;
import cdi.lite.extension.phases.enhancement.MethodConfig;
import java.lang.annotation.Annotation;
import java.util.function.Predicate;

class MethodConfigImpl extends MethodInfoImpl implements MethodConfig {
    private final AnnotationsTransformation.Methods transformations;

    MethodConfigImpl(org.jboss.jandex.IndexView jandexIndex, AnnotationsTransformation.Methods transformations,
            org.jboss.jandex.MethodInfo jandexDeclaration) {
        super(jandexIndex, transformations.annotationOverlays, jandexDeclaration);
        this.transformations = transformations;
    }

    @Override
    public MethodConfig addAnnotation(Class<? extends Annotation> annotationType, AnnotationAttribute... attributes) {
        transformations.addAnnotation(jandexDeclaration, annotationType, attributes);
        return this;
    }

    @Override
    public MethodConfig addAnnotation(ClassInfo annotationType, AnnotationAttribute... attributes) {
        transformations.addAnnotation(jandexDeclaration, annotationType, attributes);
        return this;
    }

    @Override
    public MethodConfig addAnnotation(AnnotationInfo annotation) {
        transformations.addAnnotation(jandexDeclaration, annotation);
        return this;
    }

    @Override
    public MethodConfig addAnnotation(Annotation annotation) {
        transformations.addAnnotation(jandexDeclaration, annotation);
        return this;
    }

    @Override
    public MethodConfig removeAnnotation(Predicate<AnnotationInfo> predicate) {
        transformations.removeAnnotation(jandexDeclaration, predicate);
        return this;
    }

    @Override
    public MethodConfig removeAllAnnotations() {
        transformations.removeAllAnnotations(jandexDeclaration);
        return this;
    }
}
