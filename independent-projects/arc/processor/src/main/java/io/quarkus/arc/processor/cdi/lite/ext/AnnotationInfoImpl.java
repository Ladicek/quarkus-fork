package io.quarkus.arc.processor.cdi.lite.ext;

import cdi.lite.extension.model.declarations.AnnotationAttributeInfo;
import cdi.lite.extension.model.declarations.AnnotationAttributeValue;
import cdi.lite.extension.model.declarations.AnnotationInfo;
import cdi.lite.extension.model.declarations.ClassInfo;
import cdi.lite.extension.model.declarations.DeclarationInfo;
import org.jboss.jandex.DotName;

import java.util.Collection;

class AnnotationInfoImpl implements AnnotationInfo {
    private final org.jboss.jandex.IndexView jandexIndex;
    private final org.jboss.jandex.AnnotationInstance jandexAnnotationInstance;

    AnnotationInfoImpl(org.jboss.jandex.IndexView jandexIndex, org.jboss.jandex.AnnotationInstance jandexAnnotationInstance) {
        this.jandexIndex = jandexIndex;
        this.jandexAnnotationInstance = jandexAnnotationInstance;
    }

    @Override
    public DeclarationInfo target() {
        org.jboss.jandex.AnnotationTarget target = jandexAnnotationInstance.target();
        return null;
    }

    @Override
    public ClassInfo<?> type() {
        DotName annotationClassName = jandexAnnotationInstance.name();
        org.jboss.jandex.ClassInfo annotationClass = jandexIndex.getClassByName(annotationClassName);
        if (annotationClass == null) {
            throw new IllegalStateException("Class " + annotationClassName + " not found in Jandex");
        }
        return new ClassInfoImpl(jandexIndex, annotationClass);
    }

    @Override
    public Collection<AnnotationAttributeInfo> attributes() {
        return null;
    }

    @Override
    public boolean hasAttribute(String name) {
        return false;
    }

    @Override
    public AnnotationAttributeValue attribute(String name) {
        return null;
    }
}
