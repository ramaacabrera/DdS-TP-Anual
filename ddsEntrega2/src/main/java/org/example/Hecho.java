package org.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Hecho {

    private String titulo;
    private String descripcion;
    private String categoria;
    private Ubicacion ubicacion;
    private Date fechaDeAcontecimiento;
    private Date fechaDeCarga;
    private Fuente fuente;
    private EstadoHecho estadoHecho;
    private Contribuyente contribuyente;
    private String contenidoDeTexto;
    private List<String> etiquetas = new ArrayList<>();
    private boolean esEditable;
    private List<ContenidoMultimedia> contenidoMultimedia = new ArrayList<>();

    // esto es el Constructor
    public Hecho(String titulo, String descripcion, String categoria, Ubicacion ubicacion, Date fechaDeAcontecimiento,
                 Date fechaDeCarga, Fuente fuente, EstadoHecho estadoHecho, Contribuyente contribuyente,
                 String contenidoDeTexto, List<String> etiquetas, boolean esEditable,
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
        this.contenidoDeTexto = contenidoDeTexto;
        this.etiquetas = etiquetas;
        this.esEditable = esEditable;
        this.contenidoMultimedia = contenidoMultimedia;
    }

    // Getters
    public String getTitulo() {
        return titulo;
    }

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

    public Contribuyente getContribuyente() {
        return contribuyente;
    }

    public String getContenidoDeTexto() {
        return contenidoDeTexto;
    }

    public List<String> getEtiquetas() {
        return etiquetas;
    }

    public boolean esEditable() {
        return esEditable;
    }

    public List<ContenidoMultimedia> getContenidoMultimedia() {
        return contenidoMultimedia;
    }

    //Método para ocultar el hecho
    public void ocultar() {
        this.estadoHecho = EstadoHecho.OCULTO;
    }

    //Método para comparar si dos hechos son iguales (comparando título)
    public boolean esIgualAotro(Hecho otroHecho) {
        return this.titulo.equalsIgnoreCase(otroHecho.getTitulo());
    }
    //"dos hechos son iguales si tienen el mismo titulo"

    //Método para modificar un hecho si es editable
    //REVISAR CON FUENTE DINAMICA Y SOLICITUDES
    public void modificar(String nuevoTitulo, String nuevaDescripcion, String nuevaCategoria, String nuevoContenidoTexto) {
        if (esEditable) {
            this.titulo = nuevoTitulo;
            this.descripcion = nuevaDescripcion;
            this.categoria = nuevaCategoria;
            this.contenidoDeTexto = nuevoContenidoTexto;
        } else {
            System.out.println("Este hecho no es editable.");
        }
    }
}

/*Hecho hechito = new Hecho ("Título del hecho",
        "Descripción detallada del hecho",
        "Política",
        ubicacion,
        new Date(), // fecha de acontecimiento
        new Date(), // fecha de carga
        fuente,
        estado,
        contribuyente,
        "Este es el contenido completo del hecho.",
        etiquetas,
        true,
        multimedia);
*/