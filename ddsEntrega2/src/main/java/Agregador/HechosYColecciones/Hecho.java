package Agregador.HechosYColecciones;

import utils.DTO.HechoDTO;
import Agregador.Contribuyente.Contribuyente;
import Agregador.fuente.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Hecho {

    private int id;
    private String titulo;
    private String descripcion;
    private String categoria;
    private Ubicacion ubicacion;
    private Date fechaDeAcontecimiento;
    private Date fechaDeCarga;
    private Fuente fuente;
    private EstadoHecho estadoHecho;
    private Contribuyente contribuyente;
    private List<String> etiquetas = new ArrayList<>();
    private boolean esEditable;
    private List<ContenidoMultimedia> contenidoMultimedia = new ArrayList<>();

    public Hecho() {}
    // esto es el Constructor
    public Hecho(String titulo, String descripcion, String categoria, Ubicacion ubicacion, Date fechaDeAcontecimiento,
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
        this.id = -1;
    }

    public Hecho(HechoDTO hechoDTO){
        this.titulo = hechoDTO.getTitulo();
        this.descripcion = hechoDTO.getDescripcion();
        this.categoria = hechoDTO.getCategoria();
        this.ubicacion = hechoDTO.getUbicacion();
        this.fechaDeAcontecimiento = hechoDTO.getFechaDeAcontecimiento();
        this.fechaDeCarga = hechoDTO.getFechaDeCarga();
        this.fuente = this.convertirFuente(hechoDTO.getFuente());
        this.estadoHecho = hechoDTO.getEstadoHecho();
        this.contribuyente = hechoDTO.getContribuyente();
        this.etiquetas = hechoDTO.getEtiquetas();
        this.esEditable = hechoDTO.getEsEditable();
        this.contenidoMultimedia = hechoDTO.getContenidoMultimedia();
        this.id = 0;
    }

    // Getters
    public String getTitulo() {
        return titulo;
    }

    public int getId(){return id;}


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

    public List<String> getEtiquetas() {
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

    public void setContribuyente(Contribuyente nuevo) {contribuyente = nuevo;}

    public void setEtiquetas(List<String> etiquetasNuevas) {etiquetas = etiquetasNuevas; }

    public void setEsEditable(Boolean esEditableNuevo) {esEditable = esEditableNuevo;}

    public void setId(int idNuevo){id = idNuevo;}


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

    //Metodo para comparar si dos hechos son iguales (comparando título)
    public boolean esIgualAotro(Hecho otroHecho) {
        return this.titulo.equalsIgnoreCase(otroHecho.getTitulo());
    }
    //"dos hechos son iguales si tienen el mismo titulo"

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
                && this.categoria.equalsIgnoreCase(otro.categoria)
                && Objects.equals(this.ubicacion, otro.ubicacion)
                && Objects.equals(this.fechaDeAcontecimiento, otro.fechaDeAcontecimiento)
                && Objects.equals(this.fechaDeCarga, otro.fechaDeCarga)
                && this.estadoHecho == otro.estadoHecho
                && Objects.equals(this.contribuyente, otro.contribuyente)
                && this.etiquetas.equals(otro.etiquetas)
                && this.esEditable == otro.esEditable;
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

    public Fuente convertirFuente(String ruta){
        if (ruta.toLowerCase().endsWith(".csv")) {
            return new Fuente(TipoDeFuente.ESTATICA, ruta);
        }

        String urlCargadorDinamico = "http://localhost:8084/";
        if (ruta.contains("localhost") &&  ruta.contains(urlCargadorDinamico)) {
            return new Fuente(TipoDeFuente.DINAMICA, ruta);
        }

        return new Fuente(TipoDeFuente.PROXY, ruta);
    }
}
