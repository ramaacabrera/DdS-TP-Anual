package gestorPublico.dto;

import java.util.Date;

public class FiltroHechosDTO {
    public String categoria;
    public Date fechaCargaDesde;
    public Date fechaCargaHasta;
    public Date fechaAcontecimientoDesde;
    public Date fechaAcontecimientoHasta;
    public Integer latitud;
    public Integer longitud;
    public String contribuyente;
    public String textoBusqueda;
    public int pagina = 1;
    public int limite = 10;
}