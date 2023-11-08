# Etapa 1: Construção
FROM maven:3.8-openjdk-17 AS build

WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

# Etapa 2: Imagem final
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=build /app/target/finance-bot-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
