package cdi.lite.extension.phases.enhancement;

import cdi.lite.extension.model.AnnotationAttribute;
import cdi.lite.extension.model.AnnotationAttributeValue;
import cdi.lite.extension.model.AnnotationInfo;
import cdi.lite.extension.model.declarations.ClassInfo;
import cdi.lite.extension.model.types.Type;

import java.lang.annotation.Annotation;
import java.util.List;

public interface AnnotationAttributes {
    AnnotationAttributeValue value(boolean value);

    AnnotationAttributeValue value(byte value);

    AnnotationAttributeValue value(short value);

    AnnotationAttributeValue value(int value);

    AnnotationAttributeValue value(long value);

    AnnotationAttributeValue value(float value);

    AnnotationAttributeValue value(double value);

    AnnotationAttributeValue value(char value);

    AnnotationAttributeValue value(String value);

    AnnotationAttributeValue value(Enum<?> enumValue);

    AnnotationAttributeValue value(String enumType, String enumValue);

    AnnotationAttributeValue value(ClassInfo enumType, String enumValue);

    AnnotationAttributeValue value(Class<?> value);

    AnnotationAttributeValue value(Type value);

    AnnotationAttributeValue annotationValue(Class<? extends Annotation> annotationType, AnnotationAttribute... attributes);

    AnnotationAttributeValue annotationValue(ClassInfo annotationType, AnnotationAttribute... attributes);

    AnnotationAttributeValue annotationValue(AnnotationInfo annotation);

    AnnotationAttributeValue annotationValue(Annotation annotation);

    AnnotationAttribute attribute(String name, boolean value);

    AnnotationAttribute attribute(String name, byte value);

    AnnotationAttribute attribute(String name, short value);

    AnnotationAttribute attribute(String name, int value);

    AnnotationAttribute attribute(String name, long value);

    AnnotationAttribute attribute(String name, float value);

    AnnotationAttribute attribute(String name, double value);

    AnnotationAttribute attribute(String name, char value);

    AnnotationAttribute attribute(String name, String value);

    AnnotationAttribute attribute(String name, Enum<?> enumValue);

    AnnotationAttribute attribute(String name, String enumType, String enumValue);

    AnnotationAttribute attribute(String name, ClassInfo enumType, String enumValue);

    AnnotationAttribute attribute(String name, Class<?> value);

// Graeme comments start
// -------------
// In Micronaut we have a specific type to represent a class value
// because when writing out metadata we can differentiate and statically load classes
// -------------
// Graeme comments end
    AnnotationAttribute attribute(String name, Type value);

    AnnotationAttribute arrayAttribute(String name, AnnotationAttributeValue... values);

    AnnotationAttribute arrayAttribute(String name, List<AnnotationAttributeValue> values);

    AnnotationAttribute annotationAttribute(String name, Class<? extends Annotation> annotationType,
            AnnotationAttribute... attributes);

    AnnotationAttribute annotationAttribute(String name, ClassInfo annotationType, AnnotationAttribute... attributes);

    AnnotationAttribute annotationAttribute(String name, AnnotationInfo annotation);

    AnnotationAttribute annotationAttribute(String name, Annotation annotation);
}
