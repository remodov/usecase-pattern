# usecase-pattern

Java-библиотека для реализации Use Case Pattern в Spring Boot приложениях. Тонкий слой над `UseCase` / `UseCaseHandler` / `UseCaseDispatcher` со стартером Spring Boot и метриками Micrometer.

Подробное описание методологии — [vikulin-va.ru/use-case-pattern](https://vikulin-va.ru/use-case-pattern/).

## Модули

- `usecase-pattern` — базовые интерфейсы и контракты, без Spring.
- `usecase-pattern-starter` — авто-конфигурация Spring Boot + метрики Micrometer (success/failure/duration на каждый UseCase).

## Пакеты

### `ru.vikulinva.usecase` — ядро

| Класс | Описание |
|-------|----------|
| `UseCase<R>` | Маркерный интерфейс для всех use case. `R` — тип результата |
| `UseCaseHandler<U, R>` | Обработчик use case: `handle()` + `useCaseType()` |
| `UseCaseDispatcher` | Маршрутизация use case → handler через Spring DI |
| `UseCaseEmptyResult` | Результат-заглушка для команд без возвращаемого значения |
| `UseCaseStep<I, O>` | Функциональный интерфейс для декомпозиции хэндлера на шаги |
| `UseCaseStepEmptyResult` | Результат-заглушка для шагов без возвращаемого значения |
| `UseCaseNotSupportedException` | Ошибка, если для use case не найден handler |

### `ru.vikulinva.usecase.cqrs` — CQRS-расширение

Разделение команд и запросов на уровне типов.

| Класс | Описание |
|-------|----------|
| `UseCaseCommand<R>` | Маркер для команд (запись). Extends `UseCase<R>` |
| `UseCaseQuery<R>` | Маркер для запросов (чтение). Extends `UseCase<R>` |

```java
// Command — мутация данных, @Transactional
public record CreateOrderCommand(
        UUID customerId, List<OrderItem> items
) implements UseCaseCommand<CreateOrderResult> {}

// Query — чтение данных, @Transactional(readOnly = true)
public record GetOrderByIdQuery(
        UUID orderId
) implements UseCaseQuery<OrderJson> {}
```

## Подключение стартера

### Gradle (Kotlin DSL)

```kotlin
repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/remodov/usecase-pattern")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("ru.vikulinva:usecase-pattern-starter:1.1.0")
    // для экспорта метрик в Prometheus
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
}
```

## Использование

### 1) Описать UseCase

```java
public record CreateOrderUseCase(String orderId) implements UseCase<String> {}
```

### 2) Реализовать handler

```java
@Component
public class CreateOrderHandler implements UseCaseHandler<CreateOrderUseCase, String> {

    @Override
    public String handle(CreateOrderUseCase useCase) {
        return "created:" + useCase.orderId();
    }

    @Override
    public Class<CreateOrderUseCase> useCaseType() {
        return CreateOrderUseCase.class;
    }
}
```

### 3) Вызов через dispatcher

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    private final UseCaseDispatcher dispatcher;

    public String create(String orderId) {
        return dispatcher.dispatch(new CreateOrderUseCase(orderId));
    }
}
```

## Метрики

Метрики создаются для каждого usecase автоматически:

- `usecase_success_total`
- `usecase_failure_total`
- `usecase_duration_seconds`

Теги:

- `usecase_name` — простое имя класса UseCase
- `application` — значение `spring.application.name`

`spring.application.name` обязателен. Если свойство не задано, приложение не стартует.

### Пример в Prometheus

```
usecase_success_total{usecase_name="CreateOrderUseCase",application="orders-service"} 1
usecase_failure_total{usecase_name="CreateOrderUseCase",application="orders-service"} 0
usecase_duration_seconds_sum{usecase_name="CreateOrderUseCase",application="orders-service"} 0.056610082
```

## Как собрать локально

```bash
./gradlew build
```

## Лицензия

MIT
