package com.tja.bh.library.vo;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class UserTest {

    @Test
    public void dummyTest() {
        assertNotNull(User.builder().id(0L).build());
    }
}