package com.afadhitya.taskmanagement.application.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record UpdateLabelRequest(
        String name,

        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex color (e.g., #FF5733)")
        String color
) {
}
