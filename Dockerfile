# 1. Fáza: Zostavenie aplikácie (vyrobí /target/*.jar)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2. Fáza: Finálny obraz (ten, ktorý pobeží v notebooku)
FROM eclipse-temurin:17-jre
WORKDIR /app
# Skopírujeme JAR súbor z fázy "build"
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
