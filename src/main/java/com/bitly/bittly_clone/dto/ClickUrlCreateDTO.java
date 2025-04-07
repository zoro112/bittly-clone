package com.bitly.bittly_clone.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClickUrlCreateDTO {
    @NotNull(message = "URL Mapping ID is required")
    private Long urlMappingId;
}
