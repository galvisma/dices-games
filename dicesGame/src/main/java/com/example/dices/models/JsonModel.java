package com.example.dices.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JsonModel {

    public int id;

    public int roll;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

    public int getRoll() {
        return roll;
    }

}

