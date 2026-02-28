package com.afadhitya.taskmanagement.application.port.in.label;

import com.afadhitya.taskmanagement.application.dto.response.LabelResponse;

import java.util.List;

public interface GetLabelsByProjectUseCase {

    List<LabelResponse> getLabelsByProject(Long projectId);
}
