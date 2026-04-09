# 🏦 Secure Banking API

A RESTful banking API built with **Spring Boot**, implementing clean architecture (DDD), JWT authentication, and asynchronous event publishing via RabbitMQ.

---

## 📐 Architecture

```
src/main/java/com/SecureBankingApi/
├── application/
│   ├── config/          # Use case bean configuration
│   ├── exceptions/      # Business exceptions
│   ├── services/        # JwtService, RefreshTokenService
│   └── usecases/        # Application use cases (one per action)
├── domain/
│   ├── account/         # Account aggregate (Account, Money, AccountNumber)
│   ├── refreshToken/    # RefreshToken aggregate
│   ├── transaction/     # Transaction aggregate + event ports
│   └── user/            # User aggregate + domain ports
└── infrastructure/
    ├── api/             # REST controllers, DTOs, exception handler
    ├── config/          # OpenAPI/Swagger configuration
    ├── messaging/       # RabbitMQ publisher implementation
    ├── persistence/     # JPA entities, mappers, repository adapters
    └── security/        # JWT filter, BCrypt hasher, SecurityConfig
```

The project follows **Domain-Driven Design** with a strict separation between domain, application, and infrastructure layers. Domain interfaces are implemented by infrastructure adapters (Ports & Adapters pattern).

---

## 🚀 Features

- **Authentication**: JWT access tokens + refresh token rotation
- **User registration**: CPF validation, duplicate checks, auto account creation
- **Account management**: Create (CHECKING / SAVINGS), block, unblock, close
- **Transactions**: Transfer, deposit, withdrawal, reverse (admin only)
- **Transaction history**: Paginated, sortable by field and direction
- **Role-based access**: `USER`, `ADMIN`, `READ_ONLY` roles
- **Event publishing**: Publishes `TransactionCompletedEvent` to RabbitMQ after each transfer
- **Observability**: Spring Actuator (`/actuator/health`, `/actuator/info`, `/actuator/metrics`)
- **API Docs**: Swagger UI at `/swagger-ui.html`

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3 |
| Security | Spring Security + JJWT |
| Persistence | Spring Data JPA + PostgreSQL |
| Migrations | Flyway |
| Messaging | RabbitMQ (Spring AMQP) |
| Build | Maven |
| Containerization | Docker / Docker Compose |
| API Docs | SpringDoc OpenAPI (Swagger) |

---

## ⚙️ Configuration

The application uses Spring profiles. Set `--spring.profiles.active=docker` to activate the production profile.

### Required environment variables (`.env`)

```env
DB_URL=jdbc:postgresql://host:5432/dbname
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password

RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=admin

JWT_SECRET=your-256-bit-secret-key-at-least-32-characters
JWT_ACCESS_TOKEN_EXPIRATION=900000       # 15 minutes in ms
JWT_REFRESH_TOKEN_EXPIRATION=604800000   # 7 days in ms
```

Place the `.env` file at `SecureBankingApi/.env` (referenced in `docker-compose.yml`).

---

## 🐳 Running with Docker Compose

```bash
# From the root of the project
docker-compose up --build
```

This starts:
- **RabbitMQ** — ports `5672` (AMQP) and `15672` (Management UI)
- **Secure Banking API** — port `3000` (mapped from internal `8080`)

Access the API at `http://localhost:3000` and Swagger at `http://localhost:3000/swagger-ui.html`.

---

## 🗄️ Database Migrations (Flyway)

Migrations run automatically on startup.

| Version | Description |
|---|---|
| V1 | Create `users` table |
| V2 | Create `accounts` table |
| V3 | Create `refresh_tokens` table |
| V4 | Create `transactions` table |
| V5 | Add `source_account_id` / `destination_account_id` FK columns to transactions |
| V6 | Make transaction party columns nullable (to support deposits and withdrawals) |

---

## 🔌 API Endpoints

### Auth — `/api/auth`
| Method | Path | Description | Auth |
|---|---|---|---|
| `POST` | `/register` | Register a new user | Public |
| `POST` | `/login` | Login and receive tokens | Public |
| `POST` | `/refresh` | Refresh access token | Public |
| `POST` | `/revoke` | Revoke refresh token (logout) | Public |

### Accounts — `/api/accounts`
| Method | Path | Description | Auth |
|---|---|---|---|
| `POST` | `/create` | Create a new account | USER |
| `GET` | `/` | List authenticated user's accounts | USER |
| `GET` | `/{id}` | Get account details | USER / ADMIN |
| `GET` | `/{id}/balance` | Get account balance | USER / ADMIN |
| `PUT` | `/{id}/block` | Block an account | ADMIN |
| `PUT` | `/{id}/unblock` | Unblock an account | ADMIN |
| `DELETE` | `/{id}` | Close an account | USER / ADMIN |

### Transactions — `/api/transaction`
| Method | Path | Description | Auth |
|---|---|---|---|
| `POST` | `/transfer` | Transfer between accounts | USER |
| `POST` | `/deposit` | Deposit into account | USER |
| `POST` | `/withdraw` | Withdraw from account | USER |
| `POST` | `/reverse/{transactionId}` | Reverse a completed transaction | ADMIN |
| `GET` | `/account/{accountId}` | Paginated transaction history | USER / ADMIN |
| `GET` | `/{transactionId}` | Get transaction details | USER / ADMIN |

---

## 🔐 Authentication Flow

```
POST /api/auth/register  →  user created + CHECKING account opened
POST /api/auth/login     →  { accessToken, refreshToken, expiresIn }
                           ↓
                  Attach: Authorization: Bearer <accessToken>
                           ↓
POST /api/auth/refresh   →  { newAccessToken, expiresIn }
POST /api/auth/revoke    →  204 No Content (token invalidated)
```

---

## 📨 RabbitMQ Events

After a successful transfer, a `TransactionCompletedEvent` is published to:

- **Exchange**: `transaction.exchange` (Topic)
- **Routing key**: `transaction.completed`
- **Queue**: `transaction.queue`

The event payload contains source/destination emails, transaction ID, amount, type, and completion timestamp. This event is consumed by the **Notification Service**.

---

## 🧪 Tests

```bash
mvn test
```

The test suite includes:

- **Unit tests**: Domain entities (Account, Money, Transaction, User, RefreshToken), mappers, repository adapters, use cases, JWT and BCrypt services, JWT filter
- **Integration tests**: Full HTTP flow using `WebTestClient` + Testcontainers (PostgreSQL + RabbitMQ)
- **Messaging test**: RabbitMQ event publish/consume round-trip

---

## 📏 Business Rules

- A user can only have one account per type (CHECKING or SAVINGS)
- Transfers between accounts of the same user are not allowed
- Deposits and withdrawals are restricted to the account owner
- An account with a non-zero balance cannot be closed
- A blocked account cannot receive or send transactions
- Only `ADMIN` users can block/unblock accounts and reverse transactions
- CPF must be exactly 11 digits; email must be a valid format