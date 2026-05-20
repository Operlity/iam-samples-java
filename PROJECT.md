# Java Spring Boot Web Application - Operlity IAM Integration

## 📋 Project Overview

This is a **Java Spring Boot 3.2.2** web application that demonstrates integration with **Operlity Identity and Access Management (IAM)** using **OAuth 2.0** and **OpenID Connect (OIDC)** protocols. The application provides secure authentication and user management through Operlity's identity platform.

### Key Features
- ✅ OAuth 2.0 / OpenID Connect authentication
- ✅ PKCE (Proof Key for Code Exchange) security
- ✅ SSL/HTTPS support with self-signed certificates
- ✅ User profile display with claims
- ✅ Secure session management
- ✅ OIDC-initiated logout
- ✅ Modern Thymeleaf-based UI

---

## 🏗️ Architecture

### Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Framework** | Spring Boot | 3.2.2 |
| **Language** | Java | 17 |
| **Security** | Spring Security OAuth2 Client | 6.x |
| **Template Engine** | Thymeleaf | 3.x |
| **Build Tool** | Maven | 3.9.6 |
| **Protocol** | HTTPS/SSL | TLS 1.2+ |

### Dependencies

```xml
- spring-boot-starter-oauth2-client  → OAuth 2.0 & OIDC support
- spring-boot-starter-security       → Security framework
- spring-boot-starter-thymeleaf      → Server-side templates
- spring-boot-starter-web            → Web MVC
- thymeleaf-extras-springsecurity6   → Security integration for templates
```

---

## 📁 Project Structure

```
Java SpringBoot Web App/
│
├── src/
│   └── main/
│       ├── java/com/operlity/identity/
│       │   ├── Application.java              # Main entry point
│       │   ├── config/
│       │   │   └── SecurityConfig.java       # Security & OAuth configuration
│       │   └── controller/
│       │       └── DashboardController.java  # Welcome page controller
│       │
│       └── resources/
│           ├── application.yml               # App configuration
│           ├── keystore.p12                  # SSL certificate (generated)
│           └── templates/
│               └── welcome.html              # User dashboard UI
│
├── pom.xml                                   # Maven dependencies
├── .gitignore                                # Git ignore rules
├── start-java.ps1                            # PowerShell startup script
├── keystore.p12                              # SSL certificate (root level)
├── LICENSE                                   # Project license
└── README.md                                 # Basic documentation
```

---

## ⚙️ Configuration

### Application Settings (`application.yml`)

#### Server Configuration
```yaml
server:
  port: 4500                    # HTTPS port
  ssl:
	enabled: true
	key-store-type: PKCS12
	key-alias: identityhub
```

#### OAuth 2.0 / OIDC Configuration
```yaml
spring:
  security:
	oauth2:
	  client:
		registration:
		  identityhub:
			client-id: ee772e0e-4f95-48a5-8bbb-f2adb0696109
			client-authentication-method: none  # Public client (PKCE)
			scope: openid,profile,email
			authorization-grant-type: authorization_code
			redirect-uri: "{baseUrl}/signin-oidc"
		provider:
		  identityhub:
			issuer-uri: https://id.demo.operlity.com
			user-name-attribute: preferred_username
```

### Security Configuration

The `SecurityConfig.java` implements:

1. **PKCE (Proof Key for Code Exchange)** - Enhanced security for public clients
2. **OAuth 2.0 Authorization Code Flow** - Secure token exchange
3. **OIDC Logout** - Proper logout with identity provider
4. **Session Management** - Secure session handling

```java
// Key feature: PKCE enabled
authorizationRequestResolver.setAuthorizationRequestCustomizer(
	OAuth2AuthorizationRequestCustomizers.withPkce()
);
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 17+** (Microsoft JDK or OpenJDK)
- **Maven 3.6+** (or use embedded Maven)
- **PowerShell** (for Windows startup script)
- **Operlity IAM Account** (for authentication)

### Installation & Setup

#### 1. Clone the Repository
```bash
git clone https://github.com/Operlity/iam-samples-java.git
cd "Java SpringBoot Web App"
```

#### 2. Configure OAuth Credentials
Edit `src/main/resources/application.yml`:
- Update `client-id` with your Operlity client ID
- Update `client-secret` (if using confidential client)
- Update `issuer-uri` if using a different Operlity environment

#### 3. Generate SSL Certificate (if not exists)
```bash
keytool -genkeypair -alias identityhub -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore keystore.p12 -validity 3650 \
  -storepass password -dname "CN=localhost" -noprompt
```

### Running the Application

#### Option 1: Using PowerShell Script (Windows)
```powershell
.\start-java.ps1
```

This script will:
1. Set up Java and Maven environment
2. Generate SSL certificate (if needed)
3. Clean build the project
4. Start the application
5. Open browser automatically after 25 seconds

#### Option 2: Using Maven Directly
```bash
mvn clean spring-boot:run
```

#### Option 3: Build and Run JAR
```bash
mvn clean package
java -jar target/identity-java-app-0.0.1-SNAPSHOT.jar
```

### Accessing the Application

- **URL**: `https://localhost:4500`
- **Default Redirect**: `/welcome` (after authentication)

**Note**: Accept the SSL certificate warning (self-signed certificate for development)

---

## 🔐 Authentication Flow

```
1. User → https://localhost:4500
2. App redirects → Operlity IAM (id.demo.operlity.com)
3. User logs in → Operlity IAM validates credentials
4. IAM redirects → https://localhost:4500/signin-oidc (with auth code)
5. App exchanges code → for ID token & access token (using PKCE)
6. User authenticated → Welcome page displays user info
```

---

