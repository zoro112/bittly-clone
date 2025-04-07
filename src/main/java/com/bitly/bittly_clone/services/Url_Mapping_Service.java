package com.bitly.bittly_clone.services;

import java.util.List;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.bitly.bittly_clone.dto.ClickUrlDTO;
import com.bitly.bittly_clone.dto.UrlMappingCreateDTO;
import com.bitly.bittly_clone.dto.UrlMappingDTO;
import com.bitly.bittly_clone.model.Click_url;
import com.bitly.bittly_clone.model.UrlMapping;
import com.bitly.bittly_clone.model.User;
import com.bitly.bittly_clone.repository.Click_Repo;
import com.bitly.bittly_clone.repository.Url_MappingRepo;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Data

public class Url_Mapping_Service {
    private final Url_MappingRepo urlMappingRepo;// Repository for Url_mapping entity
    private final ModelMapper modelMapper;// ModelMapper for object mapping
    private final UserService userService;// Service to handle user-related operations
    private final Click_Repo clickRepo; // Repository for Click_url entity
    // private final Url_MappingRepo urlMappingRepo; // Repository for Url_mapping
    // entity

    // Add methods to handle URL mapping operations here
    // For example, you can create, update, delete, or retrieve URL mappings
    public UrlMappingDTO getUrlMappingById(Long id) {
        // Implement the logic to retrieve a URL mapping by ID from the database
        UrlMapping urlMapping = urlMappingRepo.findById(id).orElse(null);
        if (urlMapping != null) {
            return modelMapper.map(urlMapping, UrlMappingDTO.class); // Convert to DTO
        } // Return null if not found
        return null; // URL mapping not found
    }

    public UrlMappingDTO saveUrlMapping(UrlMappingCreateDTO urlMappingCreateDTO) {
        // Implement the logic to save a URL mapping to the database
        UrlMapping urlMapping = modelMapper.map(urlMappingCreateDTO, UrlMapping.class);
        urlMappingRepo.save(urlMapping);
        return modelMapper.map(urlMappingRepo.save(urlMapping), UrlMappingDTO.class); // Convert to DTO

    }

    public UrlMappingDTO createUrlMapping(UrlMappingCreateDTO url_mappingDTO, String name) {
        User user = userService.getUserByUsername(name);
        UrlMapping url_mapping = new UrlMapping();
        url_mapping.setLongUrl(url_mappingDTO.getLongUrl());
        String shortUrl = generateShortUrl(); // Generate a short URL
        while (urlMappingRepo.existsByShortUrl(shortUrl)) { // Check if it already exists
            shortUrl = generateShortUrl(); // Regenerate if it already exists
            System.out.println("Short URL already exists. Regenerating: " + shortUrl);
        }
        url_mapping.setShortUrl(shortUrl); // Set the generated short URL
        url_mapping.setUser(user);
        return modelMapper.map(urlMappingRepo.save(url_mapping), UrlMappingDTO.class);
    }

    private String generateShortUrl() {
        // Implement your logic to generate a short URL here
        // For example, you can use a random string generator or a hashing algorithm
        StringBuilder shortUrl = new StringBuilder();
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int length = 3; // Length of the short URL
        Random random = new Random();
        // Generate a random string of the specified length
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            shortUrl.append(characters.charAt(index));
        }
        return shortUrl.toString();
    }

    public List<UrlMappingDTO> getMyUrls(String name) {
        User user = userService.getUserByUsername(name); // Retrieve the user by username
        List<UrlMapping> urlMappings = urlMappingRepo.findByUser(user); // Retrieve all URL mappings from the database
        if (urlMappings.isEmpty()) {
            System.out.println("No URL mappings found for user: " + name);
            return List.of(); // Return an empty list if no mappings are found
        }

        return urlMappings.stream().map(urlmapping -> modelMapper.map(urlmapping, UrlMappingDTO.class)).toList(); // Convert
                                                                                                                  // to
                                                                                                                  // DTOs
    }

    public List<ClickUrlDTO> getUrlAnalytics(String shortUrl, String name) {

        UrlMapping urlMapping = urlMappingRepo.findByShortUrl(shortUrl); // Retrieve the URL mapping by short URL
        if (urlMapping == null) {
            throw new RuntimeException("URL mapping not found for short URL: " + shortUrl); // Handle not found case
        }
        System.out.println("Username " + urlMapping.getUser().getUsername() + "   " + name);
        if (!urlMapping.getUser().getUsername().equals(name)) {
            System.out.println("Unauthorized access attempt by user: " + name); // Log unauthorized access attempt
            throw new RuntimeException("You are not authorized to view this URL mapping"); // Handle unauthorized access
        }
        List<Click_url> clickUrls = clickRepo.findByUrlMapping(urlMapping); // Retrieve all click URLs associated with
                                                                            // the URL mapping
        return clickUrls.stream().map(clickUrl -> modelMapper.map(clickUrl, ClickUrlDTO.class)).toList(); // Convert to
                                                                                                          // DTOs
    }

    public String getOriginalUrl(String shortenedUrl) {
        UrlMapping urlMapping = urlMappingRepo.findByShortUrl(shortenedUrl); // Retrieve the URL mapping by short URL
        if (urlMapping != null) {
            Click_url clickUrl = new Click_url(); // Create a new Click_url object
            clickUrl.setUrlMapping(urlMapping); // Set the URL mapping
            urlMapping.setClicks(urlMapping.getClicks() + 1); // Increment the click count
            clickRepo.save(clickUrl); // Save the click URL to the database
            urlMappingRepo.save(urlMapping); // Save the updated URL mapping to the database
            return urlMapping.getLongUrl(); // Return the original URL if found
        }
        return null; // Return null if not found
    }

}
