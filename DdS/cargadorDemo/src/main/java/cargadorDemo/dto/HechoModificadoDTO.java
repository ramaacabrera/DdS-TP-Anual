package cargadorDemo.dto;

import cargadorDemo.domain.HechosYColecciones.ContenidoMultimedia;
import cargadorDemo.domain.HechosYColecciones.EstadoHecho;
import cargadorDemo.domain.HechosYColecciones.Etiqueta;
import cargadorDemo.domain.HechosYColecciones.Ubicacion;
import cargadorDemo.domain.Usuario.Usuario;
import cargadorDemo.domain.fuente.Fuente;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class HechoModificadoDTO {
    private UUID hechoModificadoId;
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

    public HechoModificadoDTO() {}

    // Constructor copia de HechoDTO si es necesario
    public HechoModificadoDTO(HechoDTO hechoDTO) {
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
    }

    // Getters y Setters
    public UUID getHechoModificadoId() { return hechoModificadoId; }
    public void setHechoModificadoId(UUID hechoModificadoId) { this.hechoModificadoId = hechoModificadoId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Ubicacion getUbicacion() { return ubicacion; }
    public void setUbicacion(Ubicacion ubicacion) { this.ubicacion = ubicacion; }

    public Date getFechaDeAcontecimiento() { return fechaDeAcontecimiento; }
    public void setFechaDeAcontecimiento(Date fechaDeAcontecimiento) { this.fechaDeAcontecimiento = fechaDeAcontecimiento; }

    public Date getFechaDeCarga() { return fechaDeCarga; }
    public void setFechaDeCarga(Date fechaDeCarga) { this.fechaDeCarga = fechaDeCarga; }

    public Fuente getFuente() { return fuente; }
    public void setFuente(Fuente fuente) { this.fuente = fuente; }

    public EstadoHecho getEstadoHecho() { return estadoHecho; }
    public void setEstadoHecho(EstadoHecho estadoHecho) { this.estadoHecho = estadoHecho; }

    public Usuario getContribuyente() { return contribuyente; }
    public void setContribuyente(Usuario contribuyente) { this.contribuyente = contribuyente; }

    public List<Etiqueta> getEtiquetas() { return etiquetas; }
    public void setEtiquetas(List<Etiqueta> etiquetas) { this.etiquetas = etiquetas; }

    public boolean isEsEditable() { return esEditable; }
    public void setEsEditable(boolean esEditable) { this.esEditable = esEditable; }

    public List<ContenidoMultimedia> getContenidoMultimedia() { return contenidoMultimedia; }
    public void setContenidoMultimedia(List<ContenidoMultimedia> contenidoMultimedia) { this.contenidoMultimedia = contenidoMultimedia; }
}