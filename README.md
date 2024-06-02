# kafka-messaging-sample

Explores Kafka messaging with Spring-Boot.

## Build & test

```
./gradlew clean check
```

## Local development

```
docker-compose up -d
./gradlew bootRun

# check availability
curl -X GET localhost:8080/ping

# produce message
curl -X POST localhost:8080/products -H "Content-Type: application/json" -d '{"id":1234,"name":"Effective Java"}'
```

- [local Kafka UI](http://localhost:8081)
- [local Prometheus UI](http://localhost:9090)

