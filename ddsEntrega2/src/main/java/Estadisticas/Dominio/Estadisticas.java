package Estadisticas.Dominio;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;
import java.util.UUID;

@Entity
public class Estadisticas {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "estadisticas_id", updatable = false, nullable = false)
    private UUID estadisticas_id;
    private Date estadisticas_fecha;
    private int spam;
    private String categoria_max_hechos;

    public void setEstadisticas_id(UUID estadisticas_id) {this.estadisticas_id = estadisticas_id;}
    public void setEstadisticas_fecha(Date estadisticas_fecha) {this.estadisticas_fecha = estadisticas_fecha;}
    public void setSpam(int spam){this.spam = spam;}
    public void setCategoria_max_hechos(String categoria){this.categoria_max_hechos = categoria;}

    public UUID getEstadisticas_id(){return  this.estadisticas_id;}
    public Date getEstadisticas_fecha() {return  estadisticas_fecha;}
    public int getSpam(){return spam;}
    public String getCategoria_max_hechos(){return  categoria_max_hechos;}

    public Estadisticas(){}
}
