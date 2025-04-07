package com.bitly.bittly_clone.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlMappingDTO {

    private Long id;
    private String shortUrl;
    private String longUrl;
    private LocalDateTime createdAt;
    private int clicks;
    private String userName;
}
