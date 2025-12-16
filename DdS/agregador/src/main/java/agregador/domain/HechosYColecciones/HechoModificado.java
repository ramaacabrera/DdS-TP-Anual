package agregador.domain.HechosYColecciones;

import agregador.domain.Usuario.Usuario;
import agregador.domain.fuente.Fuente;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Entity
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
    private Date fechaDeCarga;

    @ManyToOne(cascade = {CascadeType.MERGE}, optional = true)
    @JoinColumn(name = "id_fuente", nullable = true)
    private Fuente fuente;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = true)
    @JoinColumn(name = "handle", nullable = true)
    private Coleccion coleccion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private EstadoHecho estadoHecho;

    @ManyToOne (cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = true)
    @JoinColumn(name = "id_usuario", nullable = true)
    private Usuario contribuyente;

    @ManyToMany (cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "HechoModificadoXEtiqueta",
            joinColumns = @JoinColumn(name = "hecho_modificado_id"),
            inverseJoinColumns = @JoinColumn(name = "id_etiqueta")
    )
    private List<Etiqueta> etiquetas = new ArrayList<>();

    private boolean esEditable;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "hecho_modificado_id")
    private List<ContenidoMultimedia> contenidoMultimedia = new ArrayList<>();

    public HechoModificado() {}

    public HechoModificado(String titulo, String descripcion, String categoria, Ubicacion ubicacion, Date fechaDeAcontecimiento,
                           Date fechaDeCarga, Fuente fuente, EstadoHecho estadoHecho, Usuario contribuyente, List<Etiqueta> etiquetas, boolean esEditable,
                           List<ContenidoMultimedia> contenidoMultimedia) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.ubicacion = ubicacion;
        this.fechaDeAcontecimiento = fechaDeAcontecimiento;
        this.fechaDeCarga = fechaDeCarga;
        this.fuente = fuente;
        this.estadoHecho = estadoHecho;
        this.contribuyente = contribuyente;
        this.etiquetas = etiquetas;
        this.esEditable = esEditable;
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
    public Date getFechaDeCarga() { return fechaDeCarga; }
    public Fuente getFuente() { return fuente; }
    public EstadoHecho getEstadoHecho() { return estadoHecho; }
    public Usuario getContribuyente() { return contribuyente; }
    public List<Etiqueta> getEtiquetas() { return etiquetas; }
    public List<ContenidoMultimedia> getContenidoMultimedia() { return contenidoMultimedia; }
    public boolean esEditable() { return esEditable; }

    // Setters
    public void setHecho_modificado_id(UUID hecho_modificado_id) { this.hecho_modificado_id = hecho_modificado_id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setUbicacion(Ubicacion ubicacion) { this.ubicacion = ubicacion; }
    public void setFechaDeAcontecimiento(Date fechaDeAcontecimiento) { this.fechaDeAcontecimiento = fechaDeAcontecimiento; }
    public void setFechaDeCarga(Date fechaDeCarga) { this.fechaDeCarga = fechaDeCarga; }
    public void setFuente(Fuente fuente) { this.fuente = fuente; }
    public void setEstadoHecho(EstadoHecho estadoHecho) { this.estadoHecho = estadoHecho; }
    public void setContribuyente(Usuario contribuyente) { this.contribuyente = contribuyente; }
    public void setEtiquetas(List<Etiqueta> etiquetas) { this.etiquetas = etiquetas; }
    public void setEsEditable(boolean esEditable) { this.esEditable = esEditable; }
    public void setContenidoMultimedia(List<ContenidoMultimedia> contenidoMultimedia) { this.contenidoMultimedia = contenidoMultimedia; }
    public void setColeccion(Coleccion coleccion) { this.coleccion = coleccion; }

    // Helpers
    public boolean estaActivo() {
        return this.estadoHecho == EstadoHecho.ACTIVO;
    }

    public void ocultar() {
        this.estadoHecho = EstadoHecho.OCULTO;
    }

    public boolean esIgualAotro(HechoModificado otroHecho) {
        if (otroHecho == null) return false;
        return this.titulo != null && this.titulo.equalsIgnoreCase(otroHecho.getTitulo());
    }
}