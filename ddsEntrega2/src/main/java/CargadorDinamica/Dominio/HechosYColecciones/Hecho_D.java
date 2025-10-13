package CargadorDinamica.Dominio.HechosYColecciones;

import Agregador.HechosYColecciones.Ubicacion;
import CargadorDinamica.DinamicaDto.Hecho_D_DTO;
import CargadorDinamica.Dominio.Usuario.Usuario_D;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
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

    @OneToMany(mappedBy = "hecho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContenidoMultimedia_D> contenidoMultimedia;

    public Hecho_D() {}
    // esto es el Constructor
    public Hecho_D(String titulo, String descripcion, String categoria, Ubicacion_D ubicacion, Date fechaDeAcontecimiento,
                 Date fechaDeCarga, String fuente, EstadoHecho estadoHecho, Usuario_D contribuyente, List<Etiqueta_D> etiquetas, boolean esEditable,
                 List<ContenidoMultimedia_D> contenidoMultimedia) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.ubicacion = ubicacion;
        this.fechaDeAcontecimiento = fechaDeAcontecimiento;
        this.fechaDeCarga = fechaDeCarga;
        this.contribuyente = contribuyente;
        this.etiquetas = etiquetas;
        this.contenidoMultimedia = contenidoMultimedia;
    }

    public Hecho_D(Hecho_D_DTO hechoDTO){
        this.titulo = hechoDTO.getTitulo();
        this.descripcion = hechoDTO.getDescripcion();
        this.categoria = hechoDTO.getCategoria();
        this.ubicacion = hechoDTO.getUbicacion();
        this.fechaDeAcontecimiento = hechoDTO.getFechaDeAcontecimiento();
        this.fechaDeCarga = hechoDTO.getFechaDeCarga();
        this.contribuyente = hechoDTO.getContribuyente();
        this.etiquetas = hechoDTO.getEtiquetas();
        this.contenidoMultimedia = hechoDTO.getContenidoMultimedia();
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

    public Usuario_D getContribuyente() {
        return contribuyente;
    }

    public List<Etiqueta_D> getEtiquetas() {
        return etiquetas;
    }

    // Setters

    public void setCategoria(String categoriaNueva) {categoria = categoriaNueva;}

    public void setTitulo(String tituloNuevo) {titulo = tituloNuevo;}

    public void setDescripcion(String descripcionNueva) {descripcion = descripcionNueva;}

    public void setUbicacion(Ubicacion_D ubicacionNueva) {ubicacion = ubicacionNueva;}

    public void setFechaDeAcontecimiento(Date fechaDeAcontecimientoNueva) {fechaDeAcontecimiento = fechaDeAcontecimientoNueva;}

    public void setFechaDeCarga(Date fechaDeCargaNueva) {fechaDeCarga = fechaDeCargaNueva;}

    public void setContribuyente(Usuario_D nuevo) {contribuyente = nuevo;}

    public void setEtiquetas(List<Etiqueta_D> etiquetasNuevas) {etiquetas = etiquetasNuevas; }

    public void setHecho_id (UUID id) {hecho_id = id;}

    public void setContenidoMultimedia(List<ContenidoMultimedia_D> contenidoNuevo) {contenidoMultimedia = contenidoNuevo;}

    public List<ContenidoMultimedia_D> getContenidoMultimedia() {
        return contenidoMultimedia;
    }
}
