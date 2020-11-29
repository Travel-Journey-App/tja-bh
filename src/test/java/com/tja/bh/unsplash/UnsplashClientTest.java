package com.tja.bh.unsplash;

import com.tja.bh.application.Application;
import com.tja.bh.config.LocalTestConfiguration;
import com.tja.bh.unsplash.api.UnsplashClient;
import com.tja.bh.unsplash.dto.Photo;
import com.tja.bh.unsplash.dto.SearchResult;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import javassist.tools.web.BadHttpRequest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AutoConfigureEmbeddedDatabase(beanName = "dataSource")
@RunWith(SpringRunner.class)
@ContextConfiguration(classes=Application.class)
@SpringBootTest(classes = LocalTestConfiguration.class)
public class UnsplashClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    UnsplashClient unsplashClient = new UnsplashClient("https://api.unsplash.com", "test");

    private final Photo photo = new Photo();
    private final SearchResult searchResult = new SearchResult(1, 1, new ArrayList<Photo>(Arrays.asList(photo)));

    @Test
    public void testEmptyQuery() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            unsplashClient.getPhotoBySearchOrDefault("");
        });

        String expectedMessage = "Search query must not be blank!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testBlankQuery() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            unsplashClient.getPhotoBySearchOrDefault("    ");
        });

        String expectedMessage = "Search query must not be blank!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testReturnPhoto() throws BadHttpRequest {
        Mockito.when(restTemplate.exchange(
                Matchers.eq(getUrlForQuery("Moscow")),
                Matchers.eq(HttpMethod.GET),
                Matchers.any(),
                Matchers.<Class<SearchResult>>any())
        )
          .thenReturn(new ResponseEntity(searchResult, HttpStatus.OK));

        Assert.assertEquals(searchResult.getResults().get(0), unsplashClient.getPhotoBySearchOrDefault("Moscow"));
    }

    @Test
    public void testReturnDefaultPhoto() throws BadHttpRequest {
        Mockito.when(restTemplate.exchange(
                Matchers.eq(getUrlForQuery("Moscow")),
                Matchers.eq(HttpMethod.GET),
                Matchers.any(),
                Matchers.<Class<SearchResult>>any())
        )
                .thenReturn(new ResponseEntity(new SearchResult(), HttpStatus.OK));

        Mockito.when(restTemplate.exchange(
                Matchers.eq(getUrlForQuery("Tokyo")),
                Matchers.eq(HttpMethod.GET),
                Matchers.any(),
                Matchers.<Class<SearchResult>>any())
        )
                .thenReturn(new ResponseEntity(searchResult, HttpStatus.OK));

        Assert.assertEquals(searchResult.getResults().get(0), unsplashClient.getPhotoBySearchOrDefault("Moscow"));
    }

    @Test
    public void testReturnBadHttpRequest() {
        Mockito.when(restTemplate.exchange(
                Matchers.eq(getUrlForQuery("Moscow")),
                Matchers.eq(HttpMethod.GET),
                Matchers.any(),
                Matchers.<Class<SearchResult>>any())
        )
                .thenReturn(new ResponseEntity(null, HttpStatus.BAD_REQUEST));


        Exception exception = assertThrows(BadHttpRequest.class, () -> {
            unsplashClient.getPhotoBySearchOrDefault("Moscow");
        });
    }

    @Test
    public void testReturnUnauthorized() {
        Mockito.when(restTemplate.exchange(
                Matchers.eq(getUrlForQuery("Moscow")),
                Matchers.eq(HttpMethod.GET),
                Matchers.any(),
                Matchers.<Class<SearchResult>>any())
        )
                .thenReturn(new ResponseEntity(null, HttpStatus.UNAUTHORIZED));


        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            unsplashClient.getPhotoBySearchOrDefault("Moscow");
        });


        String expectedMessage = "Unauthorized request";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testReturnForbidden() {
        Mockito.when(restTemplate.exchange(
                Matchers.eq(getUrlForQuery("Moscow")),
                Matchers.eq(HttpMethod.GET),
                Matchers.any(),
                Matchers.<Class<SearchResult>>any())
        )
                .thenReturn(new ResponseEntity(null, HttpStatus.FORBIDDEN));


        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            unsplashClient.getPhotoBySearchOrDefault("Moscow");
        });


        String expectedMessage = "Forbidden request";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testReturnNotFound() {
        Mockito.when(restTemplate.exchange(
                Matchers.eq(getUrlForQuery("Moscow")),
                Matchers.eq(HttpMethod.GET),
                Matchers.any(),
                Matchers.<Class<SearchResult>>any())
        )
                .thenReturn(new ResponseEntity(null, HttpStatus.NOT_FOUND));


        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            unsplashClient.getPhotoBySearchOrDefault("Moscow");
        });


        String expectedMessage = "Not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testReturnInternalServerError() {
        Mockito.when(restTemplate.exchange(
                Matchers.eq(getUrlForQuery("Moscow")),
                Matchers.eq(HttpMethod.GET),
                Matchers.any(),
                Matchers.<Class<SearchResult>>any())
        )
                .thenReturn(new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR));


        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            unsplashClient.getPhotoBySearchOrDefault("Moscow");
        });


        String expectedMessage = "Something went wrong on unsplash";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testReturnServerUnavailable() {
        Mockito.when(restTemplate.exchange(
                Matchers.eq(getUrlForQuery("Moscow")),
                Matchers.eq(HttpMethod.GET),
                Matchers.any(),
                Matchers.<Class<SearchResult>>any())
        )
                .thenReturn(new ResponseEntity(null, HttpStatus.SERVICE_UNAVAILABLE));


        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            unsplashClient.getPhotoBySearchOrDefault("Moscow");
        });


        String expectedMessage = "Something went wrong on unsplash";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    private String getUrlForQuery(String query) {
        return "https://api.unsplash.com/search/photos?page=1&per_page=1&query=" + query;
    }

    private HttpEntity getRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Client-ID " + "test");
        return new HttpEntity(headers);
    }
}
