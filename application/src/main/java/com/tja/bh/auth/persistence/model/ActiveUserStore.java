package com.tja.bh.auth.persistence.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class ActiveUserStore implements Serializable {
    public List<String> users;

    public ActiveUserStore() {
        users = new ArrayList<>();
    }
}