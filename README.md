# messaging-sample

Explores several messaging solutions with Spring-Boot.

## Local development

```
docker-compose up -d
./gradlew bootRun
```

## Build & test

Several options. I am still exploring gradle, coming from maven. Official gradle [build
lifecycle doc](https://docs.gradle.org/current/userguide/migrating_from_maven.html#migmvn:build_lifecycle).

```
./gradlew clean (cleans build dir)
./gradlew classes (compiles source code to build dir)
./gradlew test  (includes classes task and executes unit tests on top)
./gradlew assemble (includes classes task, but not test task. creates jar)
./gradlew check (includes compile task, test task and creates jar)
```
