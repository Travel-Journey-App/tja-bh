package com.tja.bh.controller;

import com.tja.bh.dto.GenericResponse;
import com.tja.bh.persistence.model.Trip;
import com.tja.bh.persistence.repository.TripRepository;
import com.tja.bh.service.IUserService;
import com.tja.bh.unsplash.api.UnsplashController;
import com.tja.bh.unsplash.dto.Photo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static com.tja.bh.dto.GenericResponse.Status.ERROR;
import static com.tja.bh.dto.GenericResponse.Status.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TripControllerTest {

    @Mock
    private TripRepository repository;

    @Mock
    private UnsplashController unsplashController;

    @Mock
    private IUserService userService;

    @InjectMocks
    private TripController controller;

    private Trip trip;

    @BeforeEach
    public void setUp() {
        trip = Trip.builder()
                .id(1L)
                .cover("test")
                .name("test")
                .days(List.of())
                .destination("test")
                .startDate(new Date(2021, 1, 1))
                .endDate(new Date(2021, 1, 2))
                .build();
    }

    @Test
    public void createTrip() {
        when(unsplashController.getPhoto(anyString())).thenReturn(GenericResponse.success(new Photo()));
        when(repository.saveAndFlush(any(Trip.class))).thenReturn(trip);

        GenericResponse<Trip> response = controller.createTrip(trip);
        assertEquals(OK, response.getStatus());
        assertEquals(trip, response.getBody());
        assertEquals(2, response.getBody().getDays().size());
    }

    @Test
    public void createTripWithPhotoError() {
        when(unsplashController.getPhoto(anyString()))
                .thenReturn(GenericResponse.error("error"));

        GenericResponse<Trip> response = controller.createTrip(trip);
        assertEquals(ERROR, response.getStatus());
        assertNull(response.getBody());
    }

}
