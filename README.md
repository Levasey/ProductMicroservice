# ProductMicroservice

Микросервис на **Spring Boot** с интеграцией **Apache Kafka**: настроена сериализация значений в JSON для продюсера и при старте объявляется топик `product-created-events-topic` для событий продукта.

## Требования

- **JDK 17**
- **Apache Maven** 3.6+ (или используйте `./mvnw` из корня репозитория)
- **Kafka** (KRaft), доступный по адресам из `application.properties` (по умолчанию `localhost:9092,localhost:9094`)

Топик `product-created-events-topic` создаётся при старте приложения с параметрами: **3 партиции**, **replication factor 3**, `min.insync.replicas=2`. Для этого в кластере должно быть **не меньше трёх брокеров** и корректная конфигурация репликации; иначе создание топика или запись могут завершиться ошибкой.

## Быстрый старт

```bash
./mvnw spring-boot:run
```

Или:

```bash
./mvnw clean package
java -jar target/ProductMicroservice-0.0.1-SNAPSHOT.jar
```

`server.port` в `application.properties` задан как `0` — порт веб-сервера выбирается **случайно** при каждом запуске; актуальный порт смотрите в логах Spring Boot.

## Конфигурация

Основные параметры: [`src/main/resources/application.properties`](src/main/resources/application.properties).

- `spring.kafka.producer.bootstrap-server` — bootstrap-серверы Kafka (через запятую при необходимости).
- Сериализация: ключ — строка, значение — JSON (`JsonSerializer`).

Объявление топика: [`KafkaConfig.java`](src/main/java/com/example/productmicroservice/config/KafkaConfig.java).

## Kafka локально

Пошаговые команды для Linux (KRaft, одна нода или кластер из трёх брокеров): [**KAFKA-KOMANDY.md**](KAFKA-KOMANDY.md).

## Сборка и тесты

```bash
./mvnw verify
```

## Стек

| Компонент        | Версия / заметка   |
|------------------|--------------------|
| Spring Boot      | 4.0.x (см. `pom.xml`) |
| Java             | 17                 |
| spring-boot-starter-kafka | да        |
| spring-boot-starter-webmvc | да       |
