# ⚠️ Application Properties Security

## Important: Sensitive Data Protection

The `application.properties` file has been **added to .gitignore** to protect sensitive credentials:

### What's Protected:
- ✅ Google OAuth2 Client ID and Secret
- ✅ Database username and password  
- ✅ Kafka configuration
- ✅ Any other sensitive configuration

---

## Setup for New Developers

### Step 1: Copy the Template
```bash
cd server/src/main/resources
cp application.properties.template application.properties
```

### Step 2: Fill in Your Credentials

Edit `application.properties` and replace:

**Database Configuration:**
```properties
spring.datasource.username=YOUR_DATABASE_USERNAME
spring.datasource.password=YOUR_DATABASE_PASSWORD
```

**Google OAuth2 Configuration:**
```properties
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
```

### Step 3: Get Google OAuth2 Credentials

1. Go to [Google Cloud Console](https://console.cloud.google.com/apis/credentials)
2. Create a new OAuth 2.0 Client ID (or use existing)
3. Add authorized redirect URI:
   ```
   http://localhost:8081/login/oauth2/code/google
   ```
4. Copy the Client ID and Client Secret
5. Paste them into your `application.properties`

---

## What's in Git

### ✅ Committed:
- `application.properties.template` - Template with placeholder values
- `.gitignore` - Updated to exclude `application.properties`

### ❌ NOT Committed (Ignored):
- `application.properties` - Your actual file with real credentials
- `application-local.properties`
- `application-dev.properties`
- `application-*.properties`

---

## Security Best Practices

### ✅ DO:
- Keep your `application.properties` **local only**
- Use different credentials for each environment (dev, staging, prod)
- Use environment variables in production
- Rotate credentials regularly
- Share credentials securely (use password managers, not Slack/email)

### ❌ DON'T:
- Commit `application.properties` to git
- Share credentials in public channels
- Use production credentials in development
- Hardcode credentials in source code

---

## For Production Deployment

Use **environment variables** instead of `application.properties`:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/taskdb
export SPRING_DATASOURCE_USERNAME=prod_user
export SPRING_DATASOURCE_PASSWORD=secure_password
export GOOGLE_CLIENT_ID=your_client_id
export GOOGLE_CLIENT_SECRET=your_client_secret
```

Then reference them in `application.properties`:
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
```

---

## Troubleshooting

### Error: "application.properties not found"
**Solution:** Copy the template file:
```bash
cp application.properties.template application.properties
```

### Error: "Invalid client credentials"
**Solution:** Check your Google OAuth2 setup:
1. Verify Client ID and Secret are correct
2. Verify redirect URI is exactly: `http://localhost:8081/login/oauth2/code/google`
3. Verify OAuth consent screen is configured

### Error: "Database connection failed"
**Solution:** Check your PostgreSQL setup:
1. Verify PostgreSQL is running
2. Verify database `taskdb` exists
3. Verify username and password are correct

---

## Current Git Status

```
✅ application.properties.template - Committed (safe)
❌ application.properties - Ignored (contains secrets)
✅ .gitignore - Updated to exclude application.properties
```

**Your sensitive credentials are now protected!** 🔒
