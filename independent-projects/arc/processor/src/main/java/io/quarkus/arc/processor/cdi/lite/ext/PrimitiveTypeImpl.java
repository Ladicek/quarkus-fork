package io.quarkus.arc.processor.cdi.lite.ext;

import cdi.lite.extension.model.types.PrimitiveType;

class PrimitiveTypeImpl extends TypeImpl<org.jboss.jandex.PrimitiveType> implements PrimitiveType {
    PrimitiveTypeImpl(org.jboss.jandex.IndexView jandexIndex, org.jboss.jandex.PrimitiveType jandexType) {
        super(jandexIndex, jandexType);
    }

    @Override
    public String name() {
        return jandexType.name().toString();
    }
}
