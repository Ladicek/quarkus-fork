package io.quarkus.smallrye.faulttolerance.deployment;

import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanDefiningAnnotationBuildItem;
import io.quarkus.arc.processor.BuiltinScope;
import io.quarkus.deployment.Feature;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.ConfigurationTypeBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.SystemPropertyBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;
import io.quarkus.deployment.metrics.MetricsCapabilityBuildItem;
import io.smallrye.faulttolerance.core.timer.TimerRunnableWrapper;
import io.smallrye.faulttolerance.internal.RequestContextControllerProvider;
import io.smallrye.faulttolerance.propagation.ContextPropagationRequestContextControllerProvider;
import io.smallrye.faulttolerance.propagation.ContextPropagationTimerRunnableWrapper;

public class SmallRyeFaultToleranceProcessor {

    private static final Set<DotName> FT_ANNOTATIONS = new HashSet<>();
    static {
        // @Blocking and @NonBlocking alone do _not_ trigger the fault tolerance interceptor,
        // only in combination with other fault tolerance annotations
        FT_ANNOTATIONS.add(DotName.createSimple(Asynchronous.class.getName()));
        FT_ANNOTATIONS.add(DotName.createSimple(Bulkhead.class.getName()));
        FT_ANNOTATIONS.add(DotName.createSimple(CircuitBreaker.class.getName()));
        FT_ANNOTATIONS.add(DotName.createSimple(Fallback.class.getName()));
        FT_ANNOTATIONS.add(DotName.createSimple(Retry.class.getName()));
        FT_ANNOTATIONS.add(DotName.createSimple(Timeout.class.getName()));
    }

    @BuildStep
    public void build(BuildProducer<FeatureBuildItem> feature,
            BuildProducer<AdditionalBeanBuildItem> additionalBean,
            BuildProducer<ServiceProviderBuildItem> serviceProvider,
            BuildProducer<BeanDefiningAnnotationBuildItem> additionalBda,
            Optional<MetricsCapabilityBuildItem> metricsCapability,
            BuildProducer<SystemPropertyBuildItem> systemProperty,
            CombinedIndexBuildItem combinedIndexBuildItem,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass) {

        feature.produce(new FeatureBuildItem(Feature.SMALLRYE_FAULT_TOLERANCE));

        serviceProvider.produce(new ServiceProviderBuildItem(RequestContextControllerProvider.class.getName(),
                ContextPropagationRequestContextControllerProvider.class.getName()));
        serviceProvider.produce(new ServiceProviderBuildItem(TimerRunnableWrapper.class.getName(),
                ContextPropagationTimerRunnableWrapper.class.getName()));

        IndexView index = combinedIndexBuildItem.getIndex();

        // Add reflective acccess to fallback handlers
        Set<String> fallbackHandlers = new HashSet<>();
        for (ClassInfo implementor : index
                .getAllKnownImplementors(DotName.createSimple(FallbackHandler.class.getName()))) {
            fallbackHandlers.add(implementor.name().toString());
        }
        if (!fallbackHandlers.isEmpty()) {
            AdditionalBeanBuildItem.Builder fallbackHandlersBeans = AdditionalBeanBuildItem.builder()
                    .setDefaultScope(BuiltinScope.DEPENDENT.getName());
            for (String fallbackHandler : fallbackHandlers) {
                reflectiveClass.produce(new ReflectiveClassBuildItem(true, false, fallbackHandler));
                fallbackHandlersBeans.addBeanClass(fallbackHandler);
            }
            additionalBean.produce(fallbackHandlersBeans.build());
        }

        for (DotName annotation : FT_ANNOTATIONS) {
            reflectiveClass.produce(new ReflectiveClassBuildItem(true, false, annotation.toString()));
            // also make them bean defining annotations
            additionalBda.produce(new BeanDefiningAnnotationBuildItem(annotation));
        }

        if (!metricsCapability.isPresent()) {
            //disable fault tolerance metrics with the MP sys props
            systemProperty.produce(new SystemPropertyBuildItem("MP_Fault_Tolerance_Metrics_Enabled", "false"));
        }
    }

    @BuildStep
    public ConfigurationTypeBuildItem registerTypes() {
        return new ConfigurationTypeBuildItem(ChronoUnit.class);
    }
}
