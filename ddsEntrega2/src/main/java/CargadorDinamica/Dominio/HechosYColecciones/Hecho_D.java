package CargadorDinamica.Dominio.HechosYColecciones;

import Agregador.HechosYColecciones.ContenidoMultimedia;
import Agregador.HechosYColecciones.EstadoHecho;
import Agregador.HechosYColecciones.Etiqueta;
import Agregador.HechosYColecciones.Ubicacion;
import Agregador.Usuario.Usuario;
import Agregador.fuente.Fuente;
import CargadorDinamica.DinamicaDto.Hecho_D_DTO;
import CargadorDinamica.Dominio.Usuario.Usuario_D;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
public class Hecho_D {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "hecho_id", updatable = false, nullable = false)
    private UUID hecho_id;

    private String titulo;
    private String descripcion;
    private String categoria;

    @ManyToOne (cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "id_ubicacion")
    private Ubicacion_D ubicacion;

    private Date fechaDeAcontecimiento;
    private Date fechaDeCarga;

    private String fuente;  //LO PUSE COMO STRING

    @Enumerated(EnumType.STRING)
    private EstadoHecho_D estadoHecho;

    @ManyToOne (cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "id_usuario", nullable = true)
    private Usuario_D contribuyente;

    @ManyToMany (cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "HechoXEtiqueta",
            joinColumns = @JoinColumn(name = "hecho_id"),
            inverseJoinColumns = @JoinColumn(name = "id_etiqueta")
    )
    private List<Etiqueta_D> etiquetas;

    private boolean esEditable;

    @OneToMany(mappedBy = "hecho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContenidoMultimedia_D> contenidoMultimedia;

    public Hecho_D() {}

    public Hecho_D(Hecho_D_DTO hechoDTO){
        this.titulo = hechoDTO.getTitulo();
        this.descripcion = hechoDTO.getDescripcion();
        this.categoria = hechoDTO.getCategoria();
        this.ubicacion = hechoDTO.getUbicacion();
        this.fechaDeAcontecimiento = hechoDTO.getFechaDeAcontecimiento();
        this.fechaDeCarga = hechoDTO.getFechaDeCarga();
        this.fuente = "Dinamica";
        this.estadoHecho = EstadoHecho_D.ACTIVO;
        this.contribuyente = hechoDTO.getContribuyente();
        this.etiquetas = hechoDTO.getEtiquetas();
        this.esEditable = true;
        this.contenidoMultimedia = hechoDTO.getContenidoMultimedia();

        // Inicializar listas si son null
        if (this.etiquetas == null) this.etiquetas = new ArrayList<>();
        if (this.contenidoMultimedia == null) this.contenidoMultimedia = new ArrayList<>();
    }

    // Getters
    public String getTitulo() {
        return titulo;
    }
    public UUID getHecho_id() {return hecho_id;}
    public String getDescripcion() {
        return descripcion;
    }
    public String getCategoria() {
        return categoria;
    }
    public Ubicacion_D getUbicacion() {
        return ubicacion;
    }
    public Date getFechaDeAcontecimiento() {
        return fechaDeAcontecimiento;
    }
    public Date getFechaDeCarga() {
        return fechaDeCarga;
    }
    public String getFuente() {return fuente; }
    public EstadoHecho_D getEstadoHecho() {return estadoHecho; }
    public Usuario_D getContribuyente() {
        return contribuyente;
    }
    public List<Etiqueta_D> getEtiquetas() {
        return etiquetas;
    }
    public List<ContenidoMultimedia_D> getContenidoMultimedia() {
        return contenidoMultimedia;
    }
    public Boolean getEsEditable() {return esEditable; }
    // Setters

    public void setCategoria(String categoriaNueva) {categoria = categoriaNueva;}
    public void setTitulo(String tituloNuevo) {titulo = tituloNuevo;}
    public void setDescripcion(String descripcionNueva) {descripcion = descripcionNueva;}
    public void setUbicacion(Ubicacion_D ubicacionNueva) {ubicacion = ubicacionNueva;}
    public void setFechaDeAcontecimiento(Date fechaDeAcontecimientoNueva) {fechaDeAcontecimiento = fechaDeAcontecimientoNueva;}
    public void setFechaDeCarga(Date fechaDeCargaNueva) {fechaDeCarga = fechaDeCargaNueva;}
    public void setFuente(String fuenteNueva) {fuente = fuenteNueva; }
    public void setEstadoHecho(EstadoHecho_D estadoNuevo) {estadoHecho = estadoNuevo;}
    public void setContribuyente(Usuario_D nuevo) {contribuyente = nuevo;}
    public void setEtiquetas(List<Etiqueta_D> etiquetasNuevas) {etiquetas = etiquetasNuevas; }
    public void setHecho_id (UUID id) {hecho_id = id;}
    public void setEsEditable (Boolean esEditableNuevo) {esEditable = esEditableNuevo; }
    public void setContenidoMultimedia(List<ContenidoMultimedia_D> contenidoNuevo) {contenidoMultimedia = contenidoNuevo;}


}
