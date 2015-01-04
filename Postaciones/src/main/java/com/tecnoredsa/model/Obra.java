package com.tecnoredsa.model;

import android.support.v7.appcompat.R;

/**
 * Created by francisco on 13/01/14.
 */
public class Obra {
    private String nombre;
    private Integer id;

    public Obra(String nombre, Integer id) {
        this.nombre = nombre;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String toString(){
        return getNombre();
    }
}
