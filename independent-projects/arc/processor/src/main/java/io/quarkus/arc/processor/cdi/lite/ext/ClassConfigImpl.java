package io.quarkus.arc.processor.cdi.lite.ext;

import cdi.lite.extension.ClassConfig;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;


import java.lang.annotation.Annotation;
import java.util.function.Predicate;

class ClassConfigImpl extends ClassInfoImpl implements ClassConfig<Object> {
    // TODO annotation transformer
    ClassConfigImpl(org.jboss.jandex.IndexView jandexIndex, org.jboss.jandex.ClassInfo jandexClass) {
        super(jandexIndex, jandexClass);
    }

    @Override
    public void addAnnotation(Class<? extends Annotation> clazz, AnnotationValue... values) {
        // TODO
    }

    @Override
    public void removeAnnotation(Predicate<AnnotationInstance> predicate) {
        // TODO
    }
}
