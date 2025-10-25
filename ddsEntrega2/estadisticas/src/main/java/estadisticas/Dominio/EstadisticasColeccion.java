package estadisticas.Dominio;

import javax.persistence.*;

@Entity
public class EstadisticasColeccion {

    @EmbeddedId
    private EstadisticasColeccionId id;

    private String estadisticasColeccion_provincia;

    @ManyToOne
    @MapsId("estadisticas_id")
    @JoinColumn(name = "estadisticas_id")
    private Estadisticas estadisticas;

    public void setId(EstadisticasColeccionId id) {this.id = id;}
    public void setEstadisticasColeccion_provincia(String estadisticasColeccion_provincia){this.estadisticasColeccion_provincia = estadisticasColeccion_provincia;}

    public EstadisticasColeccionId getId(){return this.id;}
    public String getEstadisticasColeccion_provincia(){return this.estadisticasColeccion_provincia;}

    public EstadisticasColeccion(){}
    public EstadisticasColeccion(EstadisticasColeccionId id, String estadisticasColeccion_provincia){
        this.id = id;
        this.estadisticasColeccion_provincia = estadisticasColeccion_provincia;
    }
}
