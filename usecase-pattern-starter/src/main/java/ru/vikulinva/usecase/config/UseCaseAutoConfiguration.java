package ru.vikulinva.usecase.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.core.env.Environment;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.vikulinva.usecase.UseCase;
import ru.vikulinva.usecase.UseCaseDispatcher;
import ru.vikulinva.usecase.UseCaseHandler;
import ru.vikulinva.usecase.metrics.MetricsUseCaseDispatcher;

import java.util.List;

@Configuration
public class UseCaseAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(UseCaseDispatcher.class)
    public UseCaseDispatcher useCaseMetricsDispatcher(
            List<UseCaseHandler<? extends UseCase<?>, ?>> handlers,
            MeterRegistry registry,
            Environment environment
    ) {
        return new MetricsUseCaseDispatcher(handlers, registry, environment);
    }
}

