package cdi.lite.extension;

import cdi.lite.extension.model.declarations.DeclarationInfo;
import java.util.function.Consumer;

@FunctionalInterface
public interface DeclarationInfoProcessor<T, A extends DeclarationInfo> {
    /**
     * Configure the given annotation values
     * @param annotationConfig The annotation config
     * @return The configurable type
     */
    T process(Consumer<A> annotationConfig);
}
