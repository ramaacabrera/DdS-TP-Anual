package agregador.dto.Hechos;

import java.util.UUID;

public class UbicacionDTO {
    private UUID ubicacionId;
    private double latitud;
    private double longitud;
    private String descripcion;

    public UbicacionDTO() {}

    public UbicacionDTO(UUID ubicacionId, double latitud, double longitud, String descripcion) {
        this.ubicacionId = ubicacionId;
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public UUID getUbicacionId() { return ubicacionId; }
    public void setUbicacionId(UUID ubicacionId) { this.ubicacionId = ubicacionId; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}