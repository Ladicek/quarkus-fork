package cdi.lite.extension.model.types;

import java.util.Optional;

public interface WildcardType extends Type {
    Optional<Type> upperBound();

    Optional<Type> lowerBound();

    // ---

    @Override
    default Kind kind() {
        return Kind.WILDCARD_TYPE;
    }
}
