# Smithy Demo

This project is an experimental lab for exploring [Smithy](https://smithy.io/). It demonstrates how to define a service model in Smithy, 
generate both a Java server and a TypeScript client, and run them together in a fully functional environment. 

## Features

- **Smithy-first development:** Service defined in Smithy (`smithy/model/incident.smithy`)
- **Codegen:** Generates:
    - Java server (Kotlin implementation)
    - TypeScript client (consumed by a React app)
- **Full-stack demo:** React app interacts with the backend via the generated client

## Project Structure

```
/
├── incident-app/ # React + Vite frontend (TypeScript)
├── incident-hub/ # Java/Kotlin backend (Smithy server)
├── smithy/ # Smithy model and codegen config
├── nginx.conf # NGINX config
├── docker-compose.yaml # Orchestration for local dev
```

## Getting Started 

### Prerequisites 

- [Docker](https://www.docker.com/)
- [Java 17+](https://adoptium.net/) (for local builds) 
- [Node.js 20+](https://nodejs.org/) (for local builds)

### Build & Run

1. **Build everything:** ```sh ./gradlew build installDist
2. **Start all services:** ```sh docker compose up -d``` 
3. **Open the app:** Visit http://localhost:9090 in your browser

## How it Works 

The smithy model is defined in smithy/model, the codegen is managed by Gradle and also built in the `smithy` module, 
the Java server code is implemented in incident-hub/, and the Typescript client consumed by incident-app/.
Nginx Proxies /incident to the backend, everything else to the frontend (nginx.conf)

### Why NGINX?
To avoid CORS issues, both the API and frontend are served from the same host (localhost:9090). I could not find a way
to make `incident-hub` serve the Smithy-generated SDK with proper CORS headers.