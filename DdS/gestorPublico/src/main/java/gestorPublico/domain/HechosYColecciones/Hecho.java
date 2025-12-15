package gestorPublico.domain.HechosYColecciones;

import gestorPublico.domain.Usuario.Usuario;
import gestorPublico.domain.fuente.Fuente;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.Set;

@Entity
public class Hecho {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type = "uuid-char")
    @Column(name = "hecho_id", length = 36 , updatable = false, nullable = false)
    private UUID hecho_id;

    private String titulo;
    private String descripcion;
    private String categoria;

    @ManyToOne (cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "id_ubicacion")
    private Ubicacion ubicacion;

    private Date fechaDeAcontecimiento;
    private Date fechaDeCarga;

    @ManyToOne
    @JoinColumn(name = "id_fuente", nullable = false)
    private Fuente fuente;


    @Enumerated(EnumType.STRING)
    private EstadoHecho estadoHecho;

    @ManyToOne (cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "id_usuario", nullable = true)
    private Usuario contribuyente;

    @ManyToMany (cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "HechoXEtiqueta",
            joinColumns = @JoinColumn(name = "hecho_id"),
            inverseJoinColumns = @JoinColumn(name = "id_etiqueta")
    )
    private Set<Etiqueta> etiquetas;

    private boolean esEditable;

    @OneToMany(mappedBy = "hecho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContenidoMultimedia> contenidoMultimedia;

    public Hecho() {}
    // esto es el Constructor
    public Hecho(String titulo, String descripcion, String categoria, Ubicacion ubicacion, Date fechaDeAcontecimiento,
                 Date fechaDeCarga, Fuente fuente, EstadoHecho estadoHecho, Usuario contribuyente, Set<Etiqueta> etiquetas, boolean esEditable,
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

    /*public Hecho(HechoDTO hechoDTO){
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
    }*/

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

    public Set<Etiqueta> getEtiquetas() {
        return etiquetas;
    }

    public boolean getEsEditable() {return esEditable;}

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

    public void setEtiquetas(Set<Etiqueta> etiquetasNuevas) {etiquetas = etiquetasNuevas; }

    public void setEsEditable(Boolean esEditableNuevo) {esEditable = esEditableNuevo;}

    public void setHecho_id (UUID id) {hecho_id = id;}

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

    //Metodo para modificar un hecho si es editable
    //REVISAR CON FUENTE DINAMICA Y SOLICITUDES
    public void modificar(String nuevoTitulo, String nuevaDescripcion, String nuevaCategoria, String nuevoContenidoTexto) {
        if (esEditable) {
            this.titulo = nuevoTitulo;
            this.descripcion = nuevaDescripcion;
            this.categoria = nuevaCategoria;
        } else {
            System.out.println("Este hecho no es editable.");
        }
    }

    public boolean tieneMismosAtributosQue(Hecho otro) {
        if (otro == null) return false;

        return this.titulo.equalsIgnoreCase(otro.titulo)
                && this.descripcion.equalsIgnoreCase(otro.descripcion)
                && this.categoria.equalsIgnoreCase(otro.categoria)
                && Objects.equals(this.ubicacion, otro.ubicacion)
                && Objects.equals(this.fechaDeAcontecimiento, otro.fechaDeAcontecimiento)
                && Objects.equals(this.contribuyente, otro.contribuyente);
    }

    public void actualizarCon(Hecho otroHecho) {
        this.descripcion = otroHecho.getDescripcion();
        this.categoria = otroHecho.getCategoria();
        this.ubicacion = otroHecho.getUbicacion();
        this.fechaDeAcontecimiento = otroHecho.getFechaDeAcontecimiento();
        this.fechaDeCarga = otroHecho.getFechaDeCarga();
        this.fuente = otroHecho.getFuente();
        this.estadoHecho = otroHecho.getEstadoHecho();
        this.contribuyente = otroHecho.getContribuyente();
        this.etiquetas = otroHecho.getEtiquetas();
        this.esEditable = otroHecho.esEditable();
        this.contenidoMultimedia = otroHecho.getContenidoMultimedia();
    }


}
