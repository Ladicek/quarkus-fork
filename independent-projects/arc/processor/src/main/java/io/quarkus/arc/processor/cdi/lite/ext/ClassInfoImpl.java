package io.quarkus.arc.processor.cdi.lite.ext;

import cdi.lite.extension.model.declarations.AnnotationInfo;
import cdi.lite.extension.model.declarations.ClassInfo;
import cdi.lite.extension.model.declarations.FieldInfo;
import cdi.lite.extension.model.declarations.MethodInfo;
import cdi.lite.extension.model.declarations.PackageInfo;
import cdi.lite.extension.model.types.Type;
import org.jboss.jandex.DotName;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

class ClassInfoImpl implements ClassInfo<Object> {
    private final org.jboss.jandex.IndexView jandexIndex;
    private final org.jboss.jandex.ClassInfo jandexClass;

    ClassInfoImpl(org.jboss.jandex.IndexView jandexIndex, org.jboss.jandex.ClassInfo jandexClass) {
        this.jandexIndex = jandexIndex;
        this.jandexClass = jandexClass;
    }

    @Override
    public String name() {
        return jandexClass.name().toString();
    }

    @Override
    public String simpleName() {
        return jandexClass.simpleName();
    }

    @Override
    public PackageInfo _package() {
        String fqn = jandexClass.name().toString();
        int lastDot = fqn.lastIndexOf('.');
        return new PackageInfoImpl(fqn.substring(0, lastDot));
    }

    @Override
    public ClassInfo<?> superClass() {
        DotName superClassName = jandexClass.superName();
        org.jboss.jandex.ClassInfo superClass = jandexIndex.getClassByName(superClassName);
        if (superClass == null) {
            throw new IllegalStateException("Class " + superClassName + " not found in Jandex");
        }
        return new ClassInfoImpl(jandexIndex, superClass);
    }

    @Override
    public List<ClassInfo<?>> superInterfaces() {
        return jandexClass.interfaceNames()
                .stream()
                .map(jandexIndex::getClassByName)
                .map(it -> new ClassInfoImpl(jandexIndex, it))
                .collect(Collectors.toList());
    }

    @Override
    public Type superClassType() {
        return null;
    }

    @Override
    public Collection<Type> superInterfacesTypes() {
        return null;
    }

    @Override
    public boolean isClass() {
        // TODO there must be a better way
        return !isInterface() && !isEnum() && !isAnnotation();
    }

    @Override
    public boolean isInterface() {
        return Modifier.isInterface(jandexClass.flags());
    }

    @Override
    public boolean isEnum() {
        return jandexClass.isEnum();
    }

    @Override
    public boolean isAnnotation() {
        return jandexClass.isAnnotation();
    }

    @Override
    public Collection<MethodInfo<?>> constructors() {
        return null;
    }

    @Override
    public Collection<MethodInfo<?>> methods() {
        return null;
    }

    @Override
    public Collection<FieldInfo<?>> fields() {
        return null;
    }

    @Override
    public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
        return jandexClass.classAnnotation(DotName.createSimple(annotationType.getName())) != null;
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
