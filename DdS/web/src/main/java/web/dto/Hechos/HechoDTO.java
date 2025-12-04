package web.dto.Hechos; // O el paquete correcto donde lo tengas

// Imports de tus Entidades de Dominio (Ajusta los paquetes si es necesario)
import web.domain.HechosYColecciones.Hecho;
import web.domain.HechosYColecciones.Ubicacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public HechoDTO(Hecho hecho) {
        if (hecho == null) return;

        this.hechoId = hecho.getHechoId();
        this.titulo = hecho.getTitulo();
        this.descripcion = hecho.getDescripcion();
        this.categoria = hecho.getCategoria();
        this.fechaDeAcontecimiento = hecho.getFechaDeAcontecimiento();
        this.fechaDeCarga = hecho.getFechaDeCarga();
        this.esEditable = hecho.getEsEditable();

        // 2. Mapeo de Objetos complejos (validando nulos)

        // Ubicación
        if (hecho.getUbicacion() != null) {
            Ubicacion u = hecho.getUbicacion();
            this.ubicacion = new UbicacionDTO();
            this.ubicacion.setUbicacionId(u.getId_ubicacion());
            this.ubicacion.setLatitud(u.getLatitud());
            this.ubicacion.setLongitud(u.getLongitud());
        }

        // Fuente
        if (hecho.getFuente() != null) {
            this.fuente = new FuenteDTO();
            this.fuente.setFuenteId(hecho.getFuente().getId());
            this.fuente.setDescriptor(hecho.getFuente().getDescriptor());
            if (hecho.getFuente().getTipoDeFuente() != null) {
                this.fuente.setTipoDeFuente(hecho.getFuente().getTipoDeFuente().name());
            }
        }

        // Estado
        if (hecho.getEstadoHecho() != null) {
            try {
                this.estadoHecho = EstadoHechoDTO.valueOf(hecho.getEstadoHecho().name());
            } catch (Exception e) {
                // Manejo de error si los enums no coinciden
            }
        }

        // Contribuyente
        if (hecho.getContribuyente() != null) {
            this.contribuyente = new UsuarioDTO();
            this.contribuyente.setId(hecho.getContribuyente().getId_usuario());;
            this.contribuyente.setUsername(hecho.getContribuyente().getUsername());
        }

        // 3. Mapeo de Listas (Streams)

        // Etiquetas
        if (hecho.getEtiquetas() != null) {
            this.etiquetas = hecho.getEtiquetas().stream().map(e -> {
                EtiquetaDTO dto = new EtiquetaDTO();
                dto.setId(e.getId());
                dto.setNombre(e.getNombre());
                return dto;
            }).collect(Collectors.toList());
        } else {
            this.etiquetas = new ArrayList<>();
        }

        // Multimedia
        if (hecho.getContenidoMultimedia() != null) {
            this.contenidoMultimedia = hecho.getContenidoMultimedia().stream().map(m -> {
                ContenidoMultimediaDTO dto = new ContenidoMultimediaDTO();
                dto.setContenidoId(m.getId_contenido());
                dto.setContenido(m.getContenido());
                if (m.getTipoContenido() != null) {
                    try {
                        dto.setTipoContenido(TipoContenidoMultimediaDTO.valueOf(m.getTipoContenido().name()));
                    } catch (Exception e) {}
                }
                return dto;
            }).collect(Collectors.toList());
        } else {
            this.contenidoMultimedia = new ArrayList<>();
        }
    }

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