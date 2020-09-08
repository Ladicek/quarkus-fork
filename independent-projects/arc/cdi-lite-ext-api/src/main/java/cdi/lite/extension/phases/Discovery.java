package cdi.lite.extension.phases;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 1st phase of CDI Lite extension processing.
 * Allow registering additional classes to become part of the application.
 * Also allows registering custom CDI contexts.
 * <p>
 * Extensions annotated {@code @Discovery} can define parameters of these types:
 * <ul>
 * <li>{@link cdi.lite.extension.phases.discovery.AppArchiveBuilder AppArchiveBuilder}: to add classes to application</li>
 * <li>{@link cdi.lite.extension.phases.discovery.Contexts Contexts}: to register custom contexts</li>
 * </ul>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Discovery {
}
