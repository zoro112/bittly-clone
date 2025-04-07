package com.bitly.bittly_clone.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.bitly.bittly_clone.services.Url_Mapping_Service;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UrlRedirectController {

    private final Url_Mapping_Service urlMappingService;

    @GetMapping("/{shortenedUrl}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortenedUrl) {
        // Implement the logic to redirect to the original URL based on the shortened
        // URL
        // You can use the Url_Mapping_Service to retrieve the original URL and perform
        // the redirect
        // Check if the shortened URL exists in the database
        String originalUrl = urlMappingService.getOriginalUrl(shortenedUrl);
        if (originalUrl == null) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", originalUrl);
        return ResponseEntity.status(302).headers(headers).build(); // 302 Found (Redirect)
    }

}
