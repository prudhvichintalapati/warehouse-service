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

## 🧪 Testing

### Run All Tests
```bash
./gradlew test
```

### Manual Testing with netcat

**Send Temperature Reading:**
```bash
echo "sensor_id=t1; value=40" | nc -u localhost 3344
```

**Send Humidity Reading:**
```bash
echo "sensor_id=h1; value=60" | nc -u localhost 3355
```

## 🗂️ Project Structure

```
warehouse-monitoring/
├── src/
│   ├── main/
│   │   ├── kotlin/com/warehouse/monitoring/warehouseservice
│   │   │   ├── config/
│   │   │   │   ├── RabbitMQConfig.kt      # Message broker setup
│   │   │   │   └── UdpConfig.kt           # UDP listeners
│   │   │   ├── domain/
│   │   │   │   └── Models.kt              # Data models
│   │   │   ├── service/
│   │   │   │   ├── WarehouseService.kt    # Sensor data collector
│   │   │   │   └── CentralMonitoringService.kt  # Threshold monitor
│   │   │   ├── simulator/
│   │   │   │   └── SensorSimulator.kt     # Mock sensor
│   │   │   └── WarehouseServiceApplication.kt
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── kotlin/com/warehouse/monitoring/warehouseservice
│           ├── domain/
│           │   └── ModelsTest.kt
│           ├── service/
│           │   └── ServiceTests.kt
│           └── integration/
│               └── IntegrationTest.kt
├── build.gradle.kts
└── README.md
```

## 🔍 Key Components

### 1. Warehouse Service
- Receives UDP messages from sensors
- Parses sensor data
- Publishes to RabbitMQ exchange

### 2. Central Monitoring Service
- Subscribes to sensor queues
- Compares readings against thresholds
- Triggers alarms when exceeded

### 3. Sensor Simulator
- Generates random sensor readings
- Configurable via `warehouse.simulator.enabled`
- Useful for testing without physical sensors

## 🛠️ Technologies Used
- **Spring Boot 3.2.1**: Application framework
- **Kotlin 1.9.21**: Programming language
- **Spring Integration**: UDP support
- **Spring AMQP**: RabbitMQ integration
- **MockK**: Kotlin-friendly mocking
- **JUnit 5**: Testing framework