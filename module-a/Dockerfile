# Build stage
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /build

# Copy Maven files (parent expects both modules)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY module-a/pom.xml module-a/
COPY module-b/pom.xml module-b/

# Download dependencies (cache layer)
RUN ./mvnw dependency:go-offline -B -pl module-a -am || true

# Copy source and build
COPY module-a module-a
COPY module-b module-b
RUN ./mvnw package -B -pl module-a -am -DskipTests

# Download OpenTelemetry Java Agent for reliable trace export
RUN ./mvnw dependency:copy -Dartifact=io.opentelemetry.javaagent:opentelemetry-javaagent:2.17.0 -DoutputDirectory=/build
RUN mv /build/opentelemetry-javaagent-2.17.0.jar /build/opentelemetry-javaagent.jar

# Runtime stage
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=builder /build/module-a/target/*.jar app.jar
COPY --from=builder /build/opentelemetry-javaagent.jar opentelemetry-javaagent.jar
ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "app.jar"]
