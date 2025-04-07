package com.bitly.bittly_clone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bitly.bittly_clone.model.UrlMapping;
import com.bitly.bittly_clone.model.User;

@Repository
public interface Url_MappingRepo extends JpaRepository<UrlMapping, Long> {

    // Custom query methods can be defined here if needed
    // For example, you can find URL mappings by user ID or short URL
    // List<Url_Mapping> findByUserId(Long userId);
    // Url_Mapping findByShortUrl(String shortUrl);
    boolean existsByShortUrl(String shortUrl); // Check if a short URL already exists

    List<UrlMapping> findByUser(User user);

    UrlMapping findByShortUrl(String shortUrl); // Find URL mapping by short URL
}
