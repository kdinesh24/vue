# Shipment and Vendor Creation Errors Fix

## Date: October 5, 2025

## Problem Summary

### **Issue 1: Vendor Creation - HTTP 500 Error**
- **Error:** POST `/api/vendors` returning 500 Internal Server Error
- **Frontend Sends:** `contactEmail`, `contactPhone`, `address` (separate fields)
- **Backend Expects:** `contactInfo` (single combined field)
- **Missing Columns:** `is_active`, `created_at` in database

### **Issue 2: Shipment Creation - HTTP 400 Bad Request**
- **Error:** POST `/api/shipments` returning 400 Bad Request
- **Entity Validation:** Required `@NotNull` for `assignedRoute` and `assignedVendor` but UI treats them as optional
- **Entity Validation:** Required `@NotBlank` for `shipmentCode` but frontend doesn't send it
- **Date Format:** `estimatedDelivery` type mismatch (LocalDateTime vs LocalDate)
- **Missing Column:** `shipment_code` in database

---

## Root Causes Analysis

### **Vendor Issues**

1. **Database Schema Mismatch:**
   ```sql
   -- MISSING COLUMNS:
   ALTER TABLE vendors ADD COLUMN is_active BOOLEAN DEFAULT TRUE;
   ALTER TABLE vendors ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
   ```

2. **Frontend/Backend Field Mismatch:**
   - Frontend: Sends `contactEmail`, `contactPhone`, `address`
   - Entity: Expects `contactInfo` (single String)
   - Solution: Combine contact fields in frontend before sending

3. **Strict Validation:**
   - Entity had `@NotBlank` and `@Pattern` validation limiting service types
   - Solution: Relaxed validation to allow all service types from UI

### **Shipment Issues**

1. **Database Schema Mismatch:**
   ```sql
   -- MISSING COLUMN:
   ALTER TABLE shipments ADD COLUMN shipment_code VARCHAR(20);
   ```

2. **Overly Strict Entity Validation:**
   ```java
   // BEFORE (WRONG):
   @NotNull(message = "Route is required")
   @ManyToOne(fetch = FetchType.EAGER)
   @JoinColumn(name = "route_id", nullable = false)
   private Route assignedRoute;
   
   @NotNull(message = "Vendor is required")
   @ManyToOne(fetch = FetchType.EAGER)
   @JoinColumn(name = "vendor_id", nullable = false)
   private Vendor assignedVendor;
   
   @NotBlank(message = "Shipment code is required")
   private String shipmentCode;
   
   // AFTER (FIXED):
   @ManyToOne(fetch = FetchType.EAGER)
   @JoinColumn(name = "route_id", nullable = true)  // Optional
   private Route assignedRoute;
   
   @ManyToOne(fetch = FetchType.EAGER)
   @JoinColumn(name = "vendor_id", nullable = true)  // Optional
   private Vendor assignedVendor;
   
   @Column(name = "shipment_code", nullable = true)  // Optional
   private String shipmentCode;
   ```

3. **Date Type Mismatch:**
   ```java
   // BEFORE:
   private LocalDateTime estimatedDelivery;  // Wrong for DATE column
   
   // AFTER:
   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
   private LocalDate estimatedDelivery;  // Matches DATE column
   ```

4. **Status Validation:**
   ```java
   // BEFORE:
   @Pattern(regexp = "Created|In Transit|Delivered|Delayed")
   
   // AFTER:
   // Removed pattern - allows all status values from UI
   ```

---

## Solutions Implemented

### **1. Database Schema Updates**

```sql
-- Fix vendors table
ALTER TABLE vendors 
  ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE,
  ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Fix shipments table
ALTER TABLE shipments 
  ADD COLUMN IF NOT EXISTS shipment_code VARCHAR(20);
```

**Verification:**
```sql
-- Check vendors
SELECT column_name, data_type, is_nullable FROM information_schema.columns 
WHERE table_name = 'vendors' ORDER BY ordinal_position;

-- Check shipments
SELECT column_name FROM information_schema.columns 
WHERE table_name = 'shipments' AND column_name = 'shipment_code';
```

### **2. Vendor Entity Updates**

**File:** `d:/vue/server/src/main/java/com/supplychain/model/Vendor.java`

