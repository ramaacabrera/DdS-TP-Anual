package cargadorDinamico.domain.DinamicaDto;

import cargadorDinamico.domain.HechosYColeccionesD.Ubicacion_D;

import java.util.UUID;

public class UbicacionDTO {

    private UUID id_ubicacion;
    private double latitud;
    private double longitud;
    private String descripcion; // Nuevo campo agregado

    public UbicacionDTO() {}

    public UbicacionDTO(UUID id_ubicacion, double latitud, double longitud, String descripcion) {
        this.id_ubicacion = id_ubicacion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = descripcion;
    }

    public UbicacionDTO(Ubicacion_D entidad) {
        if (entidad != null) {
            this.id_ubicacion = entidad.getUbicacionId();
            this.latitud = entidad.getLatitud();
            this.longitud = entidad.getLongitud();
            this.descripcion = entidad.getDescripcion();
        }
    }

    // --- Getters y Setters ---

    public UUID getUbicacionId() {
        return id_ubicacion;
    }

    public void setUbicacionId(UUID id_ubicacion) {
        this.id_ubicacion = id_ubicacion;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}