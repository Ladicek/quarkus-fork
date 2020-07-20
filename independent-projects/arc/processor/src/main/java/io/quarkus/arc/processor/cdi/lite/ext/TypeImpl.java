package io.quarkus.arc.processor.cdi.lite.ext;

import cdi.lite.extension.model.declarations.AnnotationInfo;
import cdi.lite.extension.model.types.Type;
import org.jboss.jandex.DotName;

import java.lang.annotation.Annotation;
import java.util.Collection;

abstract class TypeImpl<JandexType extends org.jboss.jandex.Type> implements Type {
    final org.jboss.jandex.IndexView jandexIndex;
    final JandexType jandexType;

    TypeImpl(org.jboss.jandex.IndexView jandexIndex, JandexType jandexType) {
        this.jandexIndex = jandexIndex;
        this.jandexType = jandexType;
    }

    static Type fromJandexType(org.jboss.jandex.IndexView jandexIndex, org.jboss.jandex.Type jandexType) {
        switch (jandexType.kind()) {
            case VOID:
                return new VoidTypeImpl(jandexIndex, jandexType.asVoidType());
            case PRIMITIVE:
                return new PrimitiveTypeImpl(jandexIndex, jandexType.asPrimitiveType());
            case CLASS:
                return new ClassTypeImpl(jandexIndex, jandexType.asClassType());
            case ARRAY:
                return new ArrayTypeImpl(jandexIndex, jandexType.asArrayType());
            case PARAMETERIZED_TYPE:
                return new ParameterizedTypeImpl(jandexIndex, jandexType.asParameterizedType());
            case TYPE_VARIABLE:
                return new TypeVariableImpl(jandexIndex, jandexType.asTypeVariable());
            case WILDCARD_TYPE:
                return new WildcardTypeImpl(jandexIndex, jandexType.asWildcardType());
            default:
                throw new IllegalStateException("Unknown type " + jandexType);
        }
    }

    @Override
    public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
        return jandexType.hasAnnotation(DotName.createSimple(annotationType.getName()));
    }

    @Override
    public AnnotationInfo annotation(Class<? extends Annotation> annotationType) {
        // TODO
        return null;
    }

    @Override
    public Collection<AnnotationInfo> repeatableAnnotation(Class<? extends Annotation> annotationType) {
        // TODO
        return null;
    }

    @Override
    public Collection<AnnotationInfo> annotations() {
        // TODO
        return null;
    }
}
