Redis Caching Implementation

| Phase | Status | Description |
|-------|--------|-------------|
| Phase 1 | [ ] | Replace Caffeine with Redis |
| Phase 2 | [ ] | Add new caches (users, workspaces, projects, labels, projectMembers) |
| Phase 3 | [ ] | Implement cache invalidation |

### Phase 1: Replace Caffeine with Redis

| Status | Task | Description |
|--------|------|-------------|
| [ ] | Add Redis dependency | Add spring-boot-starter-data-redis to build.gradle |
| [ ] | Add Redis to Docker | Add Redis service to docker-compose.yml |
| [ ] | Configure Redis | Add Redis config to application.properties |
| [ ] | Update CacheConfig | Replace CaffeineCacheManager with RedisCacheManager |
| [ ] | Migrate existing caches | Move workspaceFeatures and workspaceLimits to Redis |

### Phase 2: Add New Caches

| Status | Cache | Description | TTL |
|--------|-------|-------------|-----|
| [ ] | users | User lookups by ID | 10 min |
| [ ] | workspaces | Workspace metadata | 10 min |
| [ ] | projects | Project metadata | 10 min |
| [ ] | labels | Label lookups | 10 min |
| [ ] | projectMembers | Project member lists | 5 min |

### Phase 3: Implement Cache Invalidation

| Status | Component | Description |
|--------|-----------|-------------|
| [ ] | User invalidation | Evict user cache on user update/delete |
| [ ] | Workspace invalidation | Evict workspace cache on CRUD operations |
| [ ] | Project invalidation | Evict project cache on CRUD operations |
| [ ] | Label invalidation | Evict label cache on CRUD operations |
| [ ] | ProjectMember invalidation | Evict cache on member add/remove/role change |
| [ ] | Feature/Limit invalidation | Evict cache on plan configuration change |