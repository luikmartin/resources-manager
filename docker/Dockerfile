FROM eclipse-temurin:21-jdk-alpine as builder

WORKDIR /app

COPY gradle/ gradle/
COPY gradlew gradlew.bat ./
COPY build.gradle settings.gradle ./
COPY api/build.gradle api/
COPY core/build.gradle core/
COPY common/build.gradle common/
COPY liquibase/build.gradle liquibase/

RUN chmod +x gradlew

RUN ./gradlew dependencies --no-daemon

COPY api/src/ api/src/
COPY core/src/ core/src/
COPY common/src/ common/src/
COPY liquibase/src/ liquibase/src/

RUN ./gradlew build --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache curl

WORKDIR /app

RUN addgroup -g 1001 -S spring && adduser -u 1001 -S spring -G spring

COPY --from=builder /app/api/build/libs/*.jar app.jar

RUN mkdir -p /app/logs && chown -R spring:spring /app

USER spring

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseStringDeduplication"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