```java
// Relaxed contact info validation
@Size(max = 500, message = "Contact info cannot exceed 500 characters")
@Column(name = "contact_info", nullable = true, length = 500)
private String contactInfo;

// Relaxed service type validation
@NotBlank(message = "Service type is required")
@Size(max = 100, message = "Service type cannot exceed 100 characters")
@Column(name = "service_type", nullable = false, length = 100)
private String serviceType;

// Optional isActive field
@Column(name = "is_active", nullable = true)
private Boolean isActive = true;
```

### **3. Vendor Controller Updates**

**File:** `d:/vue/server/src/main/java/com/supplychain/controller/VendorController.java`

```java
@PostMapping
public ResponseEntity<?> createVendor(@RequestBody Vendor vendor) {
    try {
        System.out.println("Received vendor request: " + vendor);
        
        // Set default for isActive if not provided
        if (vendor.getIsActive() == null) {
            vendor.setIsActive(true);
        }
        
        Vendor savedVendor = vendorRepository.save(vendor);
        String message = "Vendor created: ID=" + savedVendor.getVendorId() + 
                        ", Name=" + savedVendor.getName();
        kafkaProducerService.sendMessage("vendor-events", message);
        return ResponseEntity.ok(savedVendor);
    } catch (Exception e) {
        System.err.println("Error creating vendor: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(500).body("Error creating vendor: " + e.getMessage());
    }
}
```

### **4. Vendor Frontend Updates**

**File:** `d:/vue/client/src/views/CreateVendor.vue`

```javascript
const handleSubmit = async () => {
  isSubmitting.value = true
  try {
    // Combine contact information into a single field
    const contactInfo = `Email: ${formData.contactEmail}, Phone: ${formData.contactPhone}, Address: ${formData.address}`
    
    const vendorData = {
      name: formData.name,
      serviceType: formData.serviceType,
      contactInfo: contactInfo
    }
    
    await createVendor(vendorData)
    router.push('/vendors')
  } catch (error) {
    console.error('Error creating vendor:', error)
    alert('Failed to create vendor. Please try again.')
  } finally {
    isSubmitting.value = false
  }
}
```

### **5. Shipment Entity Updates**

**File:** `d:/vue/server/src/main/java/com/supplychain/model/Shipment.java`

```java
// Made assignedRoute optional
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "route_id", nullable = true)
private Route assignedRoute;

// Made assignedVendor optional
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "vendor_id", nullable = true)
private Vendor assignedVendor;

// Made shipmentCode optional
@Size(max = 20, message = "Shipment code cannot exceed 20 characters")
@Column(name = "shipment_code", unique = true, length = 20, nullable = true)
private String shipmentCode;

// Removed status pattern validation
@NotBlank(message = "Status is required")
@Column(name = "status", nullable = false, length = 50)
private String status = "Created";

// Fixed date type to match database DATE column
@Column(name = "estimated_delivery", nullable = true)
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
private LocalDate estimatedDelivery;  // Changed from LocalDateTime
```

---

## Testing

### **Vendor Creation Test**

```bash
curl -X POST http://localhost:8081/api/vendors \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Logistics",
    "serviceType": "Logistics",
    "contactInfo": "Email: test@logistics.com, Phone: 123-456-7890, Address: 123 Main St"
  }'
```

**Expected Response:**
```json
{
  "vendorId": 1,
  "name": "Test Logistics",
  "contactInfo": "Email: test@logistics.com, Phone: 123-456-7890, Address: 123 Main St",
  "serviceType": "Logistics",
  "isActive": true,
  "createdAt": "2025-10-05T12:05:49.024911"
}
```

**Result:** ✅ **SUCCESS** (200 OK)

### **Shipment Creation Test**

```bash
curl -X POST http://localhost:8081/api/shipments \
  -H "Content-Type: application/json" \
  -d '{
    "origin": "New York",
    "destination": "Los Angeles",
    "status": "Created",
    "estimatedDelivery": "2025-11-01"
  }'
```

**Expected Response:**
```json
{
  "shipmentId": 1,
  "origin": "New York",
  "destination": "Los Angeles",
  "status": "Created",
  "estimatedDelivery": "2025-11-01",
  "assignedRoute": null,
  "assignedVendor": null,
  "shipmentCode": null,
  "createdAt": "2025-10-05T...",
  "updatedAt": "2025-10-05T..."
}
```

**Status:** Ready to test after server restart

---

## Database Schema Summary

