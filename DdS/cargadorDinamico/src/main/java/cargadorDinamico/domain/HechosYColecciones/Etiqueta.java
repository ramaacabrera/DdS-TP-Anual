package cargadorDinamico.domain.HechosYColecciones;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Etiqueta {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type = "uuid-char")
    @Column(name = "etiquetaId", length = 36 , updatable = false, nullable = false)
    private UUID etiquetaId;
    private String nombre;

    public Etiqueta() {}

    public Etiqueta(String nombre) {
        this.nombre = nombre;
    }

    public UUID getEtiquetaId() { return etiquetaId; }
    public void setEtiquetaId(UUID nuevoEtiqueta_id) { this.etiquetaId = nuevoEtiqueta_id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nuevoNombre) { this.nombre = nuevoNombre; }
}
