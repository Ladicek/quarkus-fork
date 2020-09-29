package cdi.lite.extension.model;

// Graeme comments start
// -------------
// In general I think the design of this API should be revisited, it seems to
// be adapting Jandex which is quite ugly.
//
// In Micronaut we have AnnotationValue (roughly equivalent to AnnotationInfo) and
// AnnotationValueBuilder which provide a fluent API and not this varargs approach
// contained in this interface which seems much more verbose.
//
// See
//
// https://docs.micronaut.io/latest/api/io/micronaut/core/annotation/AnnotationValueBuilder.html
// https://docs.micronaut.io/latest/api/io/micronaut/core/annotation/AnnotationValue.html#builder-java.lang.Class-
// -------------
// Graeme comments end
// TODO "attribute" is a colloquial expression, perhaps use something closer to the JLS?
public interface AnnotationAttribute {
    String name();

    AnnotationAttributeValue value();
}
