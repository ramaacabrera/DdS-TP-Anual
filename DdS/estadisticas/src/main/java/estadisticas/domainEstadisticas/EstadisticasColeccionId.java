package estadisticas.domainEstadisticas;

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
    private String estadisticasColeccion_titulo;

    public  EstadisticasColeccionId() {}
    public EstadisticasColeccionId(UUID estadisticas_id, UUID estadisticasColeccion_coleccion, String estadisticasColeccion_tituloNuevo) {
        this.estadisticas_id = estadisticas_id;
        this.coleccion_id = estadisticasColeccion_coleccion;
        this.estadisticasColeccion_titulo = estadisticasColeccion_tituloNuevo;
    }

    public void setEstadisticas_id(UUID  estadisticas_id) {this.estadisticas_id = estadisticas_id;}
    public void setColeccion_id(UUID estadisticasColeccion_coleccion){this.coleccion_id = estadisticasColeccion_coleccion;}

    public UUID getEstadisticas_id() {return this.estadisticas_id;}
    public  UUID getColeccion_id() {return this.coleccion_id;}

    public String getEstadisticasColeccion_titulo() {
        return estadisticasColeccion_titulo;
    }

    public void setEstadisticasColeccion_titulo(String estadisticasColeccion_titulo) {
        this.estadisticasColeccion_titulo = estadisticasColeccion_titulo;
    }
}
