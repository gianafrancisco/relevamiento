package com.tecnoredsa.model;

/**
 * Created by francisco on 13/01/14.
 */
public class Postacion {
    private Integer obra_id;
    private Integer postacion_id;
    private Double gps_longitude;
    private Double gps_latitude;
    private String tipo;
    private String preformada;
    private String empalme;
    private String ganancia;
    private String agregar;
    private String detalle_adicional;
    private String mensula_prolongada;
    private String codificacion;
    private Integer numero;

    public Postacion(Integer postacion_id) {
        this.postacion_id = postacion_id;
        gps_latitude=new Double(0);
        gps_longitude=new Double(0);
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPreformada() {
        return preformada;
    }

    public void setPreformada(String preformada) {
        this.preformada = preformada;
    }

    public String getEmpalme() {
        return empalme;
    }

    public void setEmpalme(String empalme) {
        this.empalme = empalme;
    }

    public String getGanancia() {
        return ganancia;
    }

    public void setGanancia(String ganancia) {
        this.ganancia = ganancia;
    }

    public String getAgregar() {
        return agregar;
    }

    public void setAgregar(String agregar) {
        this.agregar = agregar;
    }

    public String getDetalle_adicional() {
        return detalle_adicional;
    }

    public void setDetalle_adicional(String detalle_adicional) {
        this.detalle_adicional = detalle_adicional;
    }

    public Integer getObra_id() {
        return obra_id;
    }

    public void setObra_id(Integer obra_id) {
        this.obra_id = obra_id;
    }

    public Integer getPostacion_id() {
        return postacion_id;
    }

    public void setPostacion_id(Integer postacion_id) {
        this.postacion_id = postacion_id;
    }

    public String getMensula_prolongada() {
        return mensula_prolongada;
    }

    public void setMensula_prolongada(String mensula_prolongada) {
        this.mensula_prolongada = mensula_prolongada;
    }
    public String toString(){
        return getPostacion_id().toString()+"\n"+getTipo()+"\n"+getPreformada()+"\n";
    }

    public Double getGps_latitude() {
        return gps_latitude;
    }

    public void setGps_latitude(Double gps_latitude) {
        this.gps_latitude = gps_latitude;
    }

    public Double getGps_longitude() {
        return gps_longitude;
    }

    public void setGps_longitude(Double gps_longitude) {
        this.gps_longitude = gps_longitude;
    }

    public String getCodificacion() {
        String tipo;
        if(this.tipo.equals("Cemento")){
            tipo="-PC";
        }else if(this.tipo.equals("Madera")){
            tipo="-PMa";
        }else if(this.tipo.equals("Alumbrado público")){
            tipo="-PA";
        }else if(this.tipo.equals("Mensula")){
            tipo="-PMe";
        }else if(this.tipo.equals("Fachada")){
            tipo="-PF";
        }else{
            tipo="-PO";
        }
        //String ret_sus=(preformada.equals("Retención"))?"-R":"-S";
        String ret_sus;

        if(this.preformada.equals("Retención")){
            ret_sus="-R";
        }else if(this.preformada.equals("Suspensión")){
            ret_sus="-S";
        }else{
            ret_sus="-T";
        }


        String ganancia=(this.ganancia.equals("Si"))?"-G":"";
        String empalme=(this.empalme.equals("Si"))?"-E":"";
        String men_prolongada=(mensula_prolongada.equals("Si"))?"-MP":"";
        String nuevo=(agregar.equals("Si"))?"-N":"";

        codificacion = numero.toString()+tipo+ret_sus+ganancia+empalme+men_prolongada+nuevo;
        return codificacion;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getImagenTipo() {
        String tipo;
        if(this.tipo.equals("Cemento")){
            tipo="PC";
        }else if(this.tipo.equals("Madera")){
            tipo="PMa";
        }else if(this.tipo.equals("Alumbrado público")){
            tipo="PA";
        }else if(this.tipo.equals("Mensula")){
            tipo="PMe";
        }else if(this.tipo.equals("Fachada")){
            tipo="PF";
        }else{
            tipo="PO";
        }
        return tipo;
    }

    /*public String getCodificacion() {

        String ganancia=(this.ganancia.equals("Si"))?"-G":"";
        String empalme=(this.empalme.equals("Si"))?"-E":"";
        String men_prolongada=(mensula_prolongada.equals("Si"))?"-MP":"";
        String nuevo=(agregar.equals("Si"))?"-N":"";
        return codificacion;
    }*/


}
