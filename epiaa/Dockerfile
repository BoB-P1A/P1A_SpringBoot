# Dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/epia-backend-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]