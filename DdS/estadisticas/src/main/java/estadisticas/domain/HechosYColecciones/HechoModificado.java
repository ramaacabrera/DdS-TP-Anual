package estadisticas.domain.HechosYColecciones;

import estadisticas.domain.HechosYColecciones.ContenidoMultimedia;
import estadisticas.domain.HechosYColecciones.Hecho;
import estadisticas.domain.Usuario.Usuario;
import estadisticas.domain.fuente.Fuente;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class HechoModificado {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type = "uuid-char")
    @Column(name = "hecho_modificado_id", length = 36 , updatable = false, nullable = false)
    private UUID hecho_modificado_id;

    private String titulo;
    private String descripcion;
    private String categoria;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "id_ubicacion")
    private Ubicacion ubicacion;

    private Date fechaDeAcontecimiento;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "hecho_modificado_id")
    private List<ContenidoMultimedia> contenidoMultimedia = new ArrayList<>();

    public HechoModificado() {}

    public HechoModificado(String titulo, String descripcion, String categoria, Ubicacion ubicacion, Date fechaDeAcontecimiento, List<ContenidoMultimedia> contenidoMultimedia) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.ubicacion = ubicacion;
        this.fechaDeAcontecimiento = fechaDeAcontecimiento;
        this.contenidoMultimedia = contenidoMultimedia;
    }

    // Getters
    public UUID getHecho_modificado_id() { return hecho_modificado_id; }
    public UUID getId() { return hecho_modificado_id; }

    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getCategoria() { return categoria; }
    public Ubicacion getUbicacion() { return ubicacion; }
    public Date getFechaDeAcontecimiento() { return fechaDeAcontecimiento; }
    public List<ContenidoMultimedia> getContenidoMultimedia() { return contenidoMultimedia; }

    // Setters
    public void setHecho_modificado_id(UUID hecho_modificado_id) { this.hecho_modificado_id = hecho_modificado_id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setUbicacion(Ubicacion ubicacion) { this.ubicacion = ubicacion; }
    public void setFechaDeAcontecimiento(Date fechaDeAcontecimiento) { this.fechaDeAcontecimiento = fechaDeAcontecimiento; }
    public void setContenidoMultimedia(List<ContenidoMultimedia> contenidoMultimedia) { this.contenidoMultimedia = contenidoMultimedia; }


    public boolean esIgualAotro(HechoModificado otroHecho) {
        if (otroHecho == null) return false;
        return this.titulo != null && this.titulo.equalsIgnoreCase(otroHecho.getTitulo());
    }
}
