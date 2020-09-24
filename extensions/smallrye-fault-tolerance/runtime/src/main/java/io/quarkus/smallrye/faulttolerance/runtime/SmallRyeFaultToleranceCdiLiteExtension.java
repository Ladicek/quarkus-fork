package io.quarkus.smallrye.faulttolerance.runtime;

import java.util.OptionalInt;

import javax.annotation.Priority;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import cdi.lite.extension.ExactType;
import cdi.lite.extension.phases.Enhancement;
import cdi.lite.extension.phases.enhancement.Annotations;
import cdi.lite.extension.phases.enhancement.AppArchiveConfig;
import cdi.lite.extension.phases.enhancement.ClassConfig;
import io.smallrye.faulttolerance.FaultToleranceBinding;
import io.smallrye.faulttolerance.FaultToleranceExtension;

public class SmallRyeFaultToleranceCdiLiteExtension {
    @Enhancement
    public void process(
            @ExactType("org.eclipse.microprofile.faulttolerance.Asynchronous") ClassConfig asynchronousClass,
            @ExactType("org.eclipse.microprofile.faulttolerance.Bulkhead") ClassConfig bulkheadClass,
            @ExactType("org.eclipse.microprofile.faulttolerance.CircuitBreaker") ClassConfig circuitBreakerClass,
            @ExactType("org.eclipse.microprofile.faulttolerance.Fallback") ClassConfig fallbackClass,
            @ExactType("org.eclipse.microprofile.faulttolerance.Retry") ClassConfig retryClass,
            @ExactType("org.eclipse.microprofile.faulttolerance.Timeout") ClassConfig timeoutClass,
            @ExactType("io.smallrye.faulttolerance.FaultToleranceInterceptor") ClassConfig ftInterceptorClass,
            Annotations ann,
            AppArchiveConfig app) {

        asynchronousClass.addAnnotation(FaultToleranceBinding.class);
        bulkheadClass.addAnnotation(FaultToleranceBinding.class);
        circuitBreakerClass.addAnnotation(FaultToleranceBinding.class);
        fallbackClass.addAnnotation(FaultToleranceBinding.class);
        retryClass.addAnnotation(FaultToleranceBinding.class);
        timeoutClass.addAnnotation(FaultToleranceBinding.class);

        // alternative:
//        app.classes()
//                .exactly("org.eclipse.microprofile.faulttolerance.Asynchronous")
//                .exactly("org.eclipse.microprofile.faulttolerance.Bulkhead")
//                .exactly("org.eclipse.microprofile.faulttolerance.CircuitBreaker")
//                .exactly("org.eclipse.microprofile.faulttolerance.Fallback")
//                .exactly("org.eclipse.microprofile.faulttolerance.Retry")
//                .exactly("org.eclipse.microprofile.faulttolerance.Timeout")
//                .configure()
//                .forEach(it -> it.addAnnotation(new FaultToleranceBinding.Literal()));

        // in the original Quarkus extension, this runs later (the build step consumes BeanArchiveIndexBuildItem)
        // do we need to find a way how to express that in CDI Lite Extensions?
        Config config = ConfigProvider.getConfig();
        OptionalInt priority = config.getValue("mp.fault.tolerance.interceptor.priority", OptionalInt.class);
        if (priority.isPresent()) {
            ftInterceptorClass.removeAnnotation(it -> it.name().equals(Priority.class.getName()));
            ftInterceptorClass.addAnnotation(Priority.class, ann.attribute("value", priority.getAsInt()));
            // alternative:
//            ftInterceptorClass.addAnnotation(new FaultToleranceExtension.PriorityLiteral(priority.getAsInt()));
        }
    }
}
