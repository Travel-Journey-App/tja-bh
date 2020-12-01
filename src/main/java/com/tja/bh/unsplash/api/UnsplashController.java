package com.tja.bh.unsplash.api;

import com.tja.bh.dto.GenericResponse;
import com.tja.bh.unsplash.dto.Photo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(value = "/api/unsplash", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@Controller
@Slf4j
public class UnsplashController {

    final UnsplashClient unsplashClient;

    @Autowired
    public UnsplashController(UnsplashClient unsplashClient) {
        this.unsplashClient = unsplashClient;
    }

    @GetMapping("/search/{query}")
    public GenericResponse<Photo> getPhoto(@PathVariable String query) {
        try {
            Photo photo = unsplashClient.getPhotoBySearchOrDefault(query);
            log.debug("Photo result for query {}: {}", query, photo);
            return GenericResponse.success(photo);
        } catch (Exception e) {
            log.error("Photo obtaining failed for query {}: {}", query, e.getMessage());
            return GenericResponse.error();
        }
    }
}
