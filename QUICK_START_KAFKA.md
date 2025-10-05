# 🚀 Quick Start - Kafka Commands

## For Windows Users

### 1. Start Zookeeper (Terminal 1)
```bash
cd C:\kafka
bin\windows\zookeeper-server-start.bat config\zookeeper.properties
```

### 2. Start Kafka Server (Terminal 2)
```bash
cd C:\kafka
bin\windows\kafka-server-start.bat config\server.properties
```

### 3. Create Topics (One-time setup)
```bash
cd C:\kafka
bin\windows\kafka-topics.bat --create --topic shipment-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
bin\windows\kafka-topics.bat --create --topic delivery-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
bin\windows\kafka-topics.bat --create --topic route-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
bin\windows\kafka-topics.bat --create --topic cargo-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
bin\windows\kafka-topics.bat --create --topic vendor-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

### 4. Verify Topics
```bash
bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092
```

### 5. Start Backend (Terminal 3)
```bash
cd d:\vue\server
mvn spring-boot:run
```

### 6. Start Frontend (Terminal 4)
```bash
cd d:\vue\client
npm run dev
```

---

## For Linux/Mac Users

### 1. Start Zookeeper (Terminal 1)
```bash
cd ~/kafka
bin/zookeeper-server-start.sh config/zookeeper.properties
```

### 2. Start Kafka Server (Terminal 2)
```bash
cd ~/kafka
bin/kafka-server-start.sh config/server.properties
```

### 3. Create Topics (One-time setup)
```bash
cd ~/kafka
bin/kafka-topics.sh --create --topic shipment-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
bin/kafka-topics.sh --create --topic delivery-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
bin/kafka-topics.sh --create --topic route-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
bin/kafka-topics.sh --create --topic cargo-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
bin/kafka-topics.sh --create --topic vendor-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

### 4. Verify Topics
```bash
bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```

### 5. Start Backend (Terminal 3)
```bash
cd ~/vue/server
mvn spring-boot:run
```

### 6. Start Frontend (Terminal 4)
```bash
cd ~/vue/client
npm run dev
```

---

## 🔍 Useful Monitoring Commands

### View messages in shipment-events topic (Windows):
```bash
bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic shipment-events --from-beginning
```

### View messages in shipment-events topic (Linux/Mac):
```bash
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic shipment-events --from-beginning
```

### Delete a topic (if needed) - Windows:
```bash
bin\windows\kafka-topics.bat --delete --topic shipment-events --bootstrap-server localhost:9092
```

### Delete a topic (if needed) - Linux/Mac:
```bash
bin/kafka-topics.sh --delete --topic shipment-events --bootstrap-server localhost:9092
```

---

## 📝 Notes

- Keep Zookeeper and Kafka running in separate terminals
- Topics only need to be created once
- If you restart Kafka, the topics will persist
- The backend automatically connects to Kafka on startup
- The frontend automatically connects to WebSocket when you open the app

## ✅ Success Indicators

1. Zookeeper console shows: `INFO binding to port 0.0.0.0/0.0.0.0:2181`
2. Kafka console shows: `INFO Kafka Server started`
3. Backend console shows: `Subscribed to topic/partition...`
4. Frontend shows: "Real-time updates connected!" notification
5. When you create/update data, you see notifications in bottom-right corner

## 🎯 Test It!

1. Open http://localhost:5173
2. Create a new shipment
3. Watch for the green notification in the bottom-right
4. Check that the shipments list refreshes automatically
5. Success! 🎉
