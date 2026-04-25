package ru.vikulinva.usecase.fixtures;

import org.springframework.stereotype.Component;
import ru.vikulinva.usecase.UseCaseHandler;

@Component
public class TestUseCaseHandler implements UseCaseHandler<TestUseCase, String> {

    @Override
    public Class<TestUseCase> useCaseType() {
        return TestUseCase.class;
    }

    @Override
    public String handle(TestUseCase useCase) {
        return "Processed: " + useCase.input();
    }
}

