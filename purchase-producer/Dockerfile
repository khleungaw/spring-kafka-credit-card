# Get maven dependencies
FROM maven:3-eclipse-temurin-17 as dependencies
COPY pom.xml /build/
WORKDIR /build/
RUN mvn --batch-mode dependency:go-offline dependency:resolve-plugins

# Build the application
FROM maven:3-eclipse-temurin-17 as build
COPY --from=dependencies /root/.m2 /root/.m2
COPY pom.xml /build/
COPY src /build/src
WORKDIR /build/
RUN mvn --batch-mode --fail-fast package -Dmaven.test.skip

# Run the application
FROM eclipse-temurin:17-jre as runtime
COPY --from=build /build/target/app.jar ./app.jar
CMD ["java", "-jar", "./app.jar"]