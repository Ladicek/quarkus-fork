package cdi.lite.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An extension is a {@code public}, non-{@code static}, {@code void}-returning method,
 * annotated {@code LiteExtension} and declared on a {@code public} class
 * with a {@code public} zero-parameter constructor. This class must not be a CDI bean.
 * <p>
 * This method can declare arbitrary number of parameters that take one of the following forms:
 * <ul>
 *     <li>{@code TypeConfigurator<MyService>}: configurator for the one exact type</li>
 *     <li>{@code Collection<TypeConfigurator<MyService>>}: configurator for the one exact type, equivalent to previous line</li>
 *     <li>{@code Collection<TypeConfigurator<? extends MyService>>}: configurators for all subtypes</li>
 *     <li>{@code Collection<TypeConfigurator<? super MyService>>}: configurators for all supertypes</li>
 *     <li>{@code Collection<TypeConfigurator<?>>}: configurators for all present types</li>
 *     <li>TODO use {@code Stream} instead of / in addition to {@code Collection}?</li>
 * </ul>
 * <p>
 * Same for {@code MethodConfigurator}, {@code ConstructorConfigurator}, {@code FieldConfigurator}, {@code ParameterConfigurator}.
 * In these cases, the type parameter always expresses a query for the types that declare the configured element.
 * For example, if a class {@code MyService} has 2 methods and each of them has 2 parameters, then
 * {@code Collection<ParameterConfigurator<MyService>>} has 4 elements.
 * <p>
 * It is possible to further narrow down the query by using {@link WithAnnotations} and {@link WithTypes}.
 * <p>
 * For advanced use cases, where this kind of queries is not powerful enough, the extension method can also declare
 * a parameter of type {@link World}. If you declare a parameter of type {@code Collection<TypeConfigurator<?>>},
 * that's a good sign you probably want to use {@code World}.
 * <p>
 * All the parameters will be provided by the container when the extension is invoked.
 * <p>
 * If a class declares multiple extensions, they are all invoked on the same instance of the class.
 * Extension can be assigned a priority via {@link ExtensionPriority}.
 * TODO use @javax.annotation.Priority instead?
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface LiteExtension {
}
