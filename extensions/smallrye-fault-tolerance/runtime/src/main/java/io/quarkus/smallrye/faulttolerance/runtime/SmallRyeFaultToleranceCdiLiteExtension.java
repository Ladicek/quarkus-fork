package io.quarkus.smallrye.faulttolerance.runtime;

import cdi.lite.extension.Extension;
import cdi.lite.extension.phases.enhancement.AppArchiveConfig;
import io.smallrye.faulttolerance.FaultToleranceBinding;
import io.smallrye.faulttolerance.FaultToleranceExtension;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import javax.annotation.Priority;
import javax.interceptor.Interceptor;
import java.util.Arrays;
import java.util.OptionalInt;

public class SmallRyeFaultToleranceCdiLiteExtension implements Extension {
    @Override
    public void enhance(EnhancementContext context) {
        // Graeme comments start
        // -------------
        // note that in the context of processing source code we can only
        // visit the classes that are being processed and not the whole world
        // In addition I have removed the stream() and find() methods from
        // query since annotation processors work through a push API and events
        // that get triggered by the annotation processor, you cannot ask for
        // all the classes annotated and receive the directly. By adding a configure
        // method this extension registers a hook that will be triggered by the
        // processor at build time.
        // -------------
        // Graeme comments end
        AppArchiveConfig appArchiveConfig = context.archiveConfig();
        Arrays.asList(
                "org.eclipse.microprofile.faulttolerance.Asynchronous",
                "org.eclipse.microprofile.faulttolerance.Bulkhead",
                "org.eclipse.microprofile.faulttolerance.CircuitBreaker",
                "org.eclipse.microprofile.faulttolerance.Fallback",
                "org.eclipse.microprofile.faulttolerance.Retry",
                "org.eclipse.microprofile.faulttolerance.Timeout"
        ).forEach(ann -> appArchiveConfig
                .classes()
                .annotatedWith(ann)
                .configure(classConfig ->
                    classConfig.addAnnotation(FaultToleranceBinding.class)
                )
        );

        appArchiveConfig
                .classes()
                .exactly(appArchiveConfig.types().ofClass("io.smallrye.faulttolerance.FaultToleranceInterceptor"))
                .annotatedWith(Interceptor.class)
                .configure(annotationConfig -> {
                    Config config = ConfigProvider.getConfig();
                    OptionalInt priority = config.getValue("mp.fault.tolerance.interceptor.priority", OptionalInt.class);
                    if (priority.isPresent()) {
                        annotationConfig
                                .removeAnnotation(it -> it.name().equals(Priority.class.getName()))
                                .addAnnotation(new FaultToleranceExtension.PriorityLiteral(priority.getAsInt()));
                    }
                });
    }
}
