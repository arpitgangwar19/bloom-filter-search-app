FROM bellsoft/liberica-openjdk-alpine:17

WORKDIR /app

COPY target/feed-search-application-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
