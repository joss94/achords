package com.joss.achords.Models;

import java.util.UUID;

/**
 * Created by Joss on 05/02/2017.
 */

public class User {

    String name;
    UUID id;

    public User() {
        id = UUID.randomUUID();
    }

    public User(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
