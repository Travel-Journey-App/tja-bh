package com.tja.bh.unsplash.api;

import com.tja.bh.unsplash.dto.Photo;
import com.tja.bh.unsplash.dto.SearchResult;
import javassist.tools.web.BadHttpRequest;
import org.apache.logging.log4j.spi.ObjectThreadContextMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UnsplashClient {
    private final String baseUrl;

    private final RestTemplate restTemplate;

    private final HttpHeaders headers;

    private static final String DEFAULT_QUERY = "Tokyo";

    @Autowired
    public UnsplashClient(
            @Value("${unsplash.base-url}") String baseUrl,
            @Value("${unsplash.access-key}") String accessKey,
            RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        headers = new HttpHeaders();
        headers.add("Authorization", "Client-ID " + accessKey);
        this.restTemplate = restTemplate;
    }

    public Photo getPhotoBySearchOrDefault(final String query) throws BadHttpRequest {
        if (query.isBlank()) {
            throw new IllegalArgumentException("Search query must not be blank!");
        }

        SearchResult resultForQuery = getSearchResult(query);
        if (resultForQuery.getResults().size() > 0) {
            return resultForQuery.getResults().get(0);
        }

        SearchResult resultForDefault = getSearchResult(DEFAULT_QUERY);
        return resultForDefault.getResults().get(0);
    }

    private SearchResult getSearchResult(final String query) throws BadHttpRequest {
        HttpEntity<Object> request = new HttpEntity<>(headers);
        String url = getQueryURL(query);
        ResponseEntity<SearchResult> response = restTemplate.exchange(url, HttpMethod.GET, request, SearchResult.class);

        if (response.getStatusCode().isError()) {
            handleError(response);
        }

        return response.getBody();
    }

    private String getQueryURL(final String query) {
        return baseUrl +
                "/search/photos?" +
                "page=1" +
                "&per_page=1" +
                "&query=" + query;
    }

    private void handleError(final ResponseEntity<SearchResult> responseEntity) throws BadHttpRequest {
        if (responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST) {
            throw new BadHttpRequest();
        }

        if (responseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new IllegalArgumentException("Unauthorized request");
        }

        if (responseEntity.getStatusCode() == HttpStatus.FORBIDDEN) {
            throw new IllegalArgumentException("Forbidden request");
        }

        if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new IllegalArgumentException("Not found");
        }

        if (responseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
                || responseEntity.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
            throw new IllegalArgumentException("Something went wrong on unsplash");
        }
    }
}
