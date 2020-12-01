package com.tja.bh.unsplash;

import com.tja.bh.unsplash.api.UnsplashClient;
import com.tja.bh.unsplash.dto.Photo;
import com.tja.bh.unsplash.dto.SearchResult;
import javassist.tools.web.BadHttpRequest;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UnsplashClientTest {
    private static final String URL_MOSCOW = getUrlForQuery("Moscow");
    private static final String URL_TOKYO = getUrlForQuery("Tokyo");

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UnsplashClient unsplashClient;

    private SearchResult searchResult;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(unsplashClient, "baseUrl", "https://api.unsplash.com");

        final Photo photo = new Photo();
        searchResult = new SearchResult(1, 1, Collections.singletonList(photo));
    }

    @Test
    public void testEmptyQuery() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> unsplashClient.getPhotoBySearchOrDefault(""));

        String expectedMessage = "Search query must not be blank!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testBlankQuery() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> unsplashClient.getPhotoBySearchOrDefault("    "));

        String expectedMessage = "Search query must not be blank!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testReturnPhoto() throws BadHttpRequest {
        when(restTemplate.exchange(
                eq(URL_MOSCOW),
                eq(HttpMethod.GET),
                any(),
                ArgumentMatchers.<Class<SearchResult>>any())
        )
                .thenReturn(new ResponseEntity<>(searchResult, HttpStatus.OK));

        Assert.assertEquals(searchResult.getResults().get(0), unsplashClient.getPhotoBySearchOrDefault("Moscow"));
    }

    @Test
    public void testReturnDefaultPhoto() throws BadHttpRequest {
        when(restTemplate.exchange(
                eq(URL_MOSCOW),
                eq(HttpMethod.GET),
                any(),
                ArgumentMatchers.<Class<SearchResult>>any())
        )
                .thenReturn(new ResponseEntity<>(new SearchResult(), HttpStatus.OK));

        when(restTemplate.exchange(
                eq(URL_TOKYO),
                eq(HttpMethod.GET),
                any(),
                ArgumentMatchers.<Class<SearchResult>>any())
        )
                .thenReturn(new ResponseEntity<>(searchResult, HttpStatus.OK));

        Assert.assertEquals(searchResult.getResults().get(0), unsplashClient.getPhotoBySearchOrDefault("Moscow"));
    }

    @Test
    public void testReturnBadHttpRequest() {
        when(restTemplate.exchange(
                eq(URL_MOSCOW),
                eq(HttpMethod.GET),
                any(),
                ArgumentMatchers.<Class<SearchResult>>any())
        )
                .thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));


        assertThrows(BadHttpRequest.class, () -> unsplashClient.getPhotoBySearchOrDefault("Moscow"));
    }

    @Test
    public void testReturnUnauthorized() {
        when(restTemplate.exchange(
                eq(URL_MOSCOW),
                eq(HttpMethod.GET),
                any(),
                ArgumentMatchers.<Class<SearchResult>>any())
        )
                .thenReturn(new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED));


        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> unsplashClient.getPhotoBySearchOrDefault("Moscow"));


        String expectedMessage = "Unauthorized request";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testReturnForbidden() {
        when(restTemplate.exchange(
                eq(URL_MOSCOW),
                eq(HttpMethod.GET),
                any(),
                ArgumentMatchers.<Class<Object>>any())
        )
                .thenReturn(new ResponseEntity<>(null, HttpStatus.FORBIDDEN));


        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> unsplashClient.getPhotoBySearchOrDefault("Moscow"));


        String expectedMessage = "Forbidden request";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testReturnNotFound() {
        when(restTemplate.exchange(
                eq(URL_MOSCOW),
                eq(HttpMethod.GET),
                any(),
                ArgumentMatchers.<Class<SearchResult>>any())
        )
                .thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));


        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> unsplashClient.getPhotoBySearchOrDefault("Moscow"));


        String expectedMessage = "Not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testReturnInternalServerError() {
        when(restTemplate.exchange(
                eq(URL_MOSCOW),
                eq(HttpMethod.GET),
                any(),
                ArgumentMatchers.<Class<SearchResult>>any())
        )
                .thenReturn(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));


        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> unsplashClient.getPhotoBySearchOrDefault("Moscow"));


        String expectedMessage = "Something went wrong on unsplash";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testReturnServerUnavailable() {
        when(restTemplate.exchange(
                eq(URL_MOSCOW),
                eq(HttpMethod.GET),
                any(),
                ArgumentMatchers.<Class<SearchResult>>any())
        )
                .thenReturn(new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE));


        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> unsplashClient.getPhotoBySearchOrDefault("Moscow"));


        String expectedMessage = "Something went wrong on unsplash";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    private static String getUrlForQuery(String query) {
        return "https://api.unsplash.com/search/photos?page=1&per_page=1&query=" + query;
    }
}
