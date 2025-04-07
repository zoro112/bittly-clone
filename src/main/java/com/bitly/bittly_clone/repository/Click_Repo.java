package com.bitly.bittly_clone.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bitly.bittly_clone.model.Click_url;
import com.bitly.bittly_clone.model.UrlMapping;

@Repository
public interface Click_Repo extends JpaRepository<Click_url, Long> {
    // Custom query methods can be defined here if needed
    // For example, you can find clicks by URL ID or user ID
    // List<Click_url> findByUrlId(Long urlId);
    // List<Click> findByUserId(Long userId);

    List<Click_url> findByUrlMapping(UrlMapping urlMapping);

    List<Click_url> findByUrlMappingAndClickTimeBetween(UrlMapping urlMapping, LocalDateTime startDate,
            LocalDateTime endDate); // Find clicks by URL mapping and date range

    List<Click_url> findByUrlMappingInAndClickTimeBetween(List<UrlMapping> urlMappings, LocalDateTime atStartOfDay,
            LocalDateTime atStartOfDay2);

}
