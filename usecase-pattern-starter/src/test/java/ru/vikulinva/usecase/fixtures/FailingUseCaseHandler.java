package ru.vikulinva.usecase.fixtures;

import org.springframework.stereotype.Component;
import ru.vikulinva.usecase.UseCaseHandler;

@Component
public class FailingUseCaseHandler implements UseCaseHandler<FailingUseCase, String> {
    @Override
    public String handle(FailingUseCase useCase) {
        throw new IllegalStateException("Failed: " + useCase.input());
    }

    @Override
    public Class<FailingUseCase> useCaseType() {
        return FailingUseCase.class;
    }
}

