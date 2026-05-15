
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests


FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

EXPOSE 8080


COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["sh", "-c", "java -Dspring.datasource.url=${DB_URL} -Dspring.datasource.username=${DB_USERNAME} -Dspring.datasource.password=${DB_PASSWORD} -Dapi.security.token.secret=${JWT_SECRET} -jar app.jar"]