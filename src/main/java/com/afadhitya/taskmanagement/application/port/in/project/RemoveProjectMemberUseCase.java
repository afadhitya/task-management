package com.afadhitya.taskmanagement.application.port.in.project;

public interface RemoveProjectMemberUseCase {

    void removeMember(Long projectId, Long userId);
}
