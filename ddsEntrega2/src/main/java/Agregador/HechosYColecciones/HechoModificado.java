package Agregador.HechosYColecciones;

import Agregador.Usuario.Usuario;
import Agregador.fuente.Fuente;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import utils.DTO.HechoDTO;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "id_fuente", nullable = false)
    private Fuente fuente;


    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "handle")
    private Coleccion coleccion;

    @Enumerated(EnumType.STRING)
    private EstadoHecho estadoHecho;

    @ManyToOne (cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "id_usuario", nullable = true)
    private Usuario contribuyente;

    @ManyToMany (cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "HechoModificadoXEtiqueta",
            joinColumns = @JoinColumn(name = "hecho_modificado_id"),
            inverseJoinColumns = @JoinColumn(name = "id_etiqueta")
    )
    private List<Etiqueta> etiquetas;

    private boolean esEditable;

    @OneToMany(mappedBy = "hecho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContenidoMultimedia> contenidoMultimedia;

    public HechoModificado() {}
    // esto es el Constructor
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
        //this.hecho_id = -1;
    }

    public HechoModificado(HechoDTO hechoDTO){
        this.titulo = hechoDTO.getTitulo();
        this.descripcion = hechoDTO.getDescripcion();
        this.categoria = hechoDTO.getCategoria();
        this.ubicacion = hechoDTO.getUbicacion();
        this.fechaDeAcontecimiento = hechoDTO.getFechaDeAcontecimiento();
        this.fechaDeCarga = hechoDTO.getFechaDeCarga();
        this.fuente = hechoDTO.getFuente();
        this.estadoHecho = hechoDTO.getEstadoHecho();
        this.contribuyente = hechoDTO.getContribuyente();
        this.etiquetas = hechoDTO.getEtiquetas();
        this.esEditable = hechoDTO.getEsEditable();
        this.contenidoMultimedia = hechoDTO.getContenidoMultimedia();
        //this.hecho_id = 0;
    }

    // Getters
    public String getTitulo() {
        return titulo;
    }

    public UUID getHecho_id() {return hecho_modificado_id;}

    public String getDescripcion() {
        return descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public Date getFechaDeAcontecimiento() {
        return fechaDeAcontecimiento;
    }

    public Date getFechaDeCarga() {
        return fechaDeCarga;
    }

    public Fuente getFuente() {
        return fuente;
    }

    public EstadoHecho getEstadoHecho() {
        return estadoHecho;
    }

    public Usuario getContribuyente() {
        return contribuyente;
    }

    public List<Etiqueta> getEtiquetas() {
        return etiquetas;
    }

    // Setters

    public void setCategoria(String categoriaNueva) {categoria = categoriaNueva;}

    public void setTitulo(String tituloNuevo) {titulo = tituloNuevo;}

    public void setDescripcion(String descripcionNueva) {descripcion = descripcionNueva;}

    public void setUbicacion(Ubicacion ubicacionNueva) {ubicacion = ubicacionNueva;}

    public void setFechaDeAcontecimiento(Date fechaDeAcontecimientoNueva) {fechaDeAcontecimiento = fechaDeAcontecimientoNueva;}

    public void setFechaDeCarga(Date fechaDeCargaNueva) {fechaDeCarga = fechaDeCargaNueva;}

    public void setFuente(Fuente fuenteNueva) {fuente = fuenteNueva;}

    public void setEstadoHecho(EstadoHecho estadoNuevo) {estadoHecho = estadoNuevo;}

    public void setContribuyente(Usuario nuevo) {contribuyente = nuevo;}

    public void setEtiquetas(List<Etiqueta> etiquetasNuevas) {etiquetas = etiquetasNuevas; }

    public void setEsEditable(Boolean esEditableNuevo) {esEditable = esEditableNuevo;}

    public void setHecho_id (UUID id) {hecho_modificado_id = id;}

    public void setColeccion(Coleccion coleccion_) {coleccion = coleccion_;}

    public void setContenidoMultimedia(List<ContenidoMultimedia> contenidoNuevo) {contenidoMultimedia = contenidoNuevo;}

    public boolean esEditable() {
        return esEditable;
    }

    public boolean estaActivo() {
        return this.estadoHecho == EstadoHecho.ACTIVO;
    }

    public List<ContenidoMultimedia> getContenidoMultimedia() {
        return contenidoMultimedia;
    }

    //Metodo para ocultar el hecho
    public void ocultar() {
        this.estadoHecho = EstadoHecho.OCULTO;
    }

    //Metodo para comparar si dos hechos son iguales (comparando título)
    public boolean esIgualAotro(Hecho otroHecho) {
        return this.titulo.equalsIgnoreCase(otroHecho.getTitulo());
    }
    //"dos hechos son iguales si tienen el mismo titulo"
}
