FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean :app-service:bootJar --no-daemon -x test -x validateStructure
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/applications/app-service/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]