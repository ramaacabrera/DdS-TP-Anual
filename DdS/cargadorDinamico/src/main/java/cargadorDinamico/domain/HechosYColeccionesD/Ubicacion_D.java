package cargadorDinamico.domain.HechosYColeccionesD;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Ubicacion_D {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type = "uuid-char")
    @Column(name = "ubicacionId", length = 36, updatable = false, nullable = false)
    private UUID ubicacionId;

    private double latitud;
    private double longitud;
    private String descripcion;

    // Constructor 1: Para Latitud y Longitud, con una descripción por defecto
    public Ubicacion_D(double latitud, double longitud, String descripcion) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = descripcion;
    }

    public Ubicacion_D() {}

    // Getters
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public UUID getUbicacionId() {return ubicacionId;}
    public String getDescripcion() {return descripcion;}

    // Setters (si son necesarios, para modificar después de la creación)
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public void setUbicacionId(UUID id_ubicacion) {this.ubicacionId = id_ubicacion;}
    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}
}

