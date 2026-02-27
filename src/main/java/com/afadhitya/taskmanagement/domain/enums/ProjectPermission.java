package com.afadhitya.taskmanagement.domain.enums;

/**
 * Project-level permissions that define what actions a user can perform on a specific project.
 * These permissions act as restrictions or explicit grants on top of workspace roles.
 *
 * <p>The effective permission is resolved by combining workspace role with project permission:
 * <ul>
 *   <li>Workspace Owner always has MANAGER level (project permission is ignored)</li>
 *   <li>Guest requires explicit project membership to access</li>
 *   <li>For Admin/Member, project permission acts as a ceiling (restrictive model)</li>
 *   <li>If no project permission is set, defaults are: Admin→MANAGER, Member→CONTRIBUTOR</li>
 * </ul>
 *
 * @see com.afadhitya.taskmanagement.domain.enums.WorkspaceRole
 */
public enum ProjectPermission {

    /**
     * Read-only access to the project.
     * Can view tasks, comments, and attachments but cannot modify anything.
     */
    VIEW,

    /**
     * Can contribute to the project.
     * Can create/edit tasks, add comments, manage task labels, and upload attachments.
     * Cannot manage project settings or members.
     */
    CONTRIBUTOR,

    /**
     * Full control over the project.
     * Can manage project settings, add/remove members, and delete the project.
     * Note: Workspace Owner always has this level regardless of project permission setting.
     */
    MANAGER
}
