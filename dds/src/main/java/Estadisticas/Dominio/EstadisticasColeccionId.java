package Estadisticas.Dominio;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class EstadisticasColeccionId implements Serializable {
    @Type(type = "uuid-char")
    @Column(length = 36)
    private UUID estadisticas_id;
    @Type(type = "uuid-char")
    @Column(length = 36)
    private UUID coleccion_id;

    public  EstadisticasColeccionId() {}
    public EstadisticasColeccionId(UUID estadisticas_id, UUID estadisticasColeccion_coleccion) {
        this.estadisticas_id = estadisticas_id;
        this.coleccion_id = estadisticasColeccion_coleccion;
    }

    public void setEstadisticas_id(UUID  estadisticas_id) {this.estadisticas_id = estadisticas_id;}
    public void setColeccion_id(UUID estadisticasColeccion_coleccion){this.coleccion_id = estadisticasColeccion_coleccion;}

    public UUID getEstadisticas_id() {return this.estadisticas_id;}
    public  UUID getColeccion_id() {return this.coleccion_id;}
}
