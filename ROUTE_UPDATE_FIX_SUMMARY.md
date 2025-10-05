# Route Update Feature Implementation

## Problem
Routes page had edit button but:
1. No edit route was defined (`/routes/:id/edit`)
2. No `EditRoute.vue` component existed
3. Backend update method was incomplete - only updating 4 out of 7 fields
4. Route entity was missing fields: `distance`, `transportationMode`, `cost`

## Solution

### 1. Database Schema Update

**Added missing columns to `route` table:**

```sql
-- Add missing columns to route table
ALTER TABLE route ADD COLUMN IF NOT EXISTS distance DECIMAL(10, 2);
ALTER TABLE route ADD COLUMN IF NOT EXISTS transportation_mode VARCHAR(50);
ALTER TABLE route ADD COLUMN IF NOT EXISTS cost DECIMAL(12, 2);

-- Add comments for documentation
COMMENT ON COLUMN route.distance IS 'Distance in kilometers';
COMMENT ON COLUMN route.transportation_mode IS 'Transportation mode: Sea, Air, Land, Rail, Multimodal';
COMMENT ON COLUMN route.cost IS 'Route cost in USD';
```

**Run this SQL in your PostgreSQL database:**
```bash
psql -U postgres -d taskdb -c "ALTER TABLE route ADD COLUMN IF NOT EXISTS distance DECIMAL(10, 2);"
psql -U postgres -d taskdb -c "ALTER TABLE route ADD COLUMN IF NOT EXISTS transportation_mode VARCHAR(50);"
psql -U postgres -d taskdb -c "ALTER TABLE route ADD COLUMN IF NOT EXISTS cost DECIMAL(12, 2);"
```

### 2. Backend Updates

#### Route Entity (Route.java)
**Added missing fields:**
```java
@Column(name = "distance", precision = 10, scale = 2)
private Double distance;  // Distance in kilometers

@Column(name = "transportation_mode", length = 50)
private String transportationMode;  // e.g., Sea, Air, Land, Rail

@Column(name = "cost", precision = 12, scale = 2)
private Double cost;  // Route cost
```

#### Route Controller (RouteController.java)
**Updated `updateRoute` method to update ALL fields:**
- originPort
- destinationPort
- distance ⬅️ **Now included**
- duration
- transportationMode ⬅️ **Now included**
- cost ⬅️ **Now included**
- status
- Added error handling with try-catch
- Improved Kafka message

### 3. Frontend Updates

#### Router (router/index.ts)
**Added edit route:**
```typescript
{
  path: '/routes/:id/edit',
  component: () => import('../views/EditRoute.vue'),
}
```

#### Created EditRoute.vue Component (NEW)
**Features:**
- Loads existing route data by ID
- Pre-fills form with current values
- Supports updating all route fields:
  - Origin Port
  - Destination Port
  - Distance (km)
  - Duration (days)
  - Transportation Mode (dropdown: Sea, Air, Land, Rail, Multimodal)
  - Cost ($)
  - Status (dropdown: Active, Delayed, Closed)
- Shows loading state
- Error handling for route not found
- Form validation
- Success feedback
- Navigates back to routes list after update

#### Updated Routes.vue
**Changed editRoute function:**
```typescript
const editRoute = (route: RouteType) => {
  router.push(`/routes/${route.routeId}/edit`)
}
```

**Also fixed:**
- Added null check for routeId in deleteRoute function

## Files Modified

### Backend:
- `server/src/main/java/com/supplychain/model/Route.java` (added 3 fields)
- `server/src/main/java/com/supplychain/controller/RouteController.java` (updated PUT method)

### Frontend:
- `client/src/views/EditRoute.vue` (NEW)
- `client/src/router/index.ts` (added edit route)
- `client/src/views/Routes.vue` (updated editRoute function)

### Database:
- Need to run ALTER TABLE commands (see above)

## How to Use

### 1. Update Database Schema
```bash
# Connect to PostgreSQL
psql -U postgres -d taskdb

# Run these commands:
ALTER TABLE route ADD COLUMN IF NOT EXISTS distance DECIMAL(10, 2);
ALTER TABLE route ADD COLUMN IF NOT EXISTS transportation_mode VARCHAR(50);
ALTER TABLE route ADD COLUMN IF NOT EXISTS cost DECIMAL(12, 2);

# Exit psql
\q
```

### 2. Restart Spring Boot Server
The server will automatically detect the new fields in the Route entity.

### 3. Test the Feature
1. Navigate to Routes page (`/routes`)
2. Click the edit button (pencil icon) on any route
3. Update any fields in the form
4. Click "Update Route"
5. Verify changes are saved and displayed

## API Endpoint

**PUT** `/api/routes/{id}`

**Request Body:**
```json
{
  "originPort": "Los Angeles",
  "destinationPort": "Shanghai",
  "distance": 11000.5,
  "duration": 30,
  "transportationMode": "Sea",
  "cost": 15000.00,
  "status": "Active"
}
```

**Success Response (200):**
```json
{
  "routeId": 1,
  "originPort": "Los Angeles",
  "destinationPort": "Shanghai",
  "distance": 11000.5,
  "duration": 30,
  "transportationMode": "Sea",
  "cost": 15000.00,
  "status": "Active",
  "createdAt": "2025-10-05T10:30:00"
}
```

**Error Response (500):**
```json
"Error updating route: [error message]"
```

## Testing

### Backend Test with curl:
```bash
# First, get an existing route
curl http://localhost:8081/api/routes

# Update route with ID 1
curl -X PUT http://localhost:8081/api/routes/1 \
  -H "Content-Type: application/json" \
  -d '{
    "originPort": "Los Angeles",
    "destinationPort": "Shanghai",
    "distance": 11000.5,
    "duration": 30,
    "transportationMode": "Sea",
    "cost": 15000.00,
    "status": "Active"
  }'
```

### Frontend Test:
1. Go to http://localhost:5173/routes
2. Click edit button on any route
3. Modify fields in the form
4. Submit and verify changes are saved
5. Check that Kafka message is published (visible in server logs)

## Important Notes

⚠️ **Database Migration Required**: You MUST run the ALTER TABLE commands before restarting the server, otherwise you'll get database errors when trying to save routes with the new fields.

✅ **Backward Compatibility**: The new fields are nullable, so existing routes without these values will still work.

## Status
✅ **IMPLEMENTED** - Route update functionality is now fully working with complete backend and frontend integration.

⚠️ **ACTION REQUIRED**: Run database migration script to add missing columns.

## Date Fixed
**2025-10-05**
