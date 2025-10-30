package estadisticas.Dominio;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"estadisticas_id", "coleccion_id"})
})
public class EstadisticasColeccion {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type = "uuid-char")
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "estadisticas_id", nullable = false)
    private Estadisticas estadisticas;

    @Type(type = "uuid-char")
    @Column(name = "coleccion_id", length = 36, nullable = false)
    private UUID coleccionId;

    private String titulo;
    private String provincia;

    public EstadisticasColeccion() {}

    public EstadisticasColeccion(Estadisticas estadisticas, UUID coleccionId, String titulo, String provincia) {
        this.estadisticas = estadisticas;
        this.coleccionId = coleccionId;
        this.titulo = titulo;
        this.provincia = provincia;
    }

    // Getters y Setters
    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public Estadisticas getEstadisticas() { return this.estadisticas; }
    public void setEstadisticas(Estadisticas estadisticas) { this.estadisticas = estadisticas; }

    public UUID getColeccionId() { return this.coleccionId; }
    public void setColeccionId(UUID coleccionId) { this.coleccionId = coleccionId; }

    public String getTitulo() { return this.titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getProvincia() { return this.provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }
}