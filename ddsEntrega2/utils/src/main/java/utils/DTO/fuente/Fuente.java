package utils.DTO.fuente;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import utils.DTO.FuenteDTO;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;
import java.util.UUID;
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
    @Type(type = "uuid-char")
    @Column(name = "id_fuente", length = 36 , updatable = false, nullable = false)
    private UUID id_fuente;

    @Enumerated(EnumType.STRING)
    private TipoDeFuente tipoDeFuente;
    @Column(name = "ruta", nullable = false, unique = true)
    private String ruta;

    public Fuente() {}

    public Fuente(TipoDeFuente tipoDeFuente, String ruta /*, int idNuevo*/) {
        this.tipoDeFuente = tipoDeFuente;
        this.ruta = ruta;

    }

    public Fuente(FuenteDTO fuenteDTO) {
        this.tipoDeFuente = fuenteDTO.getTipoDeFuente();
        this.ruta = fuenteDTO.getRuta();

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
