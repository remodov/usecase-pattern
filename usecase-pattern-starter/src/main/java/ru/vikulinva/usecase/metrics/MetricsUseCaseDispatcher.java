package ru.vikulinva.usecase.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import ru.vikulinva.usecase.UseCase;
import ru.vikulinva.usecase.UseCaseDispatcher;
import ru.vikulinva.usecase.UseCaseHandler;
import static ru.vikulinva.usecase.constant.UseCaseMetricsConstants.APP_NAME_PROPERTY;
import static ru.vikulinva.usecase.constant.UseCaseMetricsConstants.LABEL_APPLICATION;
import static ru.vikulinva.usecase.constant.UseCaseMetricsConstants.LABEL_USECASE_NAME;
import static ru.vikulinva.usecase.constant.UseCaseMetricsConstants.METRIC_DURATION;
import static ru.vikulinva.usecase.constant.UseCaseMetricsConstants.METRIC_FAILURE;
import static ru.vikulinva.usecase.constant.UseCaseMetricsConstants.METRIC_SUCCESS;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MetricsUseCaseDispatcher extends UseCaseDispatcher {
    private final MeterRegistry registry;
    private final String appName;
    private final ConcurrentMap<Class<?>, Counter> successCounters = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<?>, Counter> failureCounters = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<?>, Timer> timers = new ConcurrentHashMap<>();

    public MetricsUseCaseDispatcher(
            List<UseCaseHandler<? extends UseCase<?>, ?>> handlers,
            MeterRegistry registry,
            Environment environment
    ) {
        super(handlers);
        this.registry = registry;
        this.appName = resolveAppName(environment);
    }

    @Override
    public <R> R dispatch(UseCase<R> useCase) {
        Class<?> useCaseType = useCase.getClass();
        Timer.Sample sample = Timer.start(registry);
        try {
            R result = super.dispatch(useCase);
            handleSuccess(useCaseType).increment();
            return result;
        } catch (Throwable e) {
            handleFailure(useCaseType).increment();
            throw e;
        } finally {
            sample.stop(handleDuration(useCaseType));
        }
    }

    private Counter handleSuccess(Class<?> useCaseType) {
        return successCounters.computeIfAbsent(
                useCaseType,
                type -> Counter.builder(METRIC_SUCCESS)
                        .description("Use case successful executions")
                        .tag(LABEL_USECASE_NAME, type.getSimpleName())
                        .tag(LABEL_APPLICATION, appName)
                        .register(registry)
        );
    }

    private Counter handleFailure(Class<?> useCaseType) {
        return failureCounters.computeIfAbsent(
                useCaseType,
                type -> Counter.builder(METRIC_FAILURE)
                        .description("Use case failed executions")
                        .tag(LABEL_USECASE_NAME, type.getSimpleName())
                        .tag(LABEL_APPLICATION, appName)
                        .register(registry)
        );
    }

    private Timer handleDuration(Class<?> useCaseType) {
        return timers.computeIfAbsent(
                useCaseType,
                type -> Timer.builder(METRIC_DURATION)
                        .description("Use case execution time")
                        .tag(LABEL_USECASE_NAME, type.getSimpleName())
                        .tag(LABEL_APPLICATION, appName)
                        .register(registry)
        );
    }

    private static String resolveAppName(Environment environment) {
        if (environment == null) {
            throw new IllegalStateException("Missing environment for " + APP_NAME_PROPERTY);
        }
        String name = environment.getProperty(APP_NAME_PROPERTY);
        if (StringUtils.isBlank(name)) {
            throw new IllegalStateException("Property " + APP_NAME_PROPERTY + " must be set");
        }
        return name.trim();
    }
}

