package org.example.agregador;

public enum EstadoSolicitudEliminacion {
    PENDIENTE("PENDIENTE"),
    ACEPTADA("ACEPTADA"),
    RECHAZADA("RECHAZADA");

    private final String descripcion;

    private EstadoSolicitudEliminacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
