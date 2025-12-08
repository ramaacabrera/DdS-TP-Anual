package gestorPublico.dto;

import java.util.Date;

public class FiltroHechosDTO {
    public String categoria;
    public Date fechaCargaDesde;
    public Date fechaCargaHasta;
    public Date fechaAcontecimientoDesde;
    public Date fechaAcontecimientoHasta;
    public String descripcion;
    public String contribuyente;
    public String textoBusqueda;
    public int page = 1;
    public int size = 10;
}