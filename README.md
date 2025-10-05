# Supply Chain Cargo Management System

**Team:** Dinesh K, Rahul RR, Dhairya Jangir (2nd Year Students)  
**Academic Project** | **GitHub Copilot Assisted**

---

## 🎯 Project Overview

A full-stack web application for managing supply chain cargo operations with real-time event streaming using Kafka. The system enables operators to track shipments, manage cargo, coordinate vendors, plan routes, and monitor deliveries in real-time.

---

## 🚀 Tech Stack

### Backend
- **Java Spring Boot 3.2.5** (Java 17)
- **Spring Data JPA** - Database ORM
- **Spring Kafka** - Event streaming
- **Spring WebSocket** - Real-time communication
- **PostgreSQL** - Database
- **Maven** - Build tool
- **Lombok** - Boilerplate reduction

### Frontend
- **Vue.js 3.5** (Composition API)
- **TypeScript 5.8**
- **Vue Router 4.5**
- **TailwindCSS 4.1** - Styling
- **Vite 7.1** - Build tool
- **STOMP.js & SockJS** - WebSocket client

### Infrastructure
- **Apache Kafka** - Message broker
- **PostgreSQL** - Database (taskdb)
- **WebSocket** - Real-time notifications

---

## 📊 Core Entities

### 1. **Shipment**
- ShipmentID, Origin, Destination, Status, EstimatedDelivery
- Links to Route and Vendor
- Status: Created → Picked Up → In Transit → Delivered → Delayed

### 2. **Cargo**
- CargoID, ShipmentID (FK), Type, Value, Description
- Additional: Weight, Volume, WeightUnit

### 3. **Route**
- RouteID, OriginPort, DestinationPort, Duration, Status
- Status: Active | Delayed | Closed
- Additional: Distance, TransportationMode, Cost

### 4. **Vendor**
- VendorID, Name, ContactInfo, ServiceType
- Additional: IsActive status

### 5. **Delivery**
- DeliveryID, ShipmentID (FK), ActualDeliveryDate, Recipient
- Status: Pending | Delivered | Failed

---

## 🎨 Features & Modules

### 1. **Cargo Management (CRUD)**
- ✅ Create cargo records with type, weight, value
- ✅ Update cargo information
- ✅ Link cargo to shipments (manifest management)
- ✅ Delete cargo

### 2. **Shipment Tracking (CRUD)**
- ✅ Create, update, delete shipments
- ✅ Track shipment lifecycle status
- ✅ Event-driven updates via Kafka
- ✅ Real-time dashboard notifications
- ✅ Assign routes and vendors

### 3. **Route Management (CRUD)**
- ✅ Define new routes (origin, destination, duration)
- ✅ Update route information
- ✅ Track route status (Active, Delayed, Closed)
- ✅ Transportation mode tracking

### 4. **Vendor Coordination (CRUD)**
- ✅ Store vendor information
- ✅ Assign vendors to shipments
- ✅ Track service types
- ✅ Manage vendor status

### 5. **Event Streaming (Kafka Integration)**
- ✅ Real-time event publishing to Kafka topics
- ✅ Events: Cargo picked up, Shipment in transit, Delivered, Delayed, Route updates
- ✅ WebSocket broadcasting to frontend
- ✅ Live notifications in dashboard

---

## 🔄 Workflow Example

```
1. Operator creates cargo → POST /api/cargo
   └─> Kafka publishes "Cargo created" event

2. Operator creates shipment → POST /api/shipments
   └─> Assigns vendor and route
   └─> Kafka publishes "Shipment created" event

3. Cargo picked up → Operator updates status
   └─> PUT /api/shipments/{id} (status: "Picked Up")
   └─> Kafka publishes "Shipment updated" event
   └─> WebSocket broadcasts to frontend
   └─> Dashboard shows real-time notification

4. Shipment in transit → Status update → Kafka event → Dashboard notification

5. Shipment delivered → Status update → Kafka event → Dashboard notification
```

---

## 📁 Project Structure

```
vue/
├── client/                   # Vue.js Frontend
│   ├── src/
│   │   ├── components/      # UI components + Sidebar + Notifications
│   │   ├── composables/     # useApi.ts, useWebSocket.ts
│   │   ├── views/           # All CRUD pages (15 views)
│   │   ├── router/          # Vue Router configuration
│   │   ├── types/           # TypeScript interfaces
│   │   └── App.vue
│   ├── .gitignore
│   └── package.json
│
├── server/                   # Spring Boot Backend
│   ├── src/main/java/com/supplychain/
│   │   ├── model/           # 5 Entity classes
│   │   ├── controller/      # 5 REST Controllers
│   │   ├── service/         # Kafka Producer & Consumer
│   │   ├── repository/      # 5 JPA Repositories
│   │   └── SupplyChainApplication.java
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── .gitignore
│   └── pom.xml
│
└── PROJECT_ANALYSIS_REPORT.md  # Detailed analysis
```

---

## 🌟 Key Features

### Real-time Dashboard
- Live notifications using WebSocket
- STOMP over WebSocket for event broadcasting
- Auto-dismiss notifications after 8 seconds
- Color-coded events by topic
- Connection status indicator