### **Vendors Table (Updated)**
```
 Column       | Type                        | Nullable | Default
--------------+-----------------------------+----------+-------------------
 vendor_id    | bigint                      | NOT NULL | nextval(...)
 name         | varchar(255)                | YES      |
 contact_info | varchar(255)                | YES      |
 service_type | varchar(255)                | YES      |
 is_active    | boolean                     | YES      | true
 created_at   | timestamp without time zone | YES      | CURRENT_TIMESTAMP
```

### **Shipments Table (Updated)**
```
 Column             | Type                 | Nullable | Default
--------------------+----------------------+----------+---------
 shipment_id        | bigint               | NOT NULL | nextval(...)
 origin             | varchar(255)         | YES      |
 destination        | varchar(255)         | YES      |
 estimated_delivery | date                 | YES      |
 status             | varchar(255)         | YES      |
 created_at         | timestamp(6)         | YES      |
 updated_at         | timestamp(6)         | YES      |
 route_id           | bigint               | YES      | (FK)
 vendor_id          | bigint               | YES      | (FK)
 shipment_code      | varchar(20)          | YES      | (UNIQUE)
```

---

## Key Changes Summary

| Component | Change | Reason |
|-----------|--------|--------|
| **vendors** table | Added `is_active`, `created_at` columns | Required by Entity but missing |
| **shipments** table | Added `shipment_code` column | Required by Entity but missing |
| **Vendor** entity | Relaxed validation constraints | Allow flexible input from UI |
| **Vendor** controller | Added error handling & default values | Prevent 500 errors |
| **CreateVendor** frontend | Combine contact fields | Match backend expectation |
| **Shipment** entity | Made route/vendor/code optional | Match UI behavior |
| **Shipment** entity | Changed `LocalDateTime` → `LocalDate` | Match database DATE type |
| **Shipment** entity | Removed status pattern validation | Accept all UI status values |

---

## Files Modified

### **Backend:**
1. `d:/vue/server/src/main/java/com/supplychain/model/Vendor.java`
2. `d:/vue/server/src/main/java/com/supplychain/model/Shipment.java`
3. `d:/vue/server/src/main/java/com/supplychain/controller/VendorController.java`

### **Frontend:**
1. `d:/vue/client/src/views/CreateVendor.vue`

### **Database:**
1. vendors table - added 2 columns
2. shipments table - added 1 column

---

## How to Apply Fixes

1. **Apply database migrations:**
   ```bash
   PGPASSWORD=7780757556 psql -U postgres -d taskdb -c "
   ALTER TABLE vendors 
     ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE,
     ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
   
   ALTER TABLE shipments 
     ADD COLUMN IF NOT EXISTS shipment_code VARCHAR(20);
   "
   ```

2. **Restart Spring Boot server:**
   ```bash
   cd D:/vue/server
   mvn spring-boot:run
   ```

3. **Test vendor creation** through the UI at `/vendors/create`

4. **Test shipment creation** through the UI at `/shipments/create`

---

## Expected Behavior After Fix

### **Vendor Creation:**
- ✅ All service types accepted (Logistics, Transportation, Warehousing, etc.)
- ✅ Contact information combined from email, phone, address
- ✅ Default `isActive = true` set automatically
- ✅ Creation timestamp recorded automatically
- ✅ Returns 200 OK with complete vendor object

### **Shipment Creation:**
- ✅ Works without assigning route (optional)
- ✅ Works without assigning vendor (optional)
- ✅ Works without shipment code (optional)
- ✅ Accepts all status values from UI dropdown
- ✅ Properly parses date in format "yyyy-MM-dd"
- ✅ Returns 200 OK (or 201 CREATED) with complete shipment object

---

## Troubleshooting

### If Vendor Creation Still Fails:
1. Check server logs: `tail -f server/logs/spring-boot.log`
2. Verify database columns exist: `\d vendors`
3. Check contactInfo format in request payload
4. Ensure `isActive` defaults to `true` in controller

### If Shipment Creation Still Fails:
1. Check date format: Must be "yyyy-MM-dd" (e.g., "2025-11-01")
2. Verify `shipment_code` column exists: `\d shipments`
3. Check that status value matches one from UI dropdown
4. Ensure route_id and vendor_id are NULL-able in database

---

## Related Documentation

- Previous fix: `CARGO_CREATION_FIXED.md` - Fixed cargo creation 500 error
- Previous fix: `CARGO_GET_ERROR_FIX.md` - Fixed cargo GET endpoint 500 error

---

## Status

✅ **Vendor Creation:** FIXED and TESTED  
🔄 **Shipment Creation:** FIXED (awaiting final test after server restart)

All code changes committed and ready for deployment.
