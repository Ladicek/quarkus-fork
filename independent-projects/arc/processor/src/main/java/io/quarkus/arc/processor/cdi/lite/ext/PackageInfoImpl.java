package io.quarkus.arc.processor.cdi.lite.ext;

import cdi.lite.extension.model.declarations.AnnotationInfo;
import cdi.lite.extension.model.declarations.PackageInfo;

import java.lang.annotation.Annotation;
import java.util.Collection;

class PackageInfoImpl implements PackageInfo {
    private final String name;

    PackageInfoImpl(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    // TODO Jandex doesn't capture package annotations

    @Override
    public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
        return false;
    }

    @Override
    public AnnotationInfo annotation(Class<? extends Annotation> annotationType) {
        return null;
    }

    @Override
    public Collection<AnnotationInfo> repeatableAnnotation(Class<? extends Annotation> annotationType) {
        return null;
    }

    @Override
    public Collection<AnnotationInfo> annotations() {
        return null;
    }
}
