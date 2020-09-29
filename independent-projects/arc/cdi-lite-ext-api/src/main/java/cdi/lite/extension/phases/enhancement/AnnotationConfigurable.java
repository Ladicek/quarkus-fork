package cdi.lite.extension.phases.enhancement;

import java.util.function.Consumer;

// Graeme comments start
// -------------
// A new interface that allows registering hooks the be invoked by an
// annotation processor implementation. This class is required for
// an annotation processor based approach because a pull model where you
// ask for all the classes annotated is not possible.
// -------------
// Graeme comments end

/**
 * Represents an annotated component that can be configured.
 * @param <T> The configurable type
 * @param <A> The Annotation config target
 */
@FunctionalInterface
public interface AnnotationConfigurable<T, A extends AnnotationConfig> {
    /**
     * Configure the given annotation values
     * @param annotationConfig The annotation config
     * @return The configurable type
     */
    T configure(Consumer<A> annotationConfig);
}
