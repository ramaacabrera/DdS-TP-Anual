package cargadorDemo.domain.fuente;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import cargadorDemo.dto.FuenteDTO;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

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

    @Column(name = "descriptor", nullable = false, unique = true)
    private String descriptor;

    public Fuente() {}

    public Fuente(TipoDeFuente tipoDeFuente, String descriptorNuevo /*, int idNuevo*/) {
        this.tipoDeFuente = tipoDeFuente;
        this.descriptor = descriptorNuevo;

    }

    public Fuente(FuenteDTO fuenteDTO) {
        this.tipoDeFuente = fuenteDTO.getTipoDeFuente();
        this.descriptor = fuenteDTO.getRuta();

    }

    public TipoDeFuente getTipoDeFuente() {
        return tipoDeFuente;
    }

    public void setTipoDeFuente(TipoDeFuente tipoDeFuente) {
        this.tipoDeFuente = tipoDeFuente;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String ruta) {
        this.descriptor = ruta;
    }

    public UUID getId() {return id_fuente;}

    public void setId(UUID id) {this.id_fuente = id;}

    public String toString() {
        return "Fuente{" +
                "tipoDeFuente=" + tipoDeFuente +
                ", descriptor='" + descriptor +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fuente fuente = (Fuente) o;
        return Objects.equals(tipoDeFuente, fuente.tipoDeFuente) &&
                Objects.equals(descriptor, fuente.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipoDeFuente, descriptor);
    }
}
