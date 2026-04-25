package ru.vikulinva.usecase;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Slf4j
public class UseCaseDispatcher {
    private final Map<Type, UseCaseHandler<? extends UseCase<?>, ?>> handlers;

    public UseCaseDispatcher(List<UseCaseHandler<? extends UseCase<?>, ?>> handlers) {
        this.handlers = Optional.ofNullable(handlers)
                .orElse(Collections.emptyList())
                .stream()
                .collect(toMap(UseCaseHandler::useCaseType, Function.identity()));
    }

    public <R> R dispatch(UseCase<R> useCase) {
        log.info("Dispatching use case {}", useCase);
        try {
            final R result = findHandler(useCase).handle(useCase);
            log.info("Use case {} dispatched successfully", useCase);
            return result;
        } catch (Throwable e) {
            log.error("Failed to dispatch use case %s".formatted(useCase), e);
            throw e;
        }
    }

    private <R> UseCaseHandler<UseCase<R>, R> findHandler(UseCase<R> useCase) {
        return Optional
                .ofNullable(this.handlers.get(useCase.getClass()))
                .map(handler ->
                        (UseCaseHandler<UseCase<R>, R>) handler
                )
                .orElseThrow(
                        () -> new UseCaseNotSupportedException("Use case \"%s\" not supported".formatted(useCase))
                );
    }

}