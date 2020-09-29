package cdi.lite.extension.phases.enhancement;

import cdi.lite.extension.model.AnnotationAttribute;
import cdi.lite.extension.model.AnnotationInfo;
import cdi.lite.extension.model.declarations.ClassInfo;
import java.lang.annotation.Annotation;
import java.util.function.Predicate;


public interface AnnotationConfig {
    AnnotationConfig addAnnotation(Class<? extends Annotation> annotationType, AnnotationAttribute... attributes);

    AnnotationConfig addAnnotation(ClassInfo annotationType, AnnotationAttribute... attributes);

    AnnotationConfig addAnnotation(AnnotationInfo annotation);

    AnnotationConfig addAnnotation(Annotation annotation);

    AnnotationConfig removeAnnotation(Predicate<AnnotationInfo> predicate);

    AnnotationConfig removeAllAnnotations();
}
