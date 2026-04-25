package ru.vikulinva.usecase.testconfig;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class TestMetricsApplication {
    @Bean
    MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }
}

