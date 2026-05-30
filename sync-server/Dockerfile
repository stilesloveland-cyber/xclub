FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY build/libs/sync-server-*-all.jar app.jar
EXPOSE 5555
ENTRYPOINT ["java", "-jar", "app.jar"]
