# Spring Boot OpenTelemetry Honeycomb Example

A multi-module Spring Boot POC demonstrating OpenTelemetry tracing with Honeycomb and Jaeger. Module A calls Module B via RestClient; traces flow through the OpenTelemetry Collector to Jaeger (local) and optionally to Honeycomb (cloud).

## Architecture

- **module-a**: REST service that calls module-b via RestClient
- **module-b**: REST service returning "hello world"
- **otel-collector**: Receives OTLP traces and forwards to Jaeger and Honeycomb
- **jaeger**: Local trace visualization UI

## Prerequisites

- Docker and Docker Compose
- Java 25 (for local Maven build)
- Maven 3.9+ (or use `./mvnw`)

## Run with Docker Compose

Build and start all services:

```bash
docker compose up --build
```

Or build the project first, then start:

```bash
mvn clean package -DskipTests
docker compose up --build
```

Services will be available at:

- **module-a**: http://localhost:8080
- **module-b**: http://localhost:8081 (for debugging)
- **Jaeger UI**: http://localhost:16686

## Call Module A

From your terminal:

```bash
curl http://localhost:8080/
```

Expected response: `hello world`

## View Traces in Jaeger

1. Open [http://localhost:16686](http://localhost:16686) in your browser.
2. Select service `module-a` (or `module-b`) from the "Service" dropdown.
3. Click **Find Traces**.
4. Inspect a trace to see the distributed span: `module-a` HTTP request → `module-b` `/hello` span.

## Optional: Send Traces to Honeycomb

Uncomment the `otlp/honeycomb` exporter in `otel-collector-config.yaml`, add it to the traces pipeline, then:

```bash
export HONEYCOMB_API_KEY=your-api-key-here
docker compose up --build
```

Get your API key from [Honeycomb](https://ui.honeycomb.io/account).

## Local Development

To run modules locally without Docker:

1. Start the collector and Jaeger:

   ```bash
   docker compose up otel-collector jaeger
   ```

2. Run module-b (in another terminal) on port 8081:

   ```bash
   cd module-b && mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081 --management.opentelemetry.tracing.export.otlp.endpoint=http://localhost:4318/v1/traces"
   ```

3. Run module-a (in another terminal):

   ```bash
   cd module-a && mvn spring-boot:run -Dspring-boot.run.arguments="--module-b.url=http://localhost:8081 --management.opentelemetry.tracing.export.otlp.endpoint=http://localhost:4318/v1/traces"
   ```

4. Use different ports for local runs: `module-b` with `server.port=8081`, and `module-a` with `module-b.url=http://localhost:8081`. OTLP should use `management.opentelemetry.tracing.export.otlp.endpoint=http://localhost:4318/v1/traces`.

## Troubleshooting: No Traces in Jaeger

If Jaeger shows "Service (0)" after calling the API:

- **Spring Boot 4**: Use `management.opentelemetry.tracing.export.otlp.endpoint` (not `management.otlp.tracing.endpoint`).
- **Service name**: Set `otel.resource.attributes.service.name=module-a` (and module-b) so services appear correctly.
- **Trace propagation**: Use `RestClient.Builder` (injected) instead of `RestClient.create()` so W3C Trace Context headers are propagated to module-b, creating a single distributed trace.
- **Docker tracing**: Uses the OpenTelemetry Java Agent (`-javaagent`). Set `OTEL_EXPORTER_OTLP_ENDPOINT`, `OTEL_SERVICE_NAME`, `spring.docker.compose.enabled=false`, `OTEL_INSTRUMENTATION_OKHTTP_ENABLED=false`, and `OTEL_INSTRUMENTATION_HTTP_URL_CONNECTION_ENABLED=false` (to hide the agent's internal OTLP export POST span from traces).
- Ensure all services are running: `docker compose ps`
- Check otel-collector logs: `docker compose logs otel-collector`
