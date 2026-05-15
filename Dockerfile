
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests


FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

EXPOSE 8080


COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["sh", "-c", "echo '=== ENV CHECK ===' && echo DB_URL=$DB_URL && echo DB_USERNAME=$DB_USERNAME && java -jar app.jar --spring.datasource.url=${DB_URL} --spring.datasource.username=${DB_USERNAME} --spring.datasource.password=${DB_PASSWORD} --api.security.token.secret=${JWT_SECRET}"]