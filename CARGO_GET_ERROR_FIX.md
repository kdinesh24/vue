# Cargo GET Endpoint Error Fix

## Date: October 5, 2025

## Problem Summary
HTTP 500 error when fetching cargo list from `/api/cargo` endpoint. The frontend was unable to display the cargo list page.

## Root Causes Identified

### 1. **Backend: Lazy Loading Issue**
**Problem:** The `Cargo` entity had `FetchType.LAZY` for the shipment relationship, which caused Jackson JSON serialization to fail when trying to serialize cargo objects with lazy-loaded shipments.

**Location:** `d:/vue/server/src/main/java/com/supplychain/model/Cargo.java`

**Fix:**
```java
// BEFORE:
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "shipment_id", nullable = true)
private Shipment shipment;

// AFTER:
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "shipment_id", nullable = true)
private Shipment shipment;
```

**Why it works:** EAGER loading ensures the shipment data is loaded immediately when cargo is fetched, preventing LazyInitializationException during JSON serialization.

---

### 2. **Frontend: Incorrect Field Access**
**Problem:** The Cargo.vue component was trying to access `item.origin` and `item.destination` directly on cargo objects, but these fields don't exist in the Cargo entity. These fields belong to the Shipment entity.

**Location:** `d:/vue/client/src/views/Cargo.vue`

**Fix:**
```vue
<!-- BEFORE: -->
<TableCell>{{ item.origin }}</TableCell>
<TableCell>{{ item.destination }}</TableCell>

<!-- AFTER: -->
<TableCell>{{ item.shipment?.origin || 'N/A' }}</TableCell>
<TableCell>{{ item.shipment?.destination || 'N/A' }}</TableCell>
```

**Why it works:** Uses optional chaining to safely access origin/destination through the shipment relationship, with fallback to 'N/A' when no shipment is assigned.

---

### 3. **Frontend: Null Weight Handling**
**Problem:** The `formatNumber()` function would fail if `item.weight` was `null` or `undefined`.

**Fix:**
```vue
<!-- BEFORE: -->
<TableCell>{{ formatNumber(item.weight) }}</TableCell>

<!-- AFTER: -->
<TableCell>{{ item.weight ? formatNumber(item.weight) : 'N/A' }}</TableCell>
```

**Why it works:** Checks if weight exists before formatting, showing 'N/A' for null values.

---

### 4. **TypeScript Type Definition Update**
**Problem:** The Cargo interface was missing fields that exist in the database.

**Location:** `d:/vue/client/src/types/index.ts`

**Fix:**
```typescript
// BEFORE:
export interface Cargo {
  cargoId?: number;
  shipment?: Shipment;
  type: string;
  description: string;
  value: number;
  weight?: number;
  weightUnit?: string;
}

// AFTER:
export interface Cargo {
  cargoId?: number;
  shipment?: Shipment;
  type: string;
  description: string;
  value: number;
  weight?: number;
  weightUnit?: string;
  volume?: number;
  createdAt?: string;
}
```

**Why it works:** Aligns TypeScript types with actual database schema, including volume and createdAt fields.

---

## Database Schema Reference

```sql
Table "public.cargo"
   Column    |            Type             | Nullable |         Default
-------------+-----------------------------+----------+-------------------------
 cargo_id    | bigint                      | not null | nextval('cargo_cargo_id_seq')
 description | character varying(255)      |          |
 type        | character varying(255)      |          |
 value       | numeric(12,2)               |          |
 shipment_id | bigint                      |          | (FK to shipments)
 weight      | numeric(10,2)               |          |
 weight_unit | character varying(255)      |          |
 volume      | numeric(10,2)               |          |
 created_at  | timestamp without time zone |          | CURRENT_TIMESTAMP
```

**Note:** Cargo does NOT have origin/destination fields - these are in the Shipments table.

---

## Testing Results

### ✅ GET Request Test
```bash
curl http://localhost:8081/api/cargo
```

**Response:**
```json
[
  {
    "cargoId": 1,
    "shipment": null,
    "type": "Electronics",
    "value": 1000.00,
    "description": "erina and lisa lisa",
    "weight": 1000.00,
    "volume": null,
    "weightUnit": "kg",
    "createdAt": "2025-10-05T02:23:53.317706"
  },
  ...
]
```

**Status:** ✅ 200 OK (Previously: ❌ 500 Internal Server Error)

---

## Key Learnings

1. **JPA Fetch Strategy:** Use `EAGER` loading for relationships that will be serialized to JSON in REST responses, or configure Jackson to handle lazy loading properly.

2. **Data Normalization:** Understand table relationships - cargo doesn't have origin/destination; these belong to shipments. Access related data through proper relationships.

3. **Optional Chaining:** Use `?.` operator in Vue/TypeScript to safely access nested properties that might be null.

4. **Null Handling:** Always check for null values before passing data to functions that expect non-null inputs.

5. **Type Safety:** Keep TypeScript interfaces synchronized with database schema to catch type mismatches early.

---

## Server Status

✅ **Server Running:** `http://localhost:8081`  
✅ **GET /api/cargo:** Working  
✅ **POST /api/cargo:** Working (fixed in previous session)  
✅ **Kafka Integration:** All 5 consumers connected  
✅ **Database:** PostgreSQL connected successfully  

---

## Related Files Modified

1. **Backend:**
   - `d:/vue/server/src/main/java/com/supplychain/model/Cargo.java`

2. **Frontend:**
   - `d:/vue/client/src/views/Cargo.vue`
   - `d:/vue/client/src/types/index.ts`

---

## Verification Steps

1. ✅ Start Spring Boot server
2. ✅ Test GET endpoint with curl
3. ✅ Verify JSON response includes all fields
4. ✅ Check frontend displays cargo list without errors
5. ✅ Verify null values handled gracefully (N/A displayed)
6. ✅ Confirm shipment relationships work correctly

---

## Previous Related Fixes

- **2025-10-05:** Fixed POST /api/cargo 500 error by adding missing `created_at` column to database
- **2025-10-05:** Fixed GET /api/cargo 500 error by changing FetchType to EAGER and fixing frontend field access

---

## Summary

All cargo-related HTTP 500 errors have been resolved. The application now properly:
- Fetches cargo with related shipment data
- Displays cargo in the frontend table
- Handles null/optional fields gracefully
- Maintains proper data relationships between cargo and shipments
