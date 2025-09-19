package utils.DTO;

import Agregador.HechosYColecciones.ContenidoMultimedia;
import Agregador.Contribuyente.Contribuyente;
import Agregador.HechosYColecciones.EstadoHecho;
import Agregador.HechosYColecciones.Ubicacion;
import Agregador.fuente.Fuente;

import java.util.Date;
import java.util.List;

public class HechoDTO {
    private String titulo;
    private String descripcion;
    private String categoria;
    private Ubicacion ubicacion;
    private Date fechaDeAcontecimiento;
    private Date fechaDeCarga;
    private Fuente fuente;
    private EstadoHecho estadoHecho;
    private Contribuyente contribuyente;
    private List<String> etiquetas;
    private boolean esEditable;
    private List<ContenidoMultimedia> contenidoMultimedia;

    public HechoDTO() {}

    public HechoDTO(String titulo, String descripcion, String categoria, Ubicacion ubicacion, Date fechaDeAcontecimiento,
                    Date fechaDeCarga, Fuente fuente, EstadoHecho estadoHecho, Contribuyente contribuyente, List<String> etiquetas, boolean esEditable,
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

    public String getTitulo() { return titulo;}
    public String getDescripcion() { return descripcion;}
    public String getCategoria() { return categoria;}
    public Ubicacion getUbicacion() { return ubicacion;}
    public Date getFechaDeAcontecimiento() { return fechaDeAcontecimiento;}
    public Date getFechaDeCarga() { return fechaDeCarga;}
    public Fuente getFuente() { return fuente;}
    public EstadoHecho getEstadoHecho() { return estadoHecho;}
    public Contribuyente getContribuyente() { return contribuyente;}
    public List<String> getEtiquetas() { return etiquetas;}
    public boolean getEsEditable() { return esEditable;}
    public List<ContenidoMultimedia> getContenidoMultimedia() { return contenidoMultimedia; }

    public void setTitulo(String titulo){ this.titulo = titulo;}
    public void setDescripcion(String descripcion){ this.descripcion = descripcion;}
    public void setCategoria(String categoria){ this.categoria = categoria;}
    public void setUbicacion(Ubicacion ubicacion) {this.ubicacion = ubicacion;}
    public void setFechaDeAcontecimiento(Date fechaDeAcontecimiento){ this.fechaDeAcontecimiento = fechaDeAcontecimiento;}
    public void setFuente(Fuente fuente) {this.fuente = fuente;}
    public void setEstadoHecho(EstadoHecho estadoHecho){ this.estadoHecho = estadoHecho;}
    public void setContribuyente(Contribuyente contribuyente){ this.contribuyente = contribuyente;}
    public void setEtiquetas(List<String> etiquetas) {this.etiquetas = etiquetas;}
    public void setEsEditable(boolean esEditable) {this.esEditable = esEditable;}
    public void setContenidoMultimedia(List<ContenidoMultimedia> contenidoMultimedia) {this.contenidoMultimedia = contenidoMultimedia;}
}
