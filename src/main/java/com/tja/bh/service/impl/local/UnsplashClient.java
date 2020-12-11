package com.tja.bh.service.impl.local;

import com.tja.bh.service.IUnsplashClient;
import com.tja.bh.unsplash.dto.Photo;
import javassist.tools.web.BadHttpRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("default")
@Service
public class UnsplashClient implements IUnsplashClient {

    @Override
    public Photo getPhotoBySearchOrDefault(String query) throws BadHttpRequest {
        return new Photo();
    }

}
