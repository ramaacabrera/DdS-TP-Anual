package gestorPublico.dto.Hechos;

import gestorPublico.domain.HechosYColecciones.Ubicacion;

import java.util.UUID;

public class UbicacionDTO {

    private UUID ubicacionId;
    private double latitud;
    private double longitud;
    private String descripcion; // Nuevo campo agregado

    public UbicacionDTO() {}

    public UbicacionDTO(UUID id_ubicacion, double latitud, double longitud, String descripcion) {
        this.ubicacionId = id_ubicacion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = descripcion;
    }
    public UbicacionDTO(Ubicacion u){
        this.descripcion = u.getDescripcion();
        this.latitud = u.getLatitud();
        this.longitud = u.getLongitud();
        this.ubicacionId = u.getId_ubicacion();
    }

    // --- Getters y Setters ---

    public UUID getUbicacionId() {
        return ubicacionId;
    }

    public void setUbicacionId(UUID id_ubicacion) {
        this.ubicacionId = id_ubicacion;
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