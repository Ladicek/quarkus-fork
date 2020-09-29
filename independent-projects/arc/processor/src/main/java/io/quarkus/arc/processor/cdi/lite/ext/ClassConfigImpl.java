package io.quarkus.arc.processor.cdi.lite.ext;

import cdi.lite.extension.model.AnnotationAttribute;
import cdi.lite.extension.model.AnnotationInfo;
import cdi.lite.extension.model.declarations.ClassInfo;
import cdi.lite.extension.phases.enhancement.ClassConfig;
import java.lang.annotation.Annotation;
import java.util.function.Predicate;

class ClassConfigImpl extends ClassInfoImpl implements ClassConfig {
    private final AnnotationsTransformation.Classes transformations;

    ClassConfigImpl(org.jboss.jandex.IndexView jandexIndex, AnnotationsTransformation.Classes transformations,
            org.jboss.jandex.ClassInfo jandexDeclaration) {
        super(jandexIndex, transformations.annotationOverlays, jandexDeclaration);
        this.transformations = transformations;
    }

    @Override
    public ClassConfig addAnnotation(Class<? extends Annotation> annotationType, AnnotationAttribute... attributes) {
        transformations.addAnnotation(jandexDeclaration, annotationType, attributes);
        return this;
    }

    @Override
    public ClassConfig addAnnotation(ClassInfo annotationType, AnnotationAttribute... attributes) {
        transformations.addAnnotation(jandexDeclaration, annotationType, attributes);
        return this;
    }

    @Override
    public ClassConfig addAnnotation(AnnotationInfo annotation) {
        transformations.addAnnotation(jandexDeclaration, annotation);
        return this;
    }

    @Override
    public ClassConfig addAnnotation(Annotation annotation) {
        transformations.addAnnotation(jandexDeclaration, annotation);
        return this;
    }

    @Override
    public ClassConfig removeAnnotation(Predicate<AnnotationInfo> predicate) {
        transformations.removeAnnotation(jandexDeclaration, predicate);
        return this;
    }

    @Override
    public ClassConfig removeAllAnnotations() {
        transformations.removeAllAnnotations(jandexDeclaration);
        return this;
    }
}
