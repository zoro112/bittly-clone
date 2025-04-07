package com.bitly.bittly_clone.repository;

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

}
