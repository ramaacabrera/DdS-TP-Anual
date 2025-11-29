package gestorAdministrativo.dto.Hechos;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class HechoDTO {
    private UUID hechoId;
    private String titulo;
    private String descripcion;
    private String categoria;
    private UbicacionDTO ubicacion;
    private Date fechaDeAcontecimiento;
    private Date fechaDeCarga;
    private FuenteDTO fuente;
    private EstadoHechoDTO estadoHecho;
    private UsuarioDTO contribuyente;
    private List<EtiquetaDTO> etiquetas;
    private boolean esEditable;
    private List<ContenidoMultimediaDTO> contenidoMultimedia;

    public HechoDTO() {}

    public HechoDTO(String titulo, String descripcion, String categoria, UbicacionDTO ubicacion,
                    Date fechaDeAcontecimiento, Date fechaDeCarga, FuenteDTO fuente,
                    EstadoHechoDTO estadoHecho, UsuarioDTO contribuyente, List<EtiquetaDTO> etiquetas,
                    boolean esEditable, List<ContenidoMultimediaDTO> contenidoMultimedia) {
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

    // Constructor que acepta el objeto de dominio Hecho (necesitarías adaptar este constructor)
    /*
    public HechoDTO(Hecho hecho) {
        this.hechoId = hecho.getHechoId();
        this.titulo = hecho.getTitulo();
        this.descripcion = hecho.getDescripcion();
        this.categoria = hecho.getCategoria();
        this.ubicacion = new UbicacionDTO(hecho.getUbicacion().getUbicacionId(),
                                         hecho.getUbicacion().getLatitud(),
                                         hecho.getUbicacion().getLongitud());
        this.fechaDeAcontecimiento = hecho.getFechaDeAcontecimiento();
        this.fechaDeCarga = hecho.getFechaDeCarga();
        // ... similar para los otros DTOs
    }
    */

    // Getters y Setters
    public UUID getHechoId() { return hechoId; }
    public void setHechoId(UUID hechoId) { this.hechoId = hechoId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public UbicacionDTO getUbicacion() { return ubicacion; }
    public void setUbicacion(UbicacionDTO ubicacion) { this.ubicacion = ubicacion; }

    public Date getFechaDeAcontecimiento() { return fechaDeAcontecimiento; }
    public void setFechaDeAcontecimiento(Date fechaDeAcontecimiento) { this.fechaDeAcontecimiento = fechaDeAcontecimiento; }

    public Date getFechaDeCarga() { return fechaDeCarga; }
    public void setFechaDeCarga(Date fechaDeCarga) { this.fechaDeCarga = fechaDeCarga; }

    public FuenteDTO getFuente() { return fuente; }
    public void setFuente(FuenteDTO fuente) { this.fuente = fuente; }

    public EstadoHechoDTO getEstadoHecho() { return estadoHecho; }
    public void setEstadoHecho(EstadoHechoDTO estadoHecho) { this.estadoHecho = estadoHecho; }

    public UsuarioDTO getContribuyente() { return contribuyente; }
    public void setContribuyente(UsuarioDTO contribuyente) { this.contribuyente = contribuyente; }

    public List<EtiquetaDTO> getEtiquetas() { return etiquetas; }
    public void setEtiquetas(List<EtiquetaDTO> etiquetas) { this.etiquetas = etiquetas; }

    public boolean isEsEditable() { return esEditable; }
    public void setEsEditable(boolean esEditable) { this.esEditable = esEditable; }

    public List<ContenidoMultimediaDTO> getContenidoMultimedia() { return contenidoMultimedia; }
    public void setContenidoMultimedia(List<ContenidoMultimediaDTO> contenidoMultimedia) { this.contenidoMultimedia = contenidoMultimedia; }
}