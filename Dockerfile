FROM maven:3.8.7-openjdk-18-slim as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN mvn clean install -DskipTests

FROM maven:3.8.7-openjdk-18-slim
COPY src/main/resources src/main/resources
VOLUME /app
COPY --from=build /workspace/app/target/*.jar /app/application.jar
EXPOSE 9910
ENTRYPOINT ["java","-jar","/app/application.jar" ]
CMD ["applicationName"]