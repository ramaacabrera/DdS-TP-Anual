package cargadorDinamico.domain.DinamicaDto;

import cargadorDinamico.domain.HechosYColeccionesD.HechoModificado_D;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class HechoModificadoDTO {
    private UUID hechoModificadoId;
    private String titulo;
    private String descripcion;
    private String categoria;
    private UbicacionDTO ubicacion;
    private Date fechaDeAcontecimiento;
    private List<ContenidoMultimediaDTO> contenidoMultimedia;

    public HechoModificadoDTO() {}

    public HechoModificadoDTO(HechoModificado_D entidad) {
        if (entidad == null) return;

        this.hechoModificadoId = entidad.getId();
        this.titulo = entidad.getTitulo();
        this.descripcion = entidad.getDescripcion();
        this.categoria = entidad.getCategoria();
        this.fechaDeAcontecimiento = entidad.getFechaDeAcontecimiento();

        if (entidad.getUbicacion() != null) {
            this.ubicacion = new UbicacionDTO(entidad.getUbicacion());
        }

        if (entidad.getContenidoMultimedia() != null) {
            this.contenidoMultimedia = entidad.getContenidoMultimedia().stream()
                    .map(ContenidoMultimediaDTO::new)
                    .collect(Collectors.toList());
        } else {
            this.contenidoMultimedia = new ArrayList<>();
        }
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

    public List<ContenidoMultimediaDTO> getContenidoMultimedia() { return contenidoMultimedia; }
    public void setContenidoMultimedia(List<ContenidoMultimediaDTO> contenidoMultimedia) { this.contenidoMultimedia = contenidoMultimedia; }
}