## 📄 Application Endpoints

| Endpoint | Method | Description | Authentication |
|----------|--------|-------------|----------------|
| `/` | GET | Redirects to `/welcome` | Required |
| `/welcome` | GET | User dashboard with profile info | Required |
| `/oauth2/authorization/identityhub` | GET | Initiates OAuth login | Public |
| `/signin-oidc` | GET | OAuth callback endpoint | Public |
| `/logout` | POST | Logs out user from app & IAM | Required |

---

## 👤 User Profile Display

The welcome page displays:
- **Full Name** (`oidcUser.getFullName()`)
- **Email** (`oidcUser.getEmail()`)
- **All OIDC Claims** (JSON display)
- **ID Token** (JWT token value)

Claims include:
- `sub` - Subject (user ID)
- `preferred_username` - Username
- `email`, `email_verified`
- `name`, `given_name`, `family_name`
- `iat`, `exp` - Token timestamps
- And more...

---

## 🛡️ Security Features

### 1. PKCE (Proof Key for Code Exchange)
Protects against authorization code interception attacks by generating a code verifier and challenge.

### 2. HTTPS/SSL Encryption
All traffic is encrypted using TLS 1.2+ with PKCS12 keystore.

### 3. Session Management
- Session fixation protection (migrates session on auth)
- Secure session cookies
- HttpOnly and Secure flags

### 4. OIDC Logout
Properly terminates sessions both locally and with the identity provider.

---

## 🎨 User Interface

The application uses a modern, coffee-themed design:
- **Sidebar Navigation** - User profile and navigation links
- **Responsive Layout** - Grid-based, mobile-friendly
- **Coffee Color Palette** - Warm browns and creams
- **JetBrains Mono Font** - For code/technical elements
- **Outfit Font** - For general UI text

---

## 🧪 Testing

### Manual Testing
1. Start the application
2. Navigate to `https://localhost:4500`
3. Verify redirect to Operlity IAM
4. Log in with test credentials
5. Verify user profile on welcome page
6. Test logout functionality

### Debug Logging
Enable detailed OAuth logs in `application.yml`:
```yaml
logging:
  level:
	org.springframework.security: DEBUG
	org.springframework.security.oauth2: DEBUG
```

---

## 🐛 Troubleshooting

### Common Issues

#### 1. SSL Certificate Errors
**Problem**: Browser shows "Your connection is not private"
**Solution**: Accept the self-signed certificate warning (dev only)

#### 2. OAuth Configuration Error
**Problem**: "invalid_client" or "unauthorized_client"
**Solution**: Verify `client-id` and `redirect-uri` in Operlity IAM console

#### 3. PKCE Challenge Failed
**Problem**: Authorization code exchange fails
**Solution**: Ensure `client-authentication-method: none` is set (for public clients)

#### 4. Port Already in Use
**Problem**: Port 4500 is occupied
**Solution**: Change `server.port` in `application.yml`

#### 5. Maven Build Errors
**Problem**: Dependencies not downloading
**Solution**: Check internet connection and Maven settings

---

## 📦 Build & Deployment

### Building for Production

1. Update `application.yml` for production:
   - Use proper SSL certificate (not self-signed)
   - Set production `issuer-uri`
   - Disable debug logging
   - Use environment variables for secrets

2. Build the JAR:
```bash
mvn clean package -DskipTests
```

3. Run with production profile:
```bash
java -jar -Dspring.profiles.active=prod target/identity-java-app-0.0.1-SNAPSHOT.jar
```

### Environment Variables (Recommended)

```bash
export OAUTH_CLIENT_ID=your-client-id
export OAUTH_CLIENT_SECRET=your-client-secret
export SSL_KEYSTORE_PATH=/path/to/keystore.p12
export SSL_KEYSTORE_PASSWORD=your-password
```

---

## 📝 Development Notes

### Adding New Features
1. New endpoints → `DashboardController.java`
2. Security rules → `SecurityConfig.java`
3. UI templates → `src/main/resources/templates/`
4. Configuration → `application.yml`

### Code Style
- Follow Spring Boot best practices
- Use constructor injection for dependencies
- Keep controllers thin, use service layer for business logic
- Document complex security configurations

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit changes (`git commit -am 'Add new feature'`)
4. Push to branch (`git push origin feature/new-feature`)
5. Create Pull Request

---

## 📄 License

This project is licensed under the terms specified in the LICENSE file.

---

## 🔗 Resources

- **Operlity IAM**: https://id.demo.operlity.com
- **Spring Security OAuth**: https://spring.io/projects/spring-security-oauth
- **OpenID Connect**: https://openid.net/connect/
- **OAuth 2.0 RFC**: https://tools.ietf.org/html/rfc6749
- **PKCE RFC**: https://tools.ietf.org/html/rfc7636

---

## 👥 Support

For issues related to:
- **Application code**: Open an issue on GitHub
- **Operlity IAM**: Contact Operlity support
- **Spring Security**: Check Spring Security documentation

---

## 📊 Project Information

- **Group ID**: `com.operlity`
- **Artifact ID**: `identity-java-app`
- **Version**: `0.0.1-SNAPSHOT`
- **Package**: `com.operlity.identity`
- **Java Version**: 17
- **Spring Boot Version**: 3.2.2

---

## 🔄 Version History

### v0.0.1-SNAPSHOT (Current)
- Initial implementation
- OAuth 2.0 / OIDC integration
- PKCE support
- SSL/HTTPS configuration
- Thymeleaf UI with user profile display
- OIDC logout functionality

---

**Last Updated**: 2025
**Maintained by**: Operlity Team
**Repository**: https://github.com/Operlity/iam-samples-java
