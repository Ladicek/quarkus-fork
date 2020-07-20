package cdi.lite.extension.model.declarations;

public interface AnnotationAttributeValue {
    enum Kind {
        BOOLEAN,
        BYTE,
        SHORT,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        CHAR,
        STRING,
        ENUM,
        CLASS,
        ARRAY,
        NESTED_ANNOTATION,
    }

    Kind kind(); // TODO perhaps add isBoolean/isByte/... methods instead? like in DeclarationInfo

    boolean asBoolean();
    byte asByte();
    short asShort();
    int asInt();
    long asLong();
    float asFloat();
    double asDouble();
    char asChar();
    String asString();
    Enum<?> asEnum(); // TODO perhaps return enum name + enum type instead?
    ClassInfo<?> asClass();
    AnnotationAttributeValue[] asArray();
    AnnotationInfo asNestedAnnotation();
}
