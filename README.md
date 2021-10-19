# Reactive Java Homework #2

![GitHub Classroom Workflow](../../workflows/GitHub%20Classroom%20Workflow/badge.svg?branch=master)

## Readers-Writers problem

### Формулировка

На основе задачи Читателей-Писателей
реализовать [ReadWriteList](src/main/java/ru/innotech/education/rxjava/reader.writer/ReadWriteList.java). Несколько
потоков имеют параллельный доступ для чтения, но писать в один момент времени может только один.

* Реализовать обработку аннотаций `@ReadOperation` и `@WriteOperation`:
    * `@ReadOperation` – несколько потоков могут выполнять этот метод параллельно.
    * `@WriteOperation` – только один поток в один момент времени может выполнять этот метод.
* Для реализации можно использовать `cglib`.
* Для реализации блокировок можно использовать _только_ `java.util.concurrent.Semaphore`.

### Сборка и прогон тестов

```shell
./gradlew clean test
```

### Прием домашнего задания

Автоматический прогон тесто запускается из ветки master.

Как только тесты будут успешно пройдены, в Github Classroom на dashboard появится отметка об успешной сдаче. Так же в
самом репозитории появится бейдж со статусом сборки.