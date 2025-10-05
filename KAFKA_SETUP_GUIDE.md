# Kafka Setup and Implementation Guide

## 📋 Overview
This guide explains how to set up and use Apache Kafka with WebSocket for real-time updates in your Supply Chain Management System.

## 🚀 What We've Implemented

### Backend (Spring Boot)
1. **Kafka Configuration** - Added to `application.properties`
2. **WebSocket Configuration** - `WebSocketConfig.java`
3. **Kafka Consumer Service** - `KafkaConsumerService.java` (consumes Kafka messages and pushes to WebSocket)
4. **Kafka Producer Service** - `KafkaProducerService.java` (already existed)
5. **Updated Controllers** - All controllers now publish Kafka events on CRUD operations
   - ShipmentController
   - CargoController
   - RouteController
   - DeliveryController
   - VendorController

### Frontend (Vue.js)
1. **WebSocket Composable** - `useWebSocket.ts` (manages WebSocket connection)
2. **Real-time Notifications Component** - `RealtimeNotifications.vue` (displays notifications)
3. **Updated App.vue** - Includes the notifications component
4. **Updated All Views** - All views now listen for real-time updates and auto-refresh

## 📦 Installation Steps

### Step 1: Install and Start Zookeeper

#### Windows:
```bash
# Navigate to Kafka directory
cd C:\kafka

# Start Zookeeper
bin\windows\zookeeper-server-start.bat config\zookeeper.properties
```

#### Linux/Mac:
```bash
# Navigate to Kafka directory
cd ~/kafka

# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties
```

### Step 2: Install and Start Kafka Server

#### Windows:
```bash
# In a NEW terminal window
cd C:\kafka

# Start Kafka
bin\windows\kafka-server-start.bat config\server.properties
```

#### Linux/Mac:
```bash
# In a NEW terminal window
cd ~/kafka

# Start Kafka
bin/kafka-server-start.sh config/server.properties
```

### Step 3: Create Kafka Topics

#### Windows:
```bash
# Create all required topics
bin\windows\kafka-topics.bat --create --topic shipment-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
bin\windows\kafka-topics.bat --create --topic delivery-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
bin\windows\kafka-topics.bat --create --topic route-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
bin\windows\kafka-topics.bat --create --topic cargo-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
bin\windows\kafka-topics.bat --create --topic vendor-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

#### Linux/Mac:
```bash
# Create all required topics
bin/kafka-topics.sh --create --topic shipment-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
bin/kafka-topics.sh --create --topic delivery-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
bin/kafka-topics.sh --create --topic route-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
bin/kafka-topics.sh --create --topic cargo-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
bin/kafka-topics.sh --create --topic vendor-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

### Step 4: Verify Topics Were Created

#### Windows:
```bash
bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092
```

#### Linux/Mac:
```bash
bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```

You should see:
- shipment-events
- delivery-events
- route-events
- cargo-events
- vendor-events

## 🔧 Quick Setup for Windows (All-in-One Script)

If you don't have Kafka installed, here's a quick setup:

### Download Kafka
1. Go to https://kafka.apache.org/downloads
2. Download the latest binary (e.g., `kafka_2.13-3.6.0.tgz`)
3. Extract to `C:\kafka`

### Create Setup Script
Create `setup-kafka.bat`:

```batch
@echo off
cd C:\kafka

echo Creating Kafka topics...

REM Create all topics
bin\windows\kafka-topics.bat --create --topic shipment-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
bin\windows\kafka-topics.bat --create --topic delivery-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
bin\windows\kafka-topics.bat --create --topic route-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
bin\windows\kafka-topics.bat --create --topic cargo-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
bin\windows\kafka-topics.bat --create --topic vendor-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

echo.
echo Topics created successfully!
echo Listing all topics:
bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092

pause
```

## 🏃 Running Your Application

### Terminal 1: Start Zookeeper
```bash
cd C:\kafka
bin\windows\zookeeper-server-start.bat config\zookeeper.properties
```

### Terminal 2: Start Kafka
```bash
cd C:\kafka
bin\windows\kafka-server-start.bat config\server.properties
```

### Terminal 3: Start Backend (Spring Boot)
```bash
cd server
mvn spring-boot:run
```

### Terminal 4: Start Frontend (Vue.js)
```bash
cd client
npm run dev
```

## 🧪 Testing Real-Time Updates

1. Open the application in your browser (http://localhost:5173)
2. You should see a green notification saying "Real-time updates connected!"
3. Try these actions to see real-time notifications:
   - Create a new shipment
   - Update a shipment status
   - Delete a shipment
   - Create cargo, routes, deliveries, or vendors
4. Watch the bottom-right corner for real-time notifications
5. The relevant pages will automatically refresh when updates occur

## 📊 How It Works

```
User Action (Create/Update/Delete)
        ↓
    Controller
        ↓
    Save to Database
        ↓
    Publish to Kafka Topic ← (KafkaProducerService)
        ↓
    Kafka Broker
        ↓
    KafkaConsumerService ← (Listening to topics)
        ↓
    Send to WebSocket ← (SimpMessagingTemplate)
        ↓
    Frontend receives via WebSocket ← (useWebSocket composable)
        ↓
    RealtimeNotifications displays notification
        ↓
    Page auto-refreshes data
```

## 🔍 Monitoring Kafka Messages

### View messages in a topic (Windows):
```bash
bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic shipment-events --from-beginning
```

### View messages in a topic (Linux/Mac):
```bash
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic shipment-events --from-beginning
```

## 🛠️ Troubleshooting

### Problem: Topics not created
**Solution:** Make sure Kafka server is running before creating topics

### Problem: WebSocket not connecting
**Solution:** 
- Check if backend is running on port 8081
- Check browser console for errors
- Verify CORS settings in WebSocketConfig.java

### Problem: No real-time notifications
**Solution:**
- Check if Kafka is running
- Verify topics exist: `kafka-topics.bat --list --bootstrap-server localhost:9092`
- Check backend console for Kafka producer/consumer logs
- Check browser console for WebSocket connection status

### Problem: Port conflicts
**Solution:**
- Zookeeper runs on port 2181
- Kafka runs on port 9092
- Backend runs on port 8081
- Frontend runs on port 5173
- Make sure these ports are available

## 🎯 Features Implemented

✅ Kafka event publishing on all CRUD operations
✅ WebSocket connection for real-time updates
✅ Real-time notification toasts with icons
✅ Auto-refresh of data when updates occur
✅ Connection status indicator
✅ Beautiful UI with smooth animations
✅ Topic-specific icons and colors
✅ Auto-dismiss notifications after 8 seconds
✅ Manual dismiss option
✅ Shows last 3 notifications at a time

## 📝 Next Steps (Optional Enhancements)

1. **Error Handling**: Add retry logic for failed Kafka messages
2. **Message Persistence**: Store messages in a database for history
3. **User Filtering**: Allow users to filter which notifications they see
4. **Sound Alerts**: Add sound notifications for important events
5. **Push Notifications**: Implement browser push notifications
6. **Kafka Clustering**: Set up Kafka cluster for high availability
7. **Message Encryption**: Encrypt sensitive messages in Kafka
8. **Analytics**: Track and analyze Kafka messages for insights

## 🎉 Congratulations!

Your Supply Chain Management System now has real-time updates powered by Kafka and WebSocket!
