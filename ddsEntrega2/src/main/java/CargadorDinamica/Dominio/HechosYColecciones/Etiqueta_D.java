package CargadorDinamica.Dominio.HechosYColecciones;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Etiqueta_D {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id_etiqueta", updatable = false, nullable = false)
    private UUID id_etiqueta;
    private String nombre;

    public Etiqueta_D() {}

    public Etiqueta_D(String nombre) {
        this.nombre = nombre;
    }

    public UUID getEtiqueta_id() { return id_etiqueta; }
    public void setEtiqueta_id(UUID nuevoEtiqueta_id) { this.id_etiqueta = nuevoEtiqueta_id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nuevoNombre) { this.nombre = nuevoNombre; }
}
