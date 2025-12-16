package cargadorDinamico.domain.HechosYColeccionesD;

import cargadorDinamico.domain.Usuario.Usuario_D;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
public class HechoModificado_D {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Type(type = "uuid-char")
    @Column(name = "hecho_modificado_id", length = 36, updatable = false, nullable = false)
    private UUID id;

    private String titulo;
    private String descripcion;
    private String categoria;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "id_ubicacion")
    private Ubicacion_D ubicacion;

    private Date fechaDeAcontecimiento;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @JoinColumn(name = "hecho_modificado_id") // FK en la tabla de multimedia
    private List<ContenidoMultimedia_D> contenidoMultimedia;

    public HechoModificado_D() {}

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public Ubicacion_D getUbicacion() { return ubicacion; }
    public void setUbicacion(Ubicacion_D ubicacion) { this.ubicacion = ubicacion; }
    public Date getFechaDeAcontecimiento() { return fechaDeAcontecimiento; }
    public void setFechaDeAcontecimiento(Date fechaDeAcontecimiento) { this.fechaDeAcontecimiento = fechaDeAcontecimiento; }
    public List<ContenidoMultimedia_D> getContenidoMultimedia() { return contenidoMultimedia; }
    public void setContenidoMultimedia(List<ContenidoMultimedia_D> contenidoMultimedia) { this.contenidoMultimedia = contenidoMultimedia; }
}