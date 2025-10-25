package cargadorDinamico.Dominio.HechosYColeccionesD;

import utils.Dominio.HechosYColecciones.Ubicacion;

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
    @Column(name = "id_ubicacion", length = 36, updatable = false, nullable = false)
    private UUID id_ubicacion;

    private double latitud;
    private double longitud;
    private String descripcion; // Para ubicaciones que no son Lat/Lon o para una descripción más detallada

    // Constructor 1: Para Latitud y Longitud, con una descripción por defecto
    public Ubicacion_D(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = "Coordenadas: " + latitud + ", " + longitud;
    }

    // Constructor 2: Para Latitud, Longitud y una descripción específica
    public Ubicacion_D(double latitud, double longitud, String descripcion) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = descripcion;
    }

    // Constructor 3: Para ubicaciones que son solo un String (nombre, dirección)
    public Ubicacion_D(String descripcion) {
        this.descripcion = descripcion;
        this.latitud = -999.0; // Valor por defecto o marcador para indicar que no hay Lat/Lon
        this.longitud = -999.0; // Valor por defecto o marcador
    }
    public Ubicacion_D(Ubicacion ubicacionAgregador) {
        this.id_ubicacion = ubicacionAgregador.getId_ubicacion();
        this.latitud = ubicacionAgregador.getLatitud();
        this.longitud = ubicacionAgregador.getLongitud();
        this.descripcion = ubicacionAgregador.getDescripcion();
    }

    // Constructor 4: Sin parámetros, para "Ubicación Desconocida"
    public Ubicacion_D() {
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

