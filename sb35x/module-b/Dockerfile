# Build stage
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /build

# Copy Maven files (parent expects sb35x and both modules)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY sb35x/pom.xml sb35x/
COPY sb35x/module-a/pom.xml sb35x/module-a/
COPY sb35x/module-b/pom.xml sb35x/module-b/

# Download dependencies (cache layer)
RUN ./mvnw dependency:go-offline -B -pl sb35x/module-b -am || true

# Copy source and build
COPY sb35x sb35x
RUN ./mvnw package -B -pl sb35x/module-b -am -DskipTests

# Download OpenTelemetry Java Agent for reliable trace export
RUN ./mvnw dependency:copy -Dartifact=io.opentelemetry.javaagent:opentelemetry-javaagent:2.17.0 -DoutputDirectory=/build
RUN mv /build/opentelemetry-javaagent-2.17.0.jar /build/opentelemetry-javaagent.jar

# Runtime stage
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=builder /build/sb35x/module-b/target/*.jar app.jar
COPY --from=builder /build/opentelemetry-javaagent.jar opentelemetry-javaagent.jar
ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "app.jar"]
