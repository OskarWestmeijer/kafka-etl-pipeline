# kafka-messaging-sample

Explores Kafka messaging with Spring-Boot.

## Local development

Kafka UI available on [localhost:8081](http://localhost:8081)

Prometheus UI available on [localhost:9090](http://localhost:9090)

```
docker-compose up -d
./gradlew bootRun
curl -X GET localhost:8080/ping
curl -X POST localhost:8080/products -H "Content-Type: application/json" -d '{"id":1234,"name":"Effective Java"}'
```

## Build & test

```
./gradlew clean check
```
