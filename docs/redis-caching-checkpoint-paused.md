# Redis Caching Implementation - PAUSED

## Current Status

| Phase | Status | Notes |
|-------|--------|-------|
| Phase 1 | ✅ Complete | Redis infrastructure setup |
| Phase 2 | ⚠️ Paused | DTO caching implemented but has issues |
| Phase 3 | ⚠️ Partial | Cache eviction annotations added to use cases |

---

## What Was Done

### Phase 1: Infrastructure ✅
- [x] Added `spring-boot-starter-data-redis` to `build.gradle`
- [x] Added Redis service to `docker-compose.yml`
- [x] Added Redis config to `application.properties`
- [x] Replaced `CacheConfig.java` with Redis-based configuration using `Jackson2JsonRedisSerializer`

### Phase 2: Caching Implementation ⚠️

**Issue Encountered:** Circular reference when caching JPA entities directly

**Solution Applied:** Changed to cache DTOs at use case level instead of entities at persistence layer

**Changes Made:**
1. Reverted `@Cacheable`/`@CacheEvict` from persistence adapters
2. Added `@Cacheable` to GET use cases (caching DTOs):
   - `GetWorkspaceByIdUseCaseImpl` - caches `WorkspaceResponse`
   - `GetProjectByIdUseCaseImpl` - caches `ProjectResponse`
   - `GetProjectMembersUseCaseImpl` - caches `List<ProjectMemberResponse>`
   - `GetLabelsByProjectUseCaseImpl` - caches `List<LabelResponse>`

3. Added `@CacheEvict` to WRITE use cases:
   - `CreateWorkspaceUseCaseImpl`, `UpdateWorkspaceUseCaseImpl`, `DeleteWorkspaceByIdUseCaseImpl`
   - `CreateProjectUseCaseImpl`, `UpdateProjectUseCaseImpl`, `DeleteProjectUseCaseImpl`
   - `CreateLabelUseCaseImpl`, `UpdateLabelUseCaseImpl`, `DeleteLabelUseCaseImpl`
   - `AddProjectMemberUseCaseImpl`, `RemoveProjectMemberUseCaseImpl`, `UpdateProjectMemberRoleUseCaseImpl`

### Phase 3: Invalidation
- Already handled via `@CacheEvict` annotations added in Phase 2
- Feature/Limit invalidation already exists in `FeatureToggleAdapter`

---

## Known Issues / Next Steps

### 1. DTO Serialization Issues ⚠️
The DTOs need to be properly serializable for Redis. Current issues:
- Response DTOs may have circular references (via Jackson serialization)
- Need to verify all cached DTOs use `@Builder` and have proper Jackson annotations

**Files to review:**
- `WorkspaceResponse`
- `ProjectResponse`
- `LabelResponse`
- `ProjectMemberResponse`

**Fix needed:** Add `@JsonIgnoreProperties(ignoreUnknown = true)` or ensure DTOs are flat (no entity references)

### 2. Phase 3 Tasks Remaining
- [ ] Verify cache invalidation works correctly
- [ ] Test cache TTL behavior
- [ ] Add logging to verify cache hits/misses

---

## Files Modified

### Infrastructure
- `build.gradle` - added Redis dependency
- `docker-compose.yml` - added Redis service
- `src/main/resources/application.properties` - Redis config
- `src/main/java/com/afadhitya/taskmanagement/infrastructure/config/CacheConfig.java` - Redis cache manager

### Use Cases (Caching Added)
- `GetWorkspaceByIdUseCaseImpl.java` - @Cacheable
- `GetProjectByIdUseCaseImpl.java` - @Cacheable
- `GetProjectMembersUseCaseImpl.java` - @Cacheable
- `GetLabelsByProjectUseCaseImpl.java` - @Cacheable
- `CreateWorkspaceUseCaseImpl.java` - @CacheEvict
- `UpdateWorkspaceUseCaseImpl.java` - @CacheEvict
- `DeleteWorkspaceByIdUseCaseImpl.java` - @CacheEvict
- `CreateProjectUseCaseImpl.java` - @CacheEvict
- `UpdateProjectUseCaseImpl.java` - @CacheEvict
- `DeleteProjectUseCaseImpl.java` - @CacheEvict
- `CreateLabelUseCaseImpl.java` - @CacheEvict
- `UpdateLabelUseCaseImpl.java` - @CacheEvict
- `DeleteLabelUseCaseImpl.java` - @CacheEvict
- `AddProjectMemberUseCaseImpl.java` - @CacheEvict
- `RemoveProjectMemberUseCaseImpl.java` - @CacheEvict
- `UpdateProjectMemberRoleUseCaseImpl.java` - @CacheEvict

---

## To Resume Later

1. **Fix DTO serialization** - Review and fix DTOs to ensure they're properly serializable (flat objects, no circular refs)
2. **Test caching** - Run application and verify Redis keys are created
3. **Verify invalidation** - Test that cache is evicted on updates

---

*Last updated: March 1, 2026*
*Paused by: User request - moving to another feature*
