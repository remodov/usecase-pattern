package ru.vikulinva.usecase;

public interface UseCaseHandler<U extends UseCase<R>, R> {

    R handle(U useCase);

    Class<U> useCaseType();

}