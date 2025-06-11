package org.example;

public class Ubicacion {

    public Integer latitud;
    public Integer longitud;

    // Constructor
    public Ubicacion(Integer latitud, Integer longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public void setLatitud(Integer lat) {
        this.latitud = lat;
    }

    public Integer getLatitud() {
        return this.latitud;
    }
    public void setLongitud(Integer lon) {
        this.longitud = lon;
    }

    public Integer getLongitud() {
        return this.longitud;
    }
}
