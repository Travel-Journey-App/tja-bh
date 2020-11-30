package com.tja.bh.trip.controller;

import com.tja.bh.application.Application;
import com.tja.bh.auth.GenericResponse;
import com.tja.bh.auth.controller.TripController;
import com.tja.bh.auth.persistence.model.Trip;
import com.tja.bh.auth.persistence.repository.TripRepository;
import com.tja.bh.config.LocalTestConfiguration;
import com.tja.bh.unsplash.api.UnsplashController;
import com.tja.bh.unsplash.dto.Photo;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = Application.class)
@AutoConfigureEmbeddedDatabase(beanName = "dataSource")
public class TripControllerTest {

    @Autowired
    TestEntityManager entityManager;

    @Mock
    private TripRepository repository;

    @Mock
    private UnsplashController unsplashController;

    @InjectMocks
    private TripController controller;

    @Test
    public void createTrip() {
        final Trip trip = Trip.builder()
                .id(1l)
                .cover("test")
                .name("test")
                .days(List.of())
                .destination("test")
                .startDate(new Date())
                .endDate(new Date())
                .build();
        when(unsplashController.getPhoto(anyString())).thenReturn(GenericResponse.success(new Photo()));
        when(repository.saveAndFlush(any(Trip.class))).thenReturn(trip);

        Trip createdTrip = controller.createTrip(trip).getBody();
        assertThat(trip).isEqualTo(createdTrip);
    }

    @Test
    public void createTripWithPhotoError() {
        final Trip trip = Trip.builder()
                .id(1l)
                .cover("test")
                .name("test")
                .days(List.of())
                .destination("test")
                .startDate(new Date())
                .endDate(new Date())
                .build();
        when(unsplashController.getPhoto(anyString())).thenReturn(GenericResponse.error());

        assertThat(controller.createTrip(trip).getStatus()).isEqualTo(GenericResponse.Status.ERROR);
    }

}
