package cargadorDemo.domain.HechosYColecciones;


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
    private UUID ubicacionId;

    private double latitud;
    private double longitud;
    private String descripcion;

    public Ubicacion(){}

    public Ubicacion(double latitud, double longitud,  String descripcion) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = descripcion;
    }

    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public UUID getUbicacionId() {return ubicacionId;}
    public String getDescripcion() {return descripcion;}

    public void setLatitud(double latitud) { this.latitud = latitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public void setUbicacionId(UUID ubicacionId) {this.ubicacionId = ubicacionId;}
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}


