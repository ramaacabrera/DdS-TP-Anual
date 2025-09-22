package Agregador.fuente;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import utils.DTO.FuenteDTO;
import java.util.UUID;
//import com.fasterxml.jackson.annotation.JsonSubTypes;
//import com.fasterxml.jackson.annotation.JsonTypeInfo;
//import utils.DTO.HechoDTO;

//import java.util.List;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;
import javax.persistence.*;
//@JsonTypeInfo(
//        use = JsonTypeInfo.Id.NAME,
//        include = JsonTypeInfo.As.PROPERTY,
//        property = "tipoDeFuente"
//)
//@JsonSubTypes({
//        @JsonSubTypes.Type(value = FuenteEstatica.class, name = "ESTATICA"),
//        @JsonSubTypes.Type(value = FuenteDinamica.class, name = "DINAMICA"),
//        @JsonSubTypes.Type(value = FuenteProxy.class, name = "PROXY")
//})

@Entity
public class Fuente {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id_fuente", updatable = false, nullable = false)
    private UUID id_fuente;

    @Enumerated(EnumType.STRING)
    private TipoDeFuente tipoDeFuente;
    private String ruta;

    public Fuente() {}

    public Fuente(TipoDeFuente tipoDeFuente, String ruta /*, int idNuevo*/) {
        this.tipoDeFuente = tipoDeFuente;
        this.ruta = ruta;
        //this.id_fuente = idNuevo;
    }

    public Fuente(FuenteDTO fuenteDTO, int idNuevo) {
        this.tipoDeFuente = fuenteDTO.getTipoDeFuente();
        this.ruta = fuenteDTO.getRuta();
        //this.id_fuente = idNuevo;
    }

    public TipoDeFuente getTipoDeFuente() {
        return tipoDeFuente;
    }

    public void setTipoDeFuente(TipoDeFuente tipoDeFuente) {
        this.tipoDeFuente = tipoDeFuente;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public UUID getId() {return id_fuente;}

    public void setId(UUID id) {this.id_fuente = id;}

    public String toString() {
        return "Fuente{" +
                "tipoDeFuente=" + tipoDeFuente +
                ", ruta='" + ruta +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fuente fuente = (Fuente) o;
        return Objects.equals(tipoDeFuente, fuente.tipoDeFuente) &&
                Objects.equals(ruta, fuente.ruta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipoDeFuente, ruta);
    }
}
