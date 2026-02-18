# AGENTS.md

Guidance for AI agents working on this project.

## Project Overview

Multi-module Spring Boot demo for OpenTelemetry tracing with Honeycomb/Jaeger. Two stacks coexist:

- **sb35x** (Spring Boot 3.5.10): module-a → module-b  
- **sb4x** (Spring Boot 4.0.1): module-c → module-d  

An API **gateway** routes `/api/a` and `/api/c` to module-a and module-c respectively. Both flows return `hello world` from downstream services.

## Architecture

```
gateway:8084  →  /api/a  →  module-a:8080  →  module-b:8080/hello  →  "hello world"
              →  /api/c  →  module-c:8080  →  module-d:8080/hello  →  "hello world"
```

All services send traces to **otel-collector** (OTLP), which forwards to **Jaeger** (and optionally Honeycomb).

## Repository Layout

```
├── pom.xml                    # Root POM; modules: gateway, sb35x, sb4x
├── compose.yaml               # Docker Compose (7 services)
├── otel-collector-config.yaml # OTLP collector config
├── test-gateway.sh            # Canonical integration test
├── gateway/                   # API gateway (RestClient proxy, Swagger UI)
├── sb35x/
│   ├── pom.xml
│   ├── module-a/              # Calls module-b
│   └── module-b/              # Returns "hello world"
└── sb4x/
    ├── pom.xml
    ├── module-c/              # Calls module-d
    └── module-d/              # Returns "hello world"
```

## Key Conventions

- **Docker builds**: Each module’s Dockerfile copies only its own project and parent POMs; uses `-f <module>/pom.xml` for Maven.
- **OpenTelemetry**: Docker runs use the Java agent only; no opentelemetry-spring-boot-starter in sb35x to avoid conflicts.
- **Ports**: gateway 8084, module-a 8080, module-b 8081, module-c 8082, module-d 8083.

## How to Test Changes

After any code or Docker changes, run the integration test:

```bash
./test-gateway.sh
```

- Assumes the stack is already running (e.g. `docker compose up -d`).
- Waits for the gateway, then calls `/api/a` and `/api/c`.
- Asserts both return exactly `hello world`.
- Exit 0 = pass, exit 1 = fail.

**Start stack and test in one step:**

```bash
./test-gateway.sh --up
```

**Manual verification:**

```bash
docker compose up -d
# wait ~20s
curl http://localhost:8084/api/a   # expect "hello world"
curl http://localhost:8084/api/c   # expect "hello world"
```

## Build Commands

| Scope | Command |
|-------|---------|
| All modules | `./mvnw clean package -DskipTests` |
| Gateway only | `./mvnw package -B -f gateway/pom.xml -DskipTests` |
| sb35x (module-a) | `./mvnw package -B -f sb35x/module-a/pom.xml -DskipTests` |
| Docker (all) | `docker compose build` |
| Docker (gateway) | `docker compose build gateway` |

## Important Files

| File | Purpose |
|------|---------|
| `gateway/src/main/java/.../ProxyController.java` | Routes /api/a, /api/c |
| `sb35x/module-a/.../ModuleBClient.java` | RestClient call to module-b |
| `sb4x/module-c/.../ModuleDClient.java` | RestClient call to module-d |
| `sb35x/module-b/.../HelloController.java` | /hello → "hello world" |
| `sb4x/module-d/.../HelloController.java` | /hello → "hello world" |
| `otel-collector-config.yaml` | OTLP receivers and exporters |

## Checklist for Agents

Before finishing a task:

1. Run `./test-gateway.sh` (with stack running) or `./test-gateway.sh --up`.
2. Ensure both `/api/a` and `/api/c` return `hello world`.
3. If modifying Dockerfiles, run `docker compose build` to confirm images build.
