package Estadisticas.Dominio;

import javax.persistence.*;

@Entity
public class EstadisticasColeccion {

    @EmbeddedId
    private EstadisticasColeccionId id;

    private String provincia;

    public void setId(EstadisticasColeccionId id) {this.id = id;}
    public void setProvincia(String provincia){this.provincia = provincia;}

    public EstadisticasColeccionId getId(){return this.id;}
    public String getProvincia(){return this.provincia;}

    public EstadisticasColeccion(){}
    public EstadisticasColeccion(EstadisticasColeccionId id, String provincia){
        this.id = id;
        this.provincia = provincia;
    }
}
