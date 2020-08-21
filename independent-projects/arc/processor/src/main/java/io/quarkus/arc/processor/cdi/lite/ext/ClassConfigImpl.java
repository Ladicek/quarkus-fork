package io.quarkus.arc.processor.cdi.lite.ext;

import cdi.lite.extension.model.AnnotationAttribute;
import cdi.lite.extension.model.AnnotationInfo;
import cdi.lite.extension.model.configs.ClassConfig;
import cdi.lite.extension.model.declarations.ClassInfo;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.function.Predicate;
import org.jboss.jandex.DotName;

class ClassConfigImpl extends ClassInfoImpl implements ClassConfig<Object> {
    private final ClassAnnotationTransformations transformations;

    ClassConfigImpl(org.jboss.jandex.IndexView jandexIndex, org.jboss.jandex.ClassInfo jandexDeclaration,
            ClassAnnotationTransformations transformations) {
        super(jandexIndex, jandexDeclaration);
        this.transformations = transformations;
    }

    @Override
    public void addAnnotation(Class<? extends Annotation> clazz, AnnotationAttribute... attributes) {
        transformations.add(jandexDeclaration.name(), ctx -> {
            org.jboss.jandex.AnnotationValue[] jandexAnnotationAttributes = Arrays.stream(attributes)
                    .map(it -> ((AnnotationAttributeImpl) it).jandexAnnotationAttribute)
                    .toArray(org.jboss.jandex.AnnotationValue[]::new);
            ctx.transform().add(clazz, jandexAnnotationAttributes).done();
        });
    }

    @Override
    public void addAnnotation(ClassInfo<?> clazz, AnnotationAttribute... attributes) {
        transformations.add(jandexDeclaration.name(), ctx -> {
            DotName jandexName = ((ClassInfoImpl) clazz).jandexDeclaration.name();
            org.jboss.jandex.AnnotationValue[] jandexAnnotationAttributes = Arrays.stream(attributes)
                    .map(it -> ((AnnotationAttributeImpl) it).jandexAnnotationAttribute)
                    .toArray(org.jboss.jandex.AnnotationValue[]::new);
            ctx.transform().add(jandexName, jandexAnnotationAttributes).done();
        });
    }

    @Override
    public void addAnnotation(AnnotationInfo annotation) {
        transformations.add(jandexDeclaration.name(), ctx -> {
            ctx.transform().add(((AnnotationInfoImpl) annotation).jandexAnnotation).done();
        });
    }

    @Override
    public void removeAnnotation(Predicate<AnnotationInfo> predicate) {
        transformations.add(jandexDeclaration.name(), ctx -> {
            ctx.transform().remove(new Predicate<org.jboss.jandex.AnnotationInstance>() {
                @Override
                public boolean test(org.jboss.jandex.AnnotationInstance annotationInstance) {
                    return predicate.test(new AnnotationInfoImpl(jandexIndex, annotationInstance));
                }
            }).done();
        });
    }

    @Override
    public void removeAllAnnotations() {
        transformations.add(jandexDeclaration.name(), ctx -> {
            ctx.transform().removeAll().done();
        });
    }
}
