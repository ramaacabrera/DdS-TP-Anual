package org.example.agregador;

public enum TipoDeFuente {
    ESTATICA("ESTATICA"),
    DINAMICA("DINAMICA"),
    PROXY("PROXY");

    private final String descripcion;

    private TipoDeFuente(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}