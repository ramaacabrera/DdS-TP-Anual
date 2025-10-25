package estadisticas.Dominio;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class EstadisticasCategoriaId implements Serializable {
    private UUID estadisticas_id;
    private String categoria;

    public  EstadisticasCategoriaId() {}
    public EstadisticasCategoriaId(UUID estadisticasCategoria_estadisticas, String estadisticasCategoria_categoria) {
        this.estadisticas_id = estadisticasCategoria_estadisticas;
        this.categoria = estadisticasCategoria_categoria;
    }

    public void setEstadisticas_id(UUID estadisticasCategoria_estadisticas) {this.estadisticas_id = estadisticasCategoria_estadisticas;}
    public void setCategoria(String estadisticasCategoria_categoria){this.categoria = estadisticasCategoria_categoria;}

    public UUID getEstadisticas_id() {return this.estadisticas_id;}
    public  String getCategoria() {return this.categoria;}
}
