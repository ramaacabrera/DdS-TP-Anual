package org.example;

public enum EstadoSolicitudModificacion {
    PENDIENTE("PENDIENTE"),
    ACEPTADA("ACEPTADA"),
    RECHAZADA("RECHAZADA"),
    ACEPTADACONSUGERENCIA("ACEPTADACONSUGERENCIA");

    private final String descripcion;

    private EstadoSolicitudModificacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
