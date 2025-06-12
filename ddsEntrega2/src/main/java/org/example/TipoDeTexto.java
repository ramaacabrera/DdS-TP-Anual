package org.example;

public enum TipoDeTexto {
    TITULO("TITULO"),
    DESCRIPCION("DESCRIPCION"),
    CATEGORIA("CATEGORIA");

    private final String descripcion;

    private TipoDeTexto(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}