package com.bitly.bittly_clone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UrlMappingCreateDTO {

    @NotBlank(message = "Long URL is required")
    private String longUrl;

    // Optional: if the user wants to customize their short URL
    @Size(min = 3, max = 20, message = "Custom short URL must be between 3 and 20 characters")
    private String shortUrl;

}