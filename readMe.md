# 🏦 SecureBankingApi

API RESTful de sistema bancário seguro, desenvolvida com **Java 21 + Spring Boot**, seguindo os princípios de **Clean Architecture** e **Domain-Driven Design (DDD)**. Implantada em container Docker na **AWS EC2** com banco de dados **PostgreSQL no AWS RDS**.

---

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas bem definida:
```
src/
├── domain/            # Entidades, Value Objects, Repositórios (interfaces), regras de negócio
│   ├── account/
│   ├── transaction/
│   ├── user/
│   └── refreshToken/
├── application/       # Casos de uso, serviços de aplicação, exceções
│   ├── usecases/
│   └── services/
└── infrastructure/    # Controllers, persistência JPA, segurança JWT
    ├── api/
    ├── persistence/
    └── security/
```

---

## 🚀 Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.x | Framework web |
| Spring Security | 3.x | Autenticação e autorização |
| JWT (jjwt) | — | Tokens de acesso e refresh |
| BCrypt | — | Hash de senhas |
| PostgreSQL | — | Banco de dados (AWS RDS) |
| Flyway | — | Migrações de banco de dados |
| Hibernate / JPA | — | ORM |
| Docker | — | Containerização |
| JUnit 5 + Mockito | — | Testes unitários |
| Maven | 3.9.6 | Build e dependências |

---

## ☁️ Infraestrutura (AWS)
```
┌─────────────────────────────────────┐
│             AWS EC2                 │
│                                     │
│   ┌─────────────────────────────┐   │
│   │      Docker Container       │   │
│   │   secure_api (porta 3000)   │   │
│   │   Spring Boot → 8080        │   │
│   └──────────────┬──────────────┘   │
└──────────────────│──────────────────┘
                   │
                   ▼
┌──────────────────────────────────────┐
│           AWS RDS                    │
│        PostgreSQL                    │
│   (acessado via DB_URL no .env)      │
└──────────────────────────────────────┘
```

- A aplicação roda como container Docker na instância **EC2**
- O banco de dados **PostgreSQL** está provisionado no **RDS**
- A comunicação entre EC2 e RDS ocorre via VPC (rede privada)
- A aplicação expõe a porta **3000** externamente, mapeada para **8080** internamente

---

## 🔐 Segurança

- Autenticação via **JWT** com Access Token (curta duração) + Refresh Token (longa duração)
- Senhas protegidas com **BCrypt** (fator de custo 12)
- Refresh Tokens armazenados em banco com suporte a revogação
- Autorização por roles: `USER`, `ADMIN`, `READ_ONLY`
- Filtro JWT customizado (`JwtAuthenticationFilter`) em todas as rotas protegidas
- CORS configurado para as origens permitidas

---

## 📋 Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto com as seguintes variáveis:
```env
# Banco de Dados (AWS RDS)
DB_URL=jdbc:postgresql://<rds-endpoint>:5432/<database-name>
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha

# JWT
JWT_SECRET=sua_chave_secreta_minimo_256bits
JWT_ACCESS_TOKEN_EXPIRATION=900000        # 15 minutos em ms
JWT_REFRESH_TOKEN_EXPIRATION=604800000    # 7 dias em ms
```

---

## 🐳 Deploy com Docker

### Pré-requisitos
- Docker instalado na instância EC2
- Instância RDS PostgreSQL acessível via Security Group da VPC
- Arquivo `.env` configurado

### Subindo a aplicação
```bash
# Clonar o repositório
git clone 
cd SecureBankingApi

# Criar e preencher o arquivo .env
cp .env.example .env

# Build e start do container
docker-compose up -d --build

# Acompanhar logs
docker-compose logs -f api

# Parar o container
docker-compose down
```

### Verificar se está rodando
```bash
docker ps
# Deve exibir o container 'secure_api' rodando na porta 3000
```

---

## 🗄️ Migrations

As migrations são gerenciadas pelo **Flyway** e executadas automaticamente na inicialização:

| Versão | Descrição |
|---|---|
| V1 | Criação da tabela `users` |
| V2 | Criação da tabela `accounts` |
| V3 | Criação da tabela `refresh_tokens` |
| V4 | Criação da tabela `transactions` |

> ⚠️ O perfil `docker` utiliza `ddl-auto: validate` — o schema deve existir previamente via Flyway.

---

## 📡 Endpoints da API

### Autenticação — `/api/auth`

| Método | Rota | Descrição | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Registra novo usuário | ❌ |
| POST | `/api/auth/login` | Login e geração de tokens | ❌ |
| POST | `/api/auth/refresh` | Renova o access token | ❌ |
| POST | `/api/auth/revoke` | Logout / revoga refresh token | ✅ |

### Contas — `/api/accounts`

| Método | Rota | Descrição | Auth |
|---|---|---|---|
| POST | `/api/accounts/create` | Cria nova conta bancária | ✅ USER |
| GET | `/api/accounts` | Lista contas do usuário autenticado | ✅ USER |
| GET | `/api/accounts/{id}` | Detalhes de uma conta | ✅ USER |
| GET | `/api/accounts/{id}/balance` | Consulta saldo | ✅ USER |
| PUT | `/api/accounts/{id}/block` | Bloqueia conta | ✅ ADMIN |
| PUT | `/api/accounts/{id}/unblock` | Desbloqueia conta | ✅ ADMIN |
| DELETE | `/api/accounts/{id}` | Encerra conta | ✅ USER/ADMIN |

### Transações — `/api/transaction`

| Método | Rota | Descrição | Auth |
|---|---|---|---|
| POST | `/api/transaction/transfer` | Transferência entre contas | ✅ USER |
| POST | `/api/transaction/deposit` | Depósito em conta | ✅ USER |
| POST | `/api/transaction/withdraw` | Saque de conta | ✅ USER |
| GET | `/api/transaction/account/{accountId}` | Histórico de transações | ✅ USER |
| GET | `/api/transaction/{transactionId}` | Detalhes de uma transação | ✅ USER |
| POST | `/api/transaction/reverse/{transactionId}` | Estorna transação | ✅ ADMIN |

---

## 🧪 Testes
```bash
# Executar todos os testes
mvn test

# Executar com relatório
mvn verify
```

Cobertura inclui:

- **Domain**: `Account`, `Money`, `Transaction`, `AccountDataTransaction`, `RefreshToken`, `User`
- **Application**: `TransferMoneyUseCase`
- **Infrastructure**: `AccountMapper`, `AccountRepositoryAdapter`, `TransactionMapper`, `UserMapper`, `UserRepositoryAdapter`

---

## 📦 Build Manual (sem Docker)
```bash
mvn clean package -DskipTests
java -jar target/*.jar --spring.profiles.active=docker
```

---

## 📁 Domínio — Entidades e Regras

**Entidades:** `User`, `Account`, `Transaction`, `RefreshToken`

**Regras de negócio:**
- Contas bloqueadas ou fechadas não aceitam débito/crédito
- Transferências entre contas do mesmo usuário são proibidas
- Saldo insuficiente impede saques e transferências
- Contas com saldo não podem ser encerradas
- Apenas transações `COMPLETED` podem ser estornadas