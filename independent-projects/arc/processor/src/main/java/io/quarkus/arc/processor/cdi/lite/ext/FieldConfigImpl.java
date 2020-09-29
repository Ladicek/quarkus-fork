package io.quarkus.arc.processor.cdi.lite.ext;

import cdi.lite.extension.model.AnnotationAttribute;
import cdi.lite.extension.model.AnnotationInfo;
import cdi.lite.extension.model.declarations.ClassInfo;
import cdi.lite.extension.phases.enhancement.FieldConfig;
import java.lang.annotation.Annotation;
import java.util.function.Predicate;

class FieldConfigImpl extends FieldInfoImpl implements FieldConfig {
    private final AnnotationsTransformation.Fields transformations;

    FieldConfigImpl(org.jboss.jandex.IndexView jandexIndex, AnnotationsTransformation.Fields transformations,
            org.jboss.jandex.FieldInfo jandexDeclaration) {
        super(jandexIndex, transformations.annotationOverlays, jandexDeclaration);
        this.transformations = transformations;
    }

    @Override
    public FieldConfig addAnnotation(Class<? extends Annotation> annotationType, AnnotationAttribute... attributes) {
        transformations.addAnnotation(jandexDeclaration, annotationType, attributes);
        return this;
    }

    @Override
    public FieldConfig addAnnotation(ClassInfo annotationType, AnnotationAttribute... attributes) {
        transformations.addAnnotation(jandexDeclaration, annotationType, attributes);
        return this;
    }

    @Override
    public FieldConfig addAnnotation(AnnotationInfo annotation) {
        transformations.addAnnotation(jandexDeclaration, annotation);
        return this;
    }

    @Override
    public FieldConfig addAnnotation(Annotation annotation) {
        transformations.addAnnotation(jandexDeclaration, annotation);
        return this;
    }

    @Override
    public FieldConfig removeAnnotation(Predicate<AnnotationInfo> predicate) {
        transformations.removeAnnotation(jandexDeclaration, predicate);
        return this;
    }

    @Override
    public FieldConfig removeAllAnnotations() {
        transformations.removeAllAnnotations(jandexDeclaration);
        return this;
    }
}
