package com.tja.bh.unsplash;

import com.tja.bh.application.Application;
import com.tja.bh.config.LocalTestConfiguration;
import com.tja.bh.unsplash.api.UnsplashClient;
import com.tja.bh.unsplash.api.UnsplashController;
import com.tja.bh.unsplash.dto.Photo;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(UnsplashController.class)
@AutoConfigureEmbeddedDatabase(beanName = "dataSource")
@ContextConfiguration(classes=Application.class)
@AutoConfigureMockMvc(addFilters = false)
public class UnsplashControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UnsplashClient unsplashClient;

    @Test
    public void emptyQueryThrowsError() throws Exception {
        given(unsplashClient.getPhotoBySearchOrDefault("test1")).willThrow(IllegalArgumentException.class);
        mvc.perform(get("/api/unsplash/search/test1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void emptyQueryReturnsRequest() throws Exception {
        final Photo photo = new Photo();
        given(unsplashClient.getPhotoBySearchOrDefault("test")).willReturn(photo);
        mvc.perform(get("/api/unsplash/search/test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
