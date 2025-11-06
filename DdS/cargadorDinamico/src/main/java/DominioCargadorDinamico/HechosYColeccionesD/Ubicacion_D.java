package DominioCargadorDinamico.HechosYColeccionesD;

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

    // Constructor 1: Para Latitud y Longitud, con una descripción por defecto
    public Ubicacion_D(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Ubicacion_D() {}

    // Getters
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public UUID getId_ubicacion() {return id_ubicacion;}

    // Setters (si son necesarios, para modificar después de la creación)
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public void setId_ubicacion(UUID id_ubicacion) {this.id_ubicacion = id_ubicacion;}
}

