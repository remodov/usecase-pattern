package ru.vikulinva.usecase.integration;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.vikulinva.usecase.UseCaseDispatcher;
import ru.vikulinva.usecase.fixtures.FailingUseCase;
import ru.vikulinva.usecase.fixtures.FailingUseCaseHandler;
import ru.vikulinva.usecase.fixtures.TestUseCase;
import ru.vikulinva.usecase.fixtures.TestUseCaseHandler;
import ru.vikulinva.usecase.testconfig.TestMetricsApplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
        classes = {TestMetricsApplication.class, TestUseCaseHandler.class, FailingUseCaseHandler.class},
        properties = "spring.application.name=test-service"
)
class UseCaseMetricsIntegrationTest {

    @Autowired
    private UseCaseDispatcher dispatcher;

    @Autowired
    private MeterRegistry meterRegistry;

    @Test
    void shouldPublishSuccessFailureAndTimingMetrics() {
        dispatcher.dispatch(new TestUseCase("ok"));

        assertThrows(IllegalStateException.class, () -> dispatcher.dispatch(new FailingUseCase("boom")));

        String successUseCase = TestUseCase.class.getSimpleName();
        String failingUseCase = FailingUseCase.class.getSimpleName();
        String appName = "test-service";

        double successCount = meterRegistry.get("usecase_success_total")
                .tag("usecase_name", successUseCase)
                .tag("application", appName)
                .counter()
                .count();

        double failureCount = meterRegistry.get("usecase_failure_total")
                .tag("usecase_name", failingUseCase)
                .tag("application", appName)
                .counter()
                .count();

        long successTimerCount = meterRegistry.get("usecase_duration_seconds")
                .tag("usecase_name", successUseCase)
                .tag("application", appName)
                .timer()
                .count();

        long failureTimerCount = meterRegistry.get("usecase_duration_seconds")
                .tag("usecase_name", failingUseCase)
                .tag("application", appName)
                .timer()
                .count();

        assertThat(successCount).isEqualTo(1.0);
        assertThat(failureCount).isEqualTo(1.0);
        assertThat(successTimerCount).isEqualTo(1);
        assertThat(failureTimerCount).isEqualTo(1);
    }
}

