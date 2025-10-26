package estadisticas.Dominio;

import javax.persistence.*;


@Entity
public class EstadisticasCategoria {

    @EmbeddedId
    private EstadisticasCategoriaId id;
    private String estadisticasCategoria_provincia;
    private Integer estadisticasCategoria_hora;

    @ManyToOne
    @MapsId("estadisticas_id")
    @JoinColumn(name = "estadisticas_id")
    private Estadisticas estadisticas;

    public EstadisticasCategoria(){}

    public EstadisticasCategoria(EstadisticasCategoriaId id, String estadisticasCategoria_provincia, Integer estadisticasCategoria_hora) {
        this.id = id;
        this.estadisticasCategoria_provincia = estadisticasCategoria_provincia;
        this.estadisticasCategoria_hora = estadisticasCategoria_hora;
    }

    public void setId(EstadisticasCategoriaId id) {this.id = id;}
    public void setProvincia(String provincia) {this.estadisticasCategoria_provincia = provincia;}
    public void setEstadisticasCategoria_hora(Integer estadisticasCategoria_hora){this.estadisticasCategoria_hora = estadisticasCategoria_hora;}

    public EstadisticasCategoriaId getId() {return this.id;}
    public String getProvincia(){return this.estadisticasCategoria_provincia;}
    public Integer getEstadisticasCategoria_hora(){return this.estadisticasCategoria_hora;}

}
