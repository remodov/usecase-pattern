package ru.vikulinva.usecase;

@FunctionalInterface
public interface UseCaseStep<I, O> {

    O execute(I input);

}