package Estadisticas.Dominio;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.sql.Time;
import java.util.UUID;

@Entity
public class EstadisticasCategoria {

    @EmbeddedId
    private EstadisticasCategoriaId id;
    private String provincia;
    private Time hora;

    public EstadisticasCategoria(){}

    public void setId(EstadisticasCategoriaId id) {this.id = id;}
    public void setProvincia(String provincia) {this.provincia = provincia;}
    public void setHora(Time hora){this.hora = hora;}

    public EstadisticasCategoriaId getId() {return this.id;}
    public String getProvincia(){return this.provincia;}
    public Time getHora(){return this.hora;}

}
