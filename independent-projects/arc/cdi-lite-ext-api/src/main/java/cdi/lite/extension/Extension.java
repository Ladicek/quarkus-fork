package cdi.lite.extension;

import cdi.lite.extension.phases.discovery.AppArchiveBuilder;
import cdi.lite.extension.phases.discovery.Contexts;
import cdi.lite.extension.phases.enhancement.AppArchiveConfig;
import cdi.lite.extension.phases.synthesis.SyntheticComponents;
import cdi.lite.extension.phases.validation.Messages;

// Graeme comments start
// -------------
// The current proposed extension model places a lot of burden in Extension discovery
// and binding. This would adversely impact compiler performance and it is much better
// to have a simple interface that can be implemented and registered by service loader.
// -------------
// Graeme comments end
/**
 * Defines the contract for a CDI lite extension.
 *
 * CDI lite extensions are registered by {@link java.util.ServiceLoader} which
 * allows lightweight discovery.
 *
 * Extensions can be annotated with {@link ExtensionPriority} to define ordering.
 */
public interface Extension {

    // Graeme comments start
    // -------------
    // There is no equivalent in Micronaut for this phase, but I can see it being
    // called at the annotation processor init phase to register classes that you want to import and custom scopes
    // -------------
    // Graeme comments end
    /**
     * 1st phase of CDI Lite extension processing.
     * Allow registering additional classes to become part of the application.
     * Also allows registering custom CDI contexts.
     *
     * @param context The {@link DiscoveryContext}
     */
    default void discover(DiscoveryContext context) {}

    // Graeme comments start
    // -------------
    // This phase is equivalent to Micronaut's type element visitors which are
    // very similar in purpose. For example here is the visitor that adds the controller
    // annotation to a JAX-RS resource:
    // https://github.com/micronaut-projects/micronaut-jaxrs/blob/master/jaxrs-processor/src/main/java/io/micronaut/jaxrs/processor/JaxRsTypeElementVisitor.java#L52
    // -------------
    // Graeme comments end
    /**
     * 2nd phase of CDI Lite extension processing.
     * Allows transforming annotations.
     *
     * @param context The enhancement context
     */
    default void enhance(EnhancementContext context) {}

    // Graeme comments start
    // -------------
    // There is no end user hook for this phase, but this is where Micronaut
    // would write out the byte code for the definition bean definitions and
    // I can see it being useful for users to hook into that process and
    // add arbitrary bean definitions to be included in synthesis.
    // -------------
    // Graeme comments end
    /**
     * 3rd phase of CDI Lite extension processing.
     * Allows registering synthetic beans and observers.

     * @param context The {@link SynthesisContext}
     */
    default void synthesize(SynthesisContext context) {}

    // Graeme comments start
    // -------------
    // There is no end user hook for this phase, but I guess if you add a
    // customizable synthesis phase this would be necessary.
    // In general error reporting is done at each phase in Micronaut so
    // that errors can be reported against the originating element and can
    // result in compilation errors.
    // -------------
    // Graeme comments end
    /**
     * 4th phase of CDI Lite extension processing.
     * Allows custom validation.
     * @param context The {@link ValidationContext}
     */
    default void validate(ValidationContext context) {}


    /**
     * The discovery context is passed to the {@link #discover(DiscoveryContext)} phase
     * of an extension and allows adding additional contexts through the {@link Contexts} interface as well as additional classes.
     */
    interface DiscoveryContext {
        /**
         * @return The classes
         */
        AppArchiveBuilder classes();
        /**
         * @return The contexts
         */
        Contexts contexts();
    }

    /**
     * The enhancement context is passed to the {@link #enhance(EnhancementContext)} phase
     * of an extension and allows modifying annotations.
     */
    interface EnhancementContext {
        /**
         * @return The archive config.
         */
        AppArchiveConfig archiveConfig();

        /**
         * @return The messages object to allow error reporting
         */
        Messages messages();
    }

    /**
     * The synthesis context is passed to the {@link #synthesize(SynthesisContext)} phase
     * and allows synthesizing new beans.
     */
    interface SynthesisContext {
        /**
         * @return The archive
         */
        AppArchive archive();

        /**
         * @return The deployment
         */
        AppDeployment deployment();

        /**
         * @return The synthetic components
         */
        SyntheticComponents components();

        /**
         * @return The messages object to allow for error reporting
         */
        Messages messages();
    }

    /**
     * The validation context is passed to the {@link #validate(ValidationContext)} phase
     * and allows a final pass at validation.
     */
    interface ValidationContext {
        /**
         * @return The archive
         */
        AppArchive archive();

        /**
         * @return The deployment
         */
        AppDeployment deployment();

        /**
         * @return The messages object to allow for error reporting
         */
        Messages messages();
    }
}
