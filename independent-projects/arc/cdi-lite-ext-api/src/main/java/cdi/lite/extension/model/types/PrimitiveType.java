package cdi.lite.extension.model.types;

public interface PrimitiveType extends Type {
    String name();

    // ---

    @Override
    default Kind kind() {
        return Kind.PRIMITIVE;
    }
}
