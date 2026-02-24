package com.gymapp.model;

import java.io.Serializable;

public class MonitorId implements Serializable {
    private int id;

    // Constructor
    public MonitorId(int id) {
        this.id = id;
    }

    // Getter
    public int getId() {
        return id;
    }

    // Setter
    public void setId(int id) {
        this.id = id;
    }
}