Dockerfile build (recommended)

This project includes a multi-stage `Dockerfile` that builds the app using Maven and produces a small Alpine-based runtime image running Java 25.

- Build the image locally:

```bash
docker build -t ecom:0.0.1-SNAPSHOT .
```

- Run the image locally:

```bash
docker run --rm -p 8080:8080 ecom:0.0.1-SNAPSHOT
```

Notes:
- The Dockerfile uses `maven:3.10-jdk-25` as the build stage and `bellsoft/liberica-openjdk-alpine:25` as the runtime.
- If your environment doesn't have a JDK 25 builder image available, replace the base images with ones supported by your registry or use a custom JDK image.
