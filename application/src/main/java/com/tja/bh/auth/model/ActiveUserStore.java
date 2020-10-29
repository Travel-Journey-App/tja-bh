package com.tja.bh.auth.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ActiveUserStore {
    public List<String> users;

    public ActiveUserStore() {
        users = new ArrayList<>();
    }
}