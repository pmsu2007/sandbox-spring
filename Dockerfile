FROM gradle:8.13.0-jdk17 AS builder

WORKDIR /app

COPY settings.gradle build.gradle gradlew /app/
COPY gradle /app/gradle

RUN chmod +x ./gradlew

RUN ./gradlew dependencies

COPY src /app/src

RUN ./gradlew clean build -x test

FROM eclipse-temurin:17-jre-jammy AS app

COPY --from=builder /app/build/libs/app.jar /app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]