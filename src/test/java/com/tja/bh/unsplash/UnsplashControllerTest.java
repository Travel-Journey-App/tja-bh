package com.tja.bh.unsplash;

import com.tja.bh.application.Application;
import com.tja.bh.config.jwt.JwtProvider;
import com.tja.bh.service.IUserService;
import com.tja.bh.unsplash.api.UnsplashClient;
import com.tja.bh.unsplash.api.UnsplashController;
import com.tja.bh.unsplash.dto.Photo;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UnsplashController.class)
@ContextConfiguration(classes = Application.class)
@AutoConfigureEmbeddedDatabase(beanName = "dataSource")
public class UnsplashControllerTest {

    @MockBean
    private UnsplashClient unsplashClient;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private IUserService userService;

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(roles = "USER")
    public void emptyQueryThrowsError() throws Exception {
        when(unsplashClient.getPhotoBySearchOrDefault("test1"))
                .thenThrow(IllegalArgumentException.class);

        mvc.perform(get("/api/unsplash/search/test1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void emptyQueryReturnsRequest() throws Exception {
        final Photo photo = new Photo();
        when(unsplashClient.getPhotoBySearchOrDefault("test"))
                .thenReturn(photo);

        mvc.perform(get("/api/unsplash/search/test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
