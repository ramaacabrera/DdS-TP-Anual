package estadisticas.domainEstadisticas;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"estadisticas_id", "categoria"})
})
public class EstadisticasCategoria {

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

    @Column(nullable = false)
    private String categoria;

    private String provincia;
    private Integer hora;

    public EstadisticasCategoria() {}

    public EstadisticasCategoria(Estadisticas estadisticas, String categoria, String provincia, Integer hora) {
        this.estadisticas = estadisticas;
        this.categoria = categoria;
        this.provincia = provincia;
        this.hora = hora;
    }

    // Getters y Setters
    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public Estadisticas getEstadisticas() { return this.estadisticas; }
    public void setEstadisticas(Estadisticas estadisticas) { this.estadisticas = estadisticas; }

    public String getCategoria() { return this.categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getProvincia() { return this.provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    public Integer getHora() { return this.hora; }
    public void setHora(Integer hora) { this.hora = hora; }
}