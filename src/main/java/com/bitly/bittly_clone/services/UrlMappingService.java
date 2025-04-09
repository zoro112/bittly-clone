package com.bitly.bittly_clone.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.bitly.bittly_clone.dto.ClickUrlDTO;
import com.bitly.bittly_clone.dto.UrlMappingCreateDTO;
import com.bitly.bittly_clone.dto.UrlMappingDTO;
import com.bitly.bittly_clone.model.Click_url;
import com.bitly.bittly_clone.model.UrlMapping;
import com.bitly.bittly_clone.model.User;
import com.bitly.bittly_clone.repository.Click_Repo;
import com.bitly.bittly_clone.repository.UrlMappingRepo;

import io.ipgeolocation.api.Geolocation;
import io.ipgeolocation.api.GeolocationParams;
import io.ipgeolocation.api.IPGeolocationAPI;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Data

public class UrlMappingService {
    private final UrlMappingRepo urlMappingRepo;// Repository for Url_mapping entity
    private final ModelMapper modelMapper;// ModelMapper for object mapping
    private final UserService userService;// Service to handle user-related operations
    private final Click_Repo clickRepo; // Repository for Click_url entity
    private final IPGeolocationAPI ipGeolocationAPI; // Geolocation API for IP address geolocation

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

    /**
     * Retrieves the URL analytics for a given short URL and user.
     *
     * @param shortUrl  The short URL to retrieve analytics for.
     * @param name      The username of the user requesting the analytics.
     * @param startDate The start date for the analytics period.
     * @param endDate   The end date for the analytics period.
     * @return A list of ClickUrlDTO objects containing the click analytics.
     **/

    public List<ClickUrlDTO> getUrlAnalytics(String shortUrl, String name, LocalDate startDate, LocalDate endDate) {

        UrlMapping urlMapping = urlMappingRepo.findByShortUrl(shortUrl); // Retrieve the URL mapping by short URL
        if (urlMapping == null) {
            throw new RuntimeException("URL mapping not found for short URL: " + shortUrl); // Handle not found case
        }
        if (!urlMapping.getUser().getUsername().equals(name)) {
            throw new RuntimeException("You are not authorized to view this URL mapping"); // Handle unauthorized access
        }
        List<Click_url> clickUrls = clickRepo.findByUrlMappingAndClickTimeBetween(urlMapping, startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()); // Retrieve all click URLs associated with
                                                     // the URL mapping
        if (clickUrls.isEmpty()) {
            return List.of(); // Return an empty list if no clicks are found
        }
        // Group clicks by date and count them
        // Collect the click URLs into a list of ClickUrlDTO objects
        return clickUrls.stream()
                .collect(
                        Collectors.groupingBy(clickUrl -> clickUrl.getClickTime().toLocalDate(), Collectors.counting()))
                .entrySet().stream().map(entry -> {
                    ClickUrlDTO clickUrlDTO = new ClickUrlDTO(); // Create a new ClickUrlDTO object
                    clickUrlDTO.setClickTime(entry.getKey().atStartOfDay()); // Set the date of the click
                    clickUrlDTO.setCount(entry.getValue()); // Set the count of clicks for that date
                    return clickUrlDTO; // Return the ClickUrlDTO object
                }

                ).toList(); // Group clicks by date and count them
    }

    /**
     * Retrieves all URL analytics for a given user within a specified date range.
     *
     * @param username  The username of the user requesting the analytics.
     * @param startDate The start date for the analytics period.
     * @param endDate   The end date for the analytics period.
     * @return A list of ClickUrlDTO objects containing the click analytics.
     **/

    public List<ClickUrlDTO> getAllUrlAnalytics(String username, LocalDate startDate, LocalDate endDate) {
        User user = userService.getUserByUsername(username); // Retrieve the user by username
        if (!user.getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to view this URL mapping"); // Handle unauthorized access
        }
        List<UrlMapping> urlMappings = urlMappingRepo.findByUser(user); // Retrieve all URL mappings for the user
        if (urlMappings.isEmpty()) {
            return List.of(); // Return an empty list if no mappings are found
        }
        // Retrieve all click URLs associated with the URL mappings
        List<Click_url> clicksUrl = clickRepo.findByUrlMappingInAndClickTimeBetween(urlMappings,
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay());
        return clicksUrl.stream()
                .collect(
                        Collectors.groupingBy(clickUrl -> clickUrl.getClickTime().toLocalDate(), Collectors.counting()))
                .entrySet().stream().map(entry -> {
                    ClickUrlDTO clickUrlDTO = new ClickUrlDTO(); // Create a new ClickUrlDTO object
                    clickUrlDTO.setClickTime(entry.getKey().atStartOfDay()); // Set the date of the click
                    clickUrlDTO.setCount(entry.getValue()); // Set the count of clicks for that date
                    return clickUrlDTO; // Return the ClickUrlDTO object
                }).toList(); // Group clicks by date and count them
    }

    /**
     * Retrieves the original URL for a given shortened URL.
     *
     * @param shortenedUrl The shortened URL to retrieve the original URL for.
     * @return The original URL if found, or null if not found.
     */
    public String getOriginalUrl(String shortenedUrl, HttpServletRequest request) {
        UrlMapping urlMapping = urlMappingRepo.findByShortUrl(shortenedUrl); // Retrieve the URL mapping by short URL
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        } else {
            // In case of multiple proxies, take the first IP
            ipAddress = ipAddress.split(",")[0];
        }
        GeolocationParams geoParams = new GeolocationParams();
        geoParams.setIPAddress(ipAddress);// Set the IP address for geolocation
        geoParams.setFields("country_name"); // Set the fields to retrieve
        Geolocation geolocation = ipGeolocationAPI.getGeolocation(geoParams); // a.getGeolocation(geoParams);
        String country = geolocation.getCountryName(); // Get the country name from the geolocation response

        if (urlMapping != null) {
            Click_url clickUrl = new Click_url(); // Create a new Click_url object
            clickUrl.setUrlMapping(urlMapping); // Set the URL mapping
            urlMapping.setClicks(urlMapping.getClicks() + 1); // Increment the click count
            clickUrl.setCountry(country); // Set the country of the click
            clickRepo.save(clickUrl); // Save the click URL to the database
            urlMappingRepo.save(urlMapping); // Save the updated URL mapping to the database
            return urlMapping.getLongUrl(); // Return the original URL if found
        }
        return null; // Return null if not found
    }

}
