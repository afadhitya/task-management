package com.afadhitya.taskmanagement.infrastructure.audit;

import org.springframework.stereotype.Component;

@Component
public class AuditContextHolder {

    private static final ThreadLocal<AuditContext> CONTEXT = new ThreadLocal<>();

    public static void set(Long workspaceId, Long actorId) {
        CONTEXT.set(new AuditContext(workspaceId, actorId));
    }

    public static AuditContext get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }

    public static boolean hasContext() {
        return CONTEXT.get() != null;
    }

    public record AuditContext(Long workspaceId, Long actorId) {
    }
}
