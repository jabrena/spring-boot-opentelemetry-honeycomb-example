# Build stage
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /build

# Copy Maven files (module-d + parent chain: root -> sb4x -> module-d)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY sb4x/pom.xml sb4x/
COPY sb4x/module-d/pom.xml sb4x/module-d/

# Download dependencies (cache layer)
RUN ./mvnw dependency:go-offline -B -f sb4x/module-d/pom.xml || true

# Copy source and build
COPY sb4x/module-d sb4x/module-d
RUN ./mvnw package -B -f sb4x/module-d/pom.xml -DskipTests

# Download OpenTelemetry Java Agent for reliable trace export
RUN ./mvnw dependency:copy -f sb4x/module-d/pom.xml -Dartifact=io.opentelemetry.javaagent:opentelemetry-javaagent:2.25.0 -DoutputDirectory=/build
RUN mv /build/opentelemetry-javaagent-2.25.0.jar /build/opentelemetry-javaagent.jar

# Runtime stage
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=builder /build/sb4x/module-d/target/*.jar app.jar
COPY --from=builder /build/opentelemetry-javaagent.jar opentelemetry-javaagent.jar
ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "app.jar"]
