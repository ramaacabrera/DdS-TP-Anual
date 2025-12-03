package cargadorDinamico.domain.DinamicaDto;

import cargadorDinamico.domain.HechosYColeccionesD.ContenidoMultimedia_D;
import cargadorDinamico.domain.HechosYColeccionesD.Etiqueta_D;
import cargadorDinamico.domain.HechosYColeccionesD.Ubicacion_D;
import cargadorDinamico.domain.Usuario.Usuario_D;

import java.util.Date;
import java.util.List;

public class Hecho_D_DTO {
    private String titulo;
    private String descripcion;
    private String categoria;
    private Ubicacion_D ubicacion;
    private Date fechaDeAcontecimiento;
    private Date fechaDeCarga;
    private Usuario_D contribuyente;
    private List<Etiqueta_D> etiquetas;
    private List<ContenidoMultimedia_D> contenidoMultimedia;

    public Hecho_D_DTO() {}

    public Hecho_D_DTO(String titulo, String descripcion, String categoria, Ubicacion_D ubicacion, Date fechaDeAcontecimiento
                    /*Date fechaDeCarga*/, Usuario_D contribuyente, List<Etiqueta_D> etiquetas, List<ContenidoMultimedia_D> contenidoMultimedia) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.ubicacion = ubicacion;
        this.fechaDeAcontecimiento = fechaDeAcontecimiento;
        //this.fechaDeCarga = fechaDeCarga;
        this.contribuyente = contribuyente;
        this.etiquetas = etiquetas;
        this.contenidoMultimedia = contenidoMultimedia;
    }

    public String getTitulo() { return titulo;}
    public String getDescripcion() { return descripcion;}
    public String getCategoria() { return categoria;}
    public Ubicacion_D getUbicacion() { return ubicacion;}
    public Date getFechaDeAcontecimiento() { return fechaDeAcontecimiento;}
    public Date getFechaDeCarga() { return fechaDeCarga;}
    public Usuario_D getContribuyente() { return contribuyente;}
    public List<Etiqueta_D> getEtiquetas() { return etiquetas;}
    public List<ContenidoMultimedia_D> getContenidoMultimedia() { return contenidoMultimedia; }

    public void setTitulo(String titulo){ this.titulo = titulo;}
    public void setDescripcion(String descripcion){ this.descripcion = descripcion;}
    public void setCategoria(String categoria){ this.categoria = categoria;}
    public void setUbicacion(Ubicacion_D ubicacion) {this.ubicacion = ubicacion;}
    public void setFechaDeAcontecimiento(Date fechaDeAcontecimiento){ this.fechaDeAcontecimiento = fechaDeAcontecimiento;}
    public void setContribuyente(Usuario_D contribuyente){ this.contribuyente = contribuyente;}
    public void setEtiquetas(List<Etiqueta_D> etiquetas) {this.etiquetas = etiquetas;}
    public void setContenidoMultimedia(List<ContenidoMultimedia_D> contenidoMultimedia) {this.contenidoMultimedia = contenidoMultimedia;}

    public void actualizarDesdeDTO(Hecho_D_DTO dto) {
        this.titulo = dto.getTitulo();
        this.descripcion = dto.getDescripcion();
        this.categoria = dto.getCategoria();
        this.ubicacion = dto.getUbicacion();
        this.fechaDeAcontecimiento = dto.getFechaDeAcontecimiento();
        //this.fechaDeCarga = dto.getFechaDeCarga();
        //this.estadoHecho = dto.getEstadoHecho();
        this.contribuyente = dto.getContribuyente();
        this.etiquetas = dto.getEtiquetas();
        this.contenidoMultimedia = dto.getContenidoMultimedia();
    }
}