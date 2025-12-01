package cargadorEstatico.domain.HechosYColecciones;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "Ubicacion")
public class Ubicacion {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type = "uuid-char")
    @Column(name = "id_ubicacion", length = 36 , updatable = false, nullable = false)
    private UUID id_ubicacion;

    private double latitud;
    private double longitud;

    public Ubicacion(){}

    public Ubicacion(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public UUID getId_ubicacion() {return id_ubicacion;}

    public void setLatitud(double latitud) { this.latitud = latitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public void setId_ubicacion(UUID id_ubicacion) {this.id_ubicacion = id_ubicacion;}
}

