package cdi.lite.extension;

import cdi.lite.extension.model.declarations.ClassInfo;
import cdi.lite.extension.model.types.PrimitiveType;
import cdi.lite.extension.model.types.Type;

public interface Types {
    /**
     * Returns a type from given class literal.
     * For example:
     * <ul>
     * <li>{@code of(void.class)}: same as {@code ofVoid()}</li>
     * <li>{@code of(int.class)}: same as {@code ofPrimitive(PrimitiveKind.INT)}</li>
     * <li>{@code of(String.class)}: same as {@code ofClass(... ClassInfo for String ...)}</li>
     * <li>{@code of(int[].class)}: same as {@code ofArray(ofPrimitive(PrimitiveKind.INT), 1)}</li>
     * <li>{@code of(String[][].class)}: same as {@code ofArray(ofClass(... ClassInfo for String ...), 2)}</li>
     * </ul>
     */
    Type of(Class<?> clazz); // TODO probably remove for the stringly-typed API?

    Type ofVoid();

    Type ofPrimitive(PrimitiveType.PrimitiveKind kind);

    Type ofClass(String clazz);

    Type ofClass(ClassInfo clazz);

    Type ofArray(Type componentType, int dimensions);

    Type parameterized(String parameterizedType, Type... typeArguments);

    Type parameterized(Type parameterizedType, Type... typeArguments);

    /**
     * Equivalent of {@code ? extends upperBound}.
     */
    Type wildcardWithUpperBound(Type upperBound);

    /**
     * Equivalent of {@code ? super lowerBound}.
     */
    Type wildcardWithLowerBound(Type lowerBound);

    /**
     * Equivalent of {@code ?}.
     */
    Type wildcardUnbounded();
}
