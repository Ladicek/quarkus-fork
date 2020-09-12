package io.quarkus.arc.impl;

import jakarta.inject.Singleton;
import java.lang.annotation.Annotation;

class SingletonContext extends AbstractSharedContext {

    @Override
    public Class<? extends Annotation> getScope() {
        return Singleton.class;
    }

}
