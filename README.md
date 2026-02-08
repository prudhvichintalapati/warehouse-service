## 🚀 Prerequisites

- Java 17 or higher
- Gradle 8.x
- RabbitMQ (running on localhost:5672)
- Docker (optional, for RabbitMQ)

## 🔧 Installation & Setup

### 1. Start RabbitMQ

**Using Docker:**
```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

### 2. Clone and Build

```bash
./gradlew clean build
```

### 3. Run the Application

```bash
./gradlew bootRun
```