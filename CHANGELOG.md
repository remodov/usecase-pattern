# Изменения

## 1.1.0
- Добавлен пакет `cqrs`: маркерные интерфейсы `UseCaseCommand<R>` и `UseCaseQuery<R>` (extends `UseCase<R>`)

## 1.0.3
- Разделены версии core и starter через `libs.versions.toml`
- Добавлены метрики Micrometer с тегами `application` и `usecase_name`
- Добавлена проверка `spring.application.name` при старте

## previous
- Базовый usecase pattern: dispatcher, handler и Spring Boot starter
