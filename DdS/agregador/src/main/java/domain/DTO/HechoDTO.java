package domain.DTO;

import utils.Dominio.HechosYColecciones.*;
import utils.Dominio.Usuario.Usuario;
import utils.Dominio.fuente.Fuente;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class HechoDTO {
    private UUID hecho_id;
    private String titulo;
    private String descripcion;
    private String categoria;
    private Ubicacion ubicacion;
    private Date fechaDeAcontecimiento;
    private Date fechaDeCarga;
    private Fuente fuente;
    private EstadoHecho estadoHecho;
    private Usuario contribuyente;
    private List<Etiqueta> etiquetas;
    private boolean esEditable;
    private List<ContenidoMultimedia> contenidoMultimedia;

    public HechoDTO() {}

    public HechoDTO(String titulo, String descripcion, String categoria, Ubicacion ubicacion, Date fechaDeAcontecimiento,
                    Date fechaDeCarga, Fuente fuente , EstadoHecho estadoHecho, Usuario contribuyente, List<Etiqueta> etiquetas, boolean esEditable,
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

    public HechoDTO(Hecho hecho){
        this.hecho_id = hecho.getHecho_id();
        this.titulo = hecho.getTitulo();
        this.descripcion = hecho.getDescripcion();
        this.categoria = hecho.getCategoria();
        this.ubicacion = hecho.getUbicacion();
        this.fechaDeAcontecimiento = hecho.getFechaDeAcontecimiento();
        this.fechaDeCarga = hecho.getFechaDeCarga();
        this.fuente = hecho.getFuente();
        this.estadoHecho = hecho.getEstadoHecho();
        this.contribuyente = hecho.getContribuyente();
        this.etiquetas = hecho.getEtiquetas();
        this.esEditable = hecho.esEditable();
        this.contenidoMultimedia = hecho.getContenidoMultimedia();
    }

    public UUID getHecho_id() {return hecho_id;}
    public String getTitulo() { return titulo;}
    public String getDescripcion() { return descripcion;}
    public String getCategoria() { return categoria;}
    public Ubicacion getUbicacion() { return ubicacion;}
    public Date getFechaDeAcontecimiento() { return fechaDeAcontecimiento;}
    public Date getFechaDeCarga() { return fechaDeCarga;}
    public Fuente getFuente() { return fuente;}
    public EstadoHecho getEstadoHecho() { return estadoHecho;}
    public Usuario getContribuyente() { return contribuyente;}
    public List<Etiqueta> getEtiquetas() { return etiquetas;}
    public boolean getEsEditable() { return esEditable;}
    public List<ContenidoMultimedia> getContenidoMultimedia() { return contenidoMultimedia; }

    public void setHecho_id(UUID hecho_id) {this.hecho_id = hecho_id;}
    public void setTitulo(String titulo){ this.titulo = titulo;}
    public void setDescripcion(String descripcion){ this.descripcion = descripcion;}
    public void setCategoria(String categoria){ this.categoria = categoria;}
    public void setUbicacion(Ubicacion ubicacion) {this.ubicacion = ubicacion;}
    public void setFechaDeAcontecimiento(Date fechaDeAcontecimiento){ this.fechaDeAcontecimiento = fechaDeAcontecimiento;}
    public void setFuente(Fuente fuente) {this.fuente = fuente;}
    public void setEstadoHecho(EstadoHecho estadoHecho){ this.estadoHecho = estadoHecho;}
    public void setContribuyente(Usuario contribuyente){ this.contribuyente = contribuyente;}
    public void setEtiquetas(List<Etiqueta> etiquetas) {this.etiquetas = etiquetas;}
    public void setEsEditable(boolean esEditable) {this.esEditable = esEditable;}
    public void setContenidoMultimedia(List<ContenidoMultimedia> contenidoMultimedia) {this.contenidoMultimedia = contenidoMultimedia;}
}
