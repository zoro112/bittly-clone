package com.bitly.bittly_clone.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bitly.bittly_clone.dto.ClickUrlDTO;
import com.bitly.bittly_clone.dto.UrlMappingCreateDTO;
import com.bitly.bittly_clone.dto.UrlMappingDTO;
import com.bitly.bittly_clone.services.Url_Mapping_Service;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/url")
@RequiredArgsConstructor
public class UrlMappingControl {
    private final Url_Mapping_Service urlMappingService;
    // Add methods to handle URL mapping operations here

    // For example, you can create, update, delete, or retrieve URL mappings

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UrlMappingDTO> createUrlMapping(@RequestBody UrlMappingCreateDTO urlMappingCreateDTO,
            Principal principal) {
        // Implement the logic to save a URL mapping to the database
        urlMappingService.createUrlMapping(urlMappingCreateDTO, principal.getName());
        return ResponseEntity.ok(urlMappingService.createUrlMapping(urlMappingCreateDTO, principal.getName()));
    }

    @GetMapping("/myurls")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<UrlMappingDTO>> getMyUrls(Principal principal) {
        System.out.println("Fetching URLs for user: " + principal.getName());
        // Implement the logic to retrieve URLs for the authenticated user
        return ResponseEntity.ok(urlMappingService.getMyUrls(principal.getName()));
    }

    @GetMapping("/analytics/{shortUrl}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ClickUrlDTO>> getUrlAnalytics(@PathVariable String shortUrl,
            @RequestParam(name = "start") String start,
            @RequestParam(name = "end") String end, Principal principal) {
        // Implement the logic to retrieve URL analytics for the authenticated user

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDate startDate = LocalDate.parse(start, formatter);
        LocalDate endDate = LocalDate.parse(end, formatter);

        List<ClickUrlDTO> clickUrls = urlMappingService.getUrlAnalytics(shortUrl, principal.getName(), startDate,
                endDate);
        return ResponseEntity.ok(clickUrls);
    }

    @GetMapping("/analytics")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ClickUrlDTO>> getAllUrlAnalytics(@RequestParam(name = "start") String start,
            @RequestParam(name = "end") String end, Principal principal) {
        // Implement the logic to retrieve URL analytics for the authenticated user

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDate startDate = LocalDate.parse(start, formatter);
        LocalDate endDate = LocalDate.parse(end, formatter);

        List<ClickUrlDTO> clickUrls = urlMappingService.getAllUrlAnalytics(principal.getName(), startDate, endDate);
        return ResponseEntity.ok(clickUrls);
    }

}
