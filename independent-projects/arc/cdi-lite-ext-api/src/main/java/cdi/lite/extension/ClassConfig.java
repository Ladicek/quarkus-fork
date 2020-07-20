package cdi.lite.extension;

import cdi.lite.extension.model.declarations.AnnotationInfo;
import cdi.lite.extension.model.declarations.ClassInfo;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;

/**
 * @param <T> the configured class
 */
public interface ClassConfig<T> extends ClassInfo<T> {
    void addAnnotation(Class<? extends Annotation> clazz, AnnotationValue... values);

    void removeAnnotation(Predicate<AnnotationInfo> predicate);
}
