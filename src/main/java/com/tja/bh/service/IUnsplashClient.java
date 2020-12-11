package com.tja.bh.service;

import com.tja.bh.unsplash.dto.Photo;
import javassist.tools.web.BadHttpRequest;

public interface IUnsplashClient {

    Photo getPhotoBySearchOrDefault(final String query) throws BadHttpRequest;

}
