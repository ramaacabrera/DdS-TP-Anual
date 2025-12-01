package gestorAdministrativo.dto.Solicitudes;

import gestorAdministrativo.dto.Hechos.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class HechoModificadoDTO {
    private UUID hechoModificadoId;
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
        this.esEditable = hechoDTO.isEsEditable();
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