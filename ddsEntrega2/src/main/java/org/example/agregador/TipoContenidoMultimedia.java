package org.example.agregador;

public enum TipoContenidoMultimedia {
    IMAGEN("IMAGEN"),
    VIDEO("VIDEO"),
    AUDIO("AUDIO");

    private final String descripcion;

    private TipoContenidoMultimedia(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}