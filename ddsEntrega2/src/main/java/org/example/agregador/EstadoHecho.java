package org.example.agregador;

public enum EstadoHecho {
    ACTIVO("Activo"),
    OCULTO("OCULTO");

    private final String descripcion;

    private EstadoHecho(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
