# Build stage
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /build

# Copy Maven files (module-c + parent chain: root -> sb4x -> module-c)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY sb4x/pom.xml sb4x/
COPY sb4x/module-c/pom.xml sb4x/module-c/

# Download dependencies (cache layer)
RUN ./mvnw dependency:go-offline -B -f sb4x/module-c/pom.xml || true

# Copy source and build
COPY sb4x/module-c sb4x/module-c
RUN ./mvnw package -B -f sb4x/module-c/pom.xml -DskipTests

# Download OpenTelemetry Java Agent for reliable trace export
RUN ./mvnw dependency:copy -f sb4x/module-c/pom.xml -Dartifact=io.opentelemetry.javaagent:opentelemetry-javaagent:2.25.0 -DoutputDirectory=/build
RUN mv /build/opentelemetry-javaagent-2.25.0.jar /build/opentelemetry-javaagent.jar

# Runtime stage
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=builder /build/sb4x/module-c/target/*.jar app.jar
COPY --from=builder /build/opentelemetry-javaagent.jar opentelemetry-javaagent.jar
ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "app.jar"]
