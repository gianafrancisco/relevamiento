package com.fransis.model;

/**
 * Created by francisco on 13/01/14.
 */
public class Obra {
    private String nombre;
    private Integer id;

    public Obra(String nombre, Integer id) {
        this.nombre = nombre.trim();
        this.id = id;
    }

    public String getNombre() {
        return nombre.trim();
    }

    public void setNombre(String nombre) {
        this.nombre = nombre.trim();
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