### Professional UI
- Modern TailwindCSS design
- Responsive layout
- Sidebar navigation
- Loading states with skeleton components
- Form validation
- Error handling with toast notifications

### Robust Backend
- RESTful API design
- Jakarta Bean Validation
- Proper error handling
- Cascade delete logic for data integrity
- Foreign key constraints
- Timestamps for all entities

### Event Streaming
- 5 Kafka topics: shipment-events, cargo-events, route-events, vendor-events, delivery-events
- Producer service for publishing events
- Consumer service for logging events
- WebSocket integration for real-time frontend updates

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 16+
- PostgreSQL 12+
- Apache Kafka 3.6+
- Maven 3.8+

### Backend Setup
```bash
# Navigate to server directory
cd server

# Configure database in application.properties
# spring.datasource.url=jdbc:postgresql://localhost:5432/taskdb
# spring.datasource.username=postgres
# spring.datasource.password=YOUR_PASSWORD

# Start Kafka (if not running)
# kafka-server-start.sh config/server.properties

# Build and run
mvn clean package
mvn spring-boot:run

# Server runs on http://localhost:8081
```

### Frontend Setup
```bash
# Navigate to client directory
cd client

# Install dependencies
npm install

# Run development server
npm run dev

# Frontend runs on http://localhost:5173
```

### Access Application
- **Frontend:** http://localhost:5173
- **Backend API:** http://localhost:8081/api
- **WebSocket:** ws://localhost:8081/ws

---

## 📡 API Endpoints

### Shipments
- `GET /api/shipments` - List all shipments
- `GET /api/shipments/{id}` - Get shipment by ID
- `POST /api/shipments` - Create shipment
- `PUT /api/shipments/{id}` - Update shipment
- `DELETE /api/shipments/{id}` - Delete shipment

### Cargo
- `GET /api/cargo` - List all cargo
- `GET /api/cargo/{id}` - Get cargo by ID
- `POST /api/cargo` - Create cargo
- `PUT /api/cargo/{id}` - Update cargo
- `DELETE /api/cargo/{id}` - Delete cargo

### Routes
- `GET /api/routes` - List all routes
- `GET /api/routes/{id}` - Get route by ID
- `POST /api/routes` - Create route
- `PUT /api/routes/{id}` - Update route
- `DELETE /api/routes/{id}` - Delete route

### Vendors
- `GET /api/vendors` - List all vendors
- `GET /api/vendors/{id}` - Get vendor by ID
- `POST /api/vendors` - Create vendor
- `PUT /api/vendors/{id}` - Update vendor
- `DELETE /api/vendors/{id}` - Delete vendor

### Deliveries
- `GET /api/deliveries` - List all deliveries
- `GET /api/deliveries/{id}` - Get delivery by ID
- `POST /api/deliveries` - Create delivery
- `PUT /api/deliveries/{id}` - Update delivery
- `DELETE /api/deliveries/{id}` - Delete delivery

---

## 🔥 Kafka Topics

- **shipment-events** - Shipment lifecycle events
- **cargo-events** - Cargo operations
- **route-events** - Route status changes
- **vendor-events** - Vendor operations
- **delivery-events** - Delivery updates

---

## 🎓 GitHub Copilot Usage

This project demonstrates effective use of GitHub Copilot for:

### Code Refactoring
- Clean separation of concerns (MVC pattern)
- Proper use of Spring Boot annotations
- Vue.js Composition API best practices
- TypeScript type safety

### Debugging
- Fixed server startup errors (database schema issues)
- Resolved foreign key constraint violations
- Fixed cascade delete logic for shipments
- Corrected entity relationship mappings

### Auto-generating Components
- Generated boilerplate REST controllers
- Created Vue.js CRUD components
- Generated TypeScript interfaces
- Created Kafka producer/consumer services

---

## 📈 Project Status

| Feature | Status |
|---------|--------|
| Core Entities | ✅ Complete (5/5) |
| CRUD Operations | ✅ Complete (All modules) |
| Kafka Integration | ✅ Complete |
| Real-time Notifications | ✅ Complete |
| Frontend UI | ✅ Complete |
| Backend API | ✅ Complete |
| Database Schema | ✅ Complete |
| Documentation | ✅ Complete |
| Deployment | ⏸️ Pending |
| Testing | ⏸️ Pending |

**Overall Progress:** 85% Complete

---

## 🎯 Next Steps

1. ✅ Add Swagger/OpenAPI documentation
2. ✅ Create Docker configuration
3. ✅ Generate automated tests using GitHub Copilot
4. ✅ Deploy to AWS/Azure
5. ✅ Add CI/CD pipeline

---

## 👥 Team

- **Dinesh K** - Full Stack Developer
- **Rahul RR** - Full Stack Developer
- **Dhairya Jangir** - Full Stack Developer

**Academic Year:** 2nd Year  
**AI Assistant:** GitHub Copilot

---

## 📄 License

This is an academic project created for educational purposes.

---

## 📞 Support

For issues or questions, please contact the team members.

---

**Last Updated:** October 5, 2025  
**Version:** 1.0.0
