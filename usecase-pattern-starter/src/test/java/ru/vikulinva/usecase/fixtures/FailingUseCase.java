package ru.vikulinva.usecase.fixtures;

import ru.vikulinva.usecase.UseCase;

public record FailingUseCase(String input) implements UseCase<String> {
}

