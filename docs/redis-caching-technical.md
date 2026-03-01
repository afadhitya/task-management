# Redis Caching Technical Documentation

## Overview

This document outlines the implementation of Redis as the distributed caching layer for the Task Management application, replacing the existing Caffeine in-memory cache.

## Current State

- **Existing Cache**: Caffeine with 5-minute TTL, max 10,000 entries
- **Currently Cached**:
  - `workspaceFeatures` - Feature flags per workspace
  - `workspaceLimits` - Plan limits per workspace

## Why Redis

### Reasons for Redis-Only Approach

1. **Distributed Support**: Works across multiple service instances without coordination complexity
2. **Consistency**: Single source of truth, no coordination needed for cache eviction
3. **Simpler Operations**: No hybrid complexity (local + distributed cache coordination)
4. **PRD Alignment**: Already specified in the architecture document

### Alternative Considered

- **Hybrid (Caffeine + Redis)**: Rejected due to complexity in coordinating cache eviction across multiple instances

## Cache Strategy

### Tier 1: High-Value, Low-Risk Caches

| Cache Name | Description | TTL |
|------------|-------------|-----|
| `workspaceFeatures` | Feature flags per workspace | 5 min |
| `workspaceLimits` | Plan limits per workspace | 5 min |
| `users:{userId}` | User lookups by ID | 10 min |
| `workspaces:{workspaceId}` | Workspace metadata | 10 min |

### Tier 2: High-Value, Requires Careful Invalidation

| Cache Name | Description | TTL |
|------------|-------------|-----|
| `projects:{projectId}` | Project metadata | 10 min |
| `labels:{workspaceId}` | Labels (global + project-specific) | 10 min |
| `projectMembers:{projectId}` | Project member lists | 5 min |

## TTL Configuration

| Cache Type | TTL | Rationale |
|------------|-----|-----------|
| workspaceFeatures | 5 min | Can change on plan upgrade |
| workspaceLimits | 5 min | Can change on plan upgrade |
| users | 10 min | User profile changes infrequently |
| workspaces | 10 min | Workspace settings rarely change |
| projects | 10 min | Project metadata relatively static |
| labels | 10 min | Labels are mostly static |
| projectMembers | 5 min | More volatile (member changes) |

## Invalidation Strategy

| Cache | Invalidation Trigger |
|-------|---------------------|
| workspaceFeatures | On plan configuration change |
| workspaceLimits | On plan configuration change |
| users | On user update/delete |
| workspaces | On workspace CRUD operations |
| projects | On project CRUD operations |
| labels | On label CRUD operations |
| projectMembers | On member add/remove/role change |

## Implementation Steps

### Phase 1: Replace Caffeine with Redis

1. Add Redis dependency to `build.gradle`
2. Add Redis service to `docker-compose.yml`
3. Configure Redis connection in `application.properties`
4. Replace `CacheConfig.java` with Redis-based configuration

### Phase 2: Add New Caches

Add `@Cacheable` annotations to target services:

1. User lookups by ID
2. Workspace metadata
3. Project metadata
4. Label lookups
5. Project member lists

### Phase 3: Implement Cache Invalidation

Add `@CacheEvict` annotations to:

1. User CRUD operations
2. Workspace CRUD operations
3. Project CRUD operations
4. Label CRUD operations
5. Project member management

## Configuration

### Redis Connection

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### Cache Configuration

- Use `RedisCacheManager` with `GenericJackson2JsonRedisSerializer`
- Default TTL: 10 minutes
- Cache key prefix: `taskmanagement:{cacheName}:{key}`

## Excluded from Caching

The following are intentionally NOT cached due to high volatility:

- Individual tasks (frequently updated)
- Comments list
- Notifications
- Task counts (complex aggregation)

---

*Document created: March 1, 2026*
