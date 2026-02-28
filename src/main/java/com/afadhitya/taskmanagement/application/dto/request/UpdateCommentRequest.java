package com.afadhitya.taskmanagement.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateCommentRequest(
        @NotBlank(message = "Body is required")
        String body
) {
}
