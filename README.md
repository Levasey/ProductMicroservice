# ProductMicroservice

Микросервис на **Spring Boot 4** с **Spring Kafka**: создание продукта по HTTP, публикация события `ProductCreatedEvent` в топик `product-created-events-topic` (JSON).

## Требования

- **JDK 17** (в логах у вас может быть другая версия — для сборки заявлена 17 в `pom.xml`)
- **Apache Maven** 3.6+ или **`./mvnw`** в корне репозитория
- **Apache Kafka** (KRaft или ZooKeeper), брокеры из `spring.kafka.bootstrap-servers` в [`application.properties`](src/main/resources/application.properties) (по умолчанию `localhost:9092,localhost:9094`)

## HTTP API

| Метод | Путь | Тело запроса (JSON) | Ответ |
|--------|------|---------------------|--------|
| `PATCH` | `/product` | `title` (string), `price` (number), `quantity` (integer) | **201 Created**, тело — строка `productId` |

При ошибке в обработчике возможен ответ **500** с телом `ErrorMessage` (см. [`ProductController`](src/main/java/com/example/productmicroservice/controller/ProductController.java)).

`server.port=0` — порт Tomcat **случайный** при каждом запуске; смотрите строку вида `Tomcat started on port ...` в логах.

## Быстрый старт

```bash
./mvnw spring-boot:run
```

Или JAR:

```bash
./mvnw clean package
java -jar target/ProductMicroservice-0.0.1-SNAPSHOT.jar
```

Пример запроса:

```bash
curl -s -X PATCH "http://localhost:<PORT>/product" \
  -H "Content-Type: application/json" \
  -d '{"title":"Sample","price":19.99,"quantity":10}'
```

## Kafka

- Топик **`product-created-events-topic`** объявляется бином [`NewTopic`](src/main/java/com/example/productmicroservice/config/KafkaConfig.java): **3 партиции**, **replication factor 1** (подходит для **одного** брокера в разработке).
- При старте приложения **KafkaAdmin** создаёт топик, если его ещё нет (`spring.kafka.admin.auto-create=true`).
- Используйте **`spring.kafka.bootstrap-servers`** (именно **`bootstrap-servers`**, не `bootstrap-server` — иначе Spring Boot может оставить значения по умолчанию и подключение будет не тем, что вы ожидаете).
- Для одного брокера задано **`spring.kafka.producer.acks=1`**, чтобы не требовать полный кворум реплик.

Команды для локальной Kafka (список топиков, consumer и т.д.): [**KAFKA-KOMANDY.md**](KAFKA-KOMANDY.md).

### Частые предупреждения consumer

- **`UNKNOWN_TOPIC_OR_PARTITION`** для партиции `…-1` часто означает, что **consumer запустили до того**, как топик получил нужное число партиций, или топик ещё не создан. Запустите приложение, дождитесь успешного старта, затем снова запустите `kafka-console-consumer`, при необходимости с **новой** `--group` или `--from-beginning`.
- Убедитесь, что **`--bootstrap-server`** у CLI совпадает с тем, куда ходит приложение.

## Postman

В каталоге [`postman/`](postman/) лежат файлы для импорта:

- **`ProductMicroservice.postman_collection.json`** — коллекция с переменной `baseUrl`
- **`Local.postman_environment.json`** — окружение с `baseUrl` (по умолчанию `http://localhost:8080`)

Импорт: **File → Import** в Postman. Подставьте в `baseUrl` реальный URL из логов (из‑за `server.port=0` порт каждый раз новый).

## Зависимости и Spring Boot 4

- Для JSON в HTTP подключён **`spring-boot-starter-json`** (Jackson 3 / `tools.jackson` через Boot).
- Клиент **Kafka 4.x** при настройке продюсера ожидает классы **`com.fasterxml.jackson`**; в `pom.xml` явно добавлен **`com.fasterxml.jackson.core:jackson-databind`**, чтобы не было `ClassNotFoundException` для `TypeReference`.

## Конфигурация

Основной файл: [`src/main/resources/application.properties`](src/main/resources/application.properties).

## Сборка и тесты

```bash
./mvnw verify
```

## Стек

| Компонент | Заметка |
|-----------|---------|
| Spring Boot | 4.0.x (`pom.xml`) |
| Java | 17 |
| spring-boot-starter-kafka | да |
| spring-boot-starter-webmvc | да |
| spring-boot-starter-json | да |
| jackson-databind (com.fasterxml) | явно, для совместимости с Kafka-клиентом |
