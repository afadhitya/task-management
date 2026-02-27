# Database Setup Guide

## Prerequisites

- Docker & Docker Compose (for local development)
- PostgreSQL 15+ (if running locally without Docker)

## Quick Start with Docker

### 1. Start PostgreSQL Container

```bash
docker-compose up -d postgres
```

This will start:
- PostgreSQL on port `5432`
- Database: `taskmanagement`
- Username: `postgres`
- Password: `postgres`

### 2. (Optional) Start pgAdmin

```bash
docker-compose up -d pgadmin
```

Access pgAdmin at: http://localhost:5050
- Email: `admin@admin.com`
- Password: `admin`

### 3. Run the Application

```bash
./gradlew bootRun
```

Flyway will automatically create the database schema on startup.

## Manual Database Setup (Without Docker)

### 1. Install PostgreSQL 15+

```bash
# macOS
brew install postgresql@15
brew services start postgresql@15

# Ubuntu/Debian
sudo apt-get install postgresql-15
sudo service postgresql start
```

### 2. Create Database and User

```bash
sudo -u postgres psql
```

```sql
CREATE DATABASE taskmanagement;
CREATE USER taskuser WITH ENCRYPTED PASSWORD 'taskpass';
GRANT ALL PRIVILEGES ON DATABASE taskmanagement TO taskuser;
\q
```

### 3. Update Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanagement
spring.datasource.username=taskuser
spring.datasource.password=taskpass
```

### 4. Run the Application

```bash
./gradlew bootRun
```

## Configuration Options

### JPA/Hibernate Settings

| Property | Description | Default |
|----------|-------------|---------|
| `spring.jpa.hibernate.ddl-auto` | Schema generation strategy | `validate` |
| `spring.jpa.show-sql` | Log SQL queries | `true` |
| `spring.jpa.properties.hibernate.format_sql` | Pretty-print SQL | `true` |

**Note:** With `ddl-auto=validate`, Hibernate validates the schema against entities but doesn't modify it. Schema changes are managed by Flyway migrations.

### Connection Pool (HikariCP)

| Property | Description | Default |
|----------|-------------|---------|
| `spring.datasource.hikari.maximum-pool-size` | Max connections | `10` |
| `spring.datasource.hikari.minimum-idle` | Min idle connections | `5` |
| `spring.datasource.hikari.connection-timeout` | Connection timeout (ms) | `20000` |

### Flyway Migration

| Property | Description |
|----------|-------------|
| `spring.flyway.enabled` | Enable Flyway | `true` |
| `spring.flyway.locations` | Migration scripts location | `classpath:db/migration` |
| `spring.flyway.baseline-on-migrate` | Baseline existing DB | `true` |

## Database Schema

The initial schema includes:

- **users** - User accounts
- **workspaces** - Workspace/organization container
- **workspace_members** - Workspace membership with roles
- **projects** - Projects within workspaces
- **project_members** - Project-level permissions
- **tasks** - Tasks with status, priority, hierarchy
- **task_assignees** - Many-to-many task assignments
- **labels** - Workspace-scoped labels
- **task_labels** - Task-label associations
- **comments** - Task comments with threading
- **attachments** - File attachments
- **notifications** - User notifications
- **audit_logs** - Activity logging

## Useful Commands

```bash
# Start database
docker-compose up -d postgres

# View logs
docker-compose logs -f postgres

# Stop database
docker-compose down

# Reset database (WARNING: deletes all data)
docker-compose down -v
docker-compose up -d postgres

# Run migrations manually
./gradlew flywayMigrate

# Check migration status
./gradlew flywayInfo
```

## Environment-Specific Profiles

Create `application-dev.properties` or `application-prod.properties` for different environments:

**application-dev.properties:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanagement_dev
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG
```

**application-prod.properties:**
```properties
spring.datasource.url=jdbc:postgresql://${DB_HOST}:5432/taskmanagement
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.show-sql=false
spring.datasource.hikari.maximum-pool-size=20
```

Activate profile:
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```
