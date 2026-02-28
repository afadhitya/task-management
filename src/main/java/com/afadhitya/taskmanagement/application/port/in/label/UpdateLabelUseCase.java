package com.afadhitya.taskmanagement.application.port.in.label;

import com.afadhitya.taskmanagement.application.dto.request.UpdateLabelRequest;
import com.afadhitya.taskmanagement.application.dto.response.LabelResponse;

public interface UpdateLabelUseCase {

    LabelResponse updateLabel(Long labelId, UpdateLabelRequest request, Long currentUserId);
}
