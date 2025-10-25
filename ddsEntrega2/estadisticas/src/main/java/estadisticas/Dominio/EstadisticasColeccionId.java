package estadisticas.Dominio;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class EstadisticasColeccionId implements Serializable {
    private UUID estadisticas_id;
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
