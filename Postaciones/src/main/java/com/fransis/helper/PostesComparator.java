package com.fransis.helper;

import com.fransis.model.Postacion;

import java.util.Comparator;

/**
 * Created by francisco on 02/07/14.
 *
 * Ordena el array en forma asecendente para poder imprimir el html en forma correcta
 *
 */



public class PostesComparator implements Comparator<Postacion> {
    public int compare(Postacion o1, Postacion o2) {
        return o1.getPostacion_id() - o2.getPostacion_id();
    }
}
