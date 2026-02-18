# Build stage
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /build

# Copy Maven files (gateway + parent for inheritance)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY gateway/pom.xml gateway/

# Download dependencies (cache layer)
RUN ./mvnw dependency:go-offline -B -f gateway/pom.xml || true

# Copy source and build
COPY gateway gateway
RUN ./mvnw package -B -f gateway/pom.xml -DskipTests

# Download OpenTelemetry Java Agent for reliable trace export
RUN ./mvnw dependency:copy -f gateway/pom.xml -Dartifact=io.opentelemetry.javaagent:opentelemetry-javaagent:2.25.0 -DoutputDirectory=/build
RUN mv /build/opentelemetry-javaagent-2.25.0.jar /build/opentelemetry-javaagent.jar

# Runtime stage
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=builder /build/gateway/target/*.jar app.jar
COPY --from=builder /build/opentelemetry-javaagent.jar opentelemetry-javaagent.jar
ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "app.jar"]
