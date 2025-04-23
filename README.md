# kafka-messaging

![main branch](https://github.com/OskarWestmeijer/kafka-messaging/actions/workflows/main-build-test-release.yml/badge.svg)
[![codecov](https://codecov.io/github/OskarWestmeijer/kafka-messaging/branch/main/graph/badge.svg?token=CA6XMRS0WS)](https://codecov.io/github/OskarWestmeijer/kafka-messaging)

Explores Kafka messaging with Spring-Boot.

### Technologies

```
Java, Gradle & Spring-Boot
Kafka & Cloud-Events
Github Actions
```

### Build & test

``` bash
./gradlew clean check
```

### Local development

``` bash
docker-compose up -d
./gradlew bootRun
```

### Prepared requests

Verify application is available.

``` bash
curl -X GET localhost:8080/ping
```

Publish message.

``` bash
curl -X POST localhost:8080/products -H "Content-Type: application/json" -d '{"id":1234,"name":"Effective Java"}'
```

### Update Gradle Wrapper

`./gradlew wrapper --gradle-version latest`

### Monitoring

- [Kafka UI](http://localhost:8081)
- [Prometheus UI](http://localhost:9090)

