# Build stage
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /build

# Copy Maven files (module-a + parent chain: root -> sb35x -> module-a)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY sb35x/pom.xml sb35x/
COPY sb35x/module-a/pom.xml sb35x/module-a/

# Download dependencies (cache layer)
RUN ./mvnw dependency:go-offline -B -f sb35x/module-a/pom.xml || true

# Copy source and build
COPY sb35x/module-a sb35x/module-a
RUN ./mvnw package -B -f sb35x/module-a/pom.xml -DskipTests

# Download OpenTelemetry Java Agent for reliable trace export
RUN ./mvnw dependency:copy -f sb35x/module-a/pom.xml -Dartifact=io.opentelemetry.javaagent:opentelemetry-javaagent:2.25.0 -DoutputDirectory=/build
RUN mv /build/opentelemetry-javaagent-2.25.0.jar /build/opentelemetry-javaagent.jar

# Runtime stage
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=builder /build/sb35x/module-a/target/*.jar app.jar
COPY --from=builder /build/opentelemetry-javaagent.jar opentelemetry-javaagent.jar
ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "app.jar"]
