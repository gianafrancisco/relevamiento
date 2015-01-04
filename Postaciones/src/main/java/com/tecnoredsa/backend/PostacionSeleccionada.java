package com.tecnoredsa.backend;

import com.tecnoredsa.model.Postacion;

/**
 * Created by francisco on 01/02/14.
 */
public class PostacionSeleccionada {
    private Postacion postacion=null;
    private Boolean insertar=null;
    private static PostacionSeleccionada ourInstance = new PostacionSeleccionada();

    public static PostacionSeleccionada getInstance() {
        return ourInstance;
    }

    private PostacionSeleccionada() {
    }

    public Postacion getPostacion() {
        return postacion;
    }

    public void setPostacion(Postacion postacion) {
        this.postacion = postacion;
    }

    public Boolean getInsertar() {
        return insertar;
    }

    public void setInsertar(Boolean insertar) {
        this.insertar = insertar;
    }
}
