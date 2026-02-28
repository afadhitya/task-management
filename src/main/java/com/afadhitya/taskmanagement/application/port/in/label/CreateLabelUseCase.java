package com.afadhitya.taskmanagement.application.port.in.label;

import com.afadhitya.taskmanagement.application.dto.request.CreateLabelRequest;
import com.afadhitya.taskmanagement.application.dto.response.LabelResponse;

public interface CreateLabelUseCase {

    LabelResponse createLabel(Long workspaceId, CreateLabelRequest request, Long createdByUserId);
}
