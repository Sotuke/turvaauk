FROM gradle:7.6-jdk17 AS builder
WORKDIR /app

COPY gradlew settings.gradle build.gradle /app/
COPY gradle /app/gradle
RUN chmod +x ./gradlew

COPY src /app/src
RUN ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8000

ENTRYPOINT ["java","-jar","app.jar"]