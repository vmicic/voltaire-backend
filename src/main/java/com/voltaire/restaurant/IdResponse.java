package com.voltaire.restaurant;

import java.io.Serializable;

public class IdResponse implements Serializable {

    private Long id;

    public IdResponse(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
