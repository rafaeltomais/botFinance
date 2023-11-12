# Etapa 1: Construção
FROM maven:3.8-openjdk-17-slim AS build

WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests && \
    apt-get update && apt-get install -y --no-install-recommends && \
    apt-get clean

# Etapa 2: Imagem final
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=build /app/target/finance-bot-0.0.1-SNAPSHOT.jar app.jar

# Expondo porta configurável via variável de ambiente
EXPOSE ${PORT}

# Adicionando metadata
LABEL version="1.0" \
      description="Finance Bot Application" \
      maintainer="Rafael Tomais"

# Rodar como um usuário não-root
USER 1001

ENTRYPOINT ["java", "-jar", "app.jar"]