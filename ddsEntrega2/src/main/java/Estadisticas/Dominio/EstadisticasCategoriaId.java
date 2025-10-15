package Estadisticas.Dominio;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class EstadisticasCategoriaId implements Serializable {
    private UUID estadisticas_id;
    private String categoria;

    public  EstadisticasCategoriaId() {}
    public EstadisticasCategoriaId(UUID estadisticas_id, String categoria) {
        this.estadisticas_id = estadisticas_id;
        this.categoria = categoria;
    }

    public void setEstadisticas_id(UUID  estadisticas_id) {this.estadisticas_id = estadisticas_id;}
    public void setCategoria(String categoria){this.categoria = categoria;}

    public UUID getEstadisticas_id() {return this.estadisticas_id;}
    public  UUID getCategoria() {return this.categoria;}
}
