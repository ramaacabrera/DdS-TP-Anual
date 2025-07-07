package org.example.fuente;

import org.example.agregador.ContenidoMultimedia;
import org.example.agregador.Contribuyente;
import org.example.agregador.EstadoHecho;
import org.example.agregador.Ubicacion;

import java.util.Date;
import java.util.List;

public class HechoDTO {
    private final String titulo;
    private final String descripcion;
    private final String categoria;
    private final Ubicacion ubicacion;
    private final Date fechaDeAcontecimiento;
    private final Date fechaDeCarga;
    private final Fuente fuente;
    private final EstadoHecho estadoHecho;
    private final Contribuyente contribuyente;
    private final List<String> etiquetas;
    private final boolean esEditable;
    private final List<ContenidoMultimedia> contenidoMultimedia;

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
}
