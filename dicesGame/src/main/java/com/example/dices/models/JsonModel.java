package com.example.dices.models;

import java.util.Optional;

public class JsonModel {

    public int id;

    public Optional<Integer> roll;

    public int getId() {
        return id;
    }

    public JsonModel(int id, Optional<Integer> roll) {
        this.id = id;
        this.roll = roll;
    }

    public Optional<Integer> getRoll() {
        return roll;
    }

}

