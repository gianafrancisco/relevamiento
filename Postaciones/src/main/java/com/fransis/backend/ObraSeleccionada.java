package com.fransis.backend;

import com.fransis.model.Obra;

/**
 * Created by francisco on 23/01/14.
 */
public class ObraSeleccionada {
    private static ObraSeleccionada ourInstance = new ObraSeleccionada();
    private Obra obraSeleccionada=null;
    public static ObraSeleccionada getInstance() {
        return ourInstance;
    }

    private ObraSeleccionada() {
    }

    public Obra getObraSeleccionada() {
        return obraSeleccionada;
    }

    public void setObraSeleccionada(Obra obraSeleccionada) {
        this.obraSeleccionada = obraSeleccionada;
    }
}
