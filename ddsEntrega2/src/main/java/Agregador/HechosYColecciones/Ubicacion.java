package Agregador.HechosYColecciones;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;
import javax.persistence.*;

@Entity
public class Ubicacion {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id_ubicacion", updatable = false, nullable = false)
    private UUID id_ubicacion;

    private double latitud;
    private double longitud;
    private String descripcion; // Para ubicaciones que no son Lat/Lon o para una descripción más detallada

    // Constructor 1: Para Latitud y Longitud, con una descripción por defecto
    public Ubicacion(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = "Coordenadas: " + latitud + ", " + longitud;
    }

    // Constructor 2: Para Latitud, Longitud y una descripción específica
    public Ubicacion(double latitud, double longitud, String descripcion) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = descripcion;
    }

    // Constructor 3: Para ubicaciones que son solo un String (nombre, dirección)
    public Ubicacion(String descripcion) {
        this.descripcion = descripcion;
        this.latitud = -999.0; // Valor por defecto o marcador para indicar que no hay Lat/Lon
        this.longitud = -999.0; // Valor por defecto o marcador
    }

    // Constructor 4: Sin parámetros, para "Ubicación Desconocida"
    public Ubicacion() {
        this.descripcion = "Ubicación Desconocida";
        this.latitud = -999.0;
        this.longitud = -999.0;
    }

    // Getters
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public String getDescripcion() { return descripcion; }
    public UUID getId_ubicacion() {return id_ubicacion;}

    // Setters (si son necesarios, para modificar después de la creación)
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setId_ubicacion(UUID id_ubicacion) {this.id_ubicacion = id_ubicacion;}

    // Opcional: toString para fácil depuración
    @Override
    public String toString() {
        if (latitud != -999.0 && longitud != -999.0) {
            return "Ubicacion{lat=" + latitud + ", lon=" + longitud + ", desc='" + descripcion + "'}";
        } else {
            return "Ubicacion{desc='" + descripcion + "'}";
        }
    }
}

