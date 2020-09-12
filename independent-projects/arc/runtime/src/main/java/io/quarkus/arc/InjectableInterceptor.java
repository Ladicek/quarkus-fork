package io.quarkus.arc;

import jakarta.enterprise.inject.spi.Interceptor;
import jakarta.enterprise.inject.spi.Prioritized;

/**
 * Quarkus representation of an interceptor bean.
 * This interface extends the standard CDI {@link Interceptor} interface.
 *
 * @author Martin Kouba
 *
 * @param <T>
 */
public interface InjectableInterceptor<T> extends InjectableBean<T>, Interceptor<T>, Prioritized {

    @Override
    default Kind getKind() {
        return Kind.INTERCEPTOR;
    }

}
