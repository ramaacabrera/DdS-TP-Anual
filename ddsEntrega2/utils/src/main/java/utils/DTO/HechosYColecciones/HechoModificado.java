package utils.DTO.HechosYColecciones;

import utils.DTO.Usuario.Usuario;
import utils.DTO.fuente.Fuente;
import utils.DTO.HechoDTO;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class HechoModificado {
    private UUID hecho_modificado_id;

    private String titulo;
    private String descripcion;
    private String categoria;
    private Ubicacion ubicacion;

    private Date fechaDeAcontecimiento;
    private Date fechaDeCarga;

    private Fuente fuente;


    private Coleccion coleccion;

    private EstadoHecho estadoHecho;

    private Usuario contribuyente;
    private List<Etiqueta> etiquetas;

    private boolean esEditable;

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


}
