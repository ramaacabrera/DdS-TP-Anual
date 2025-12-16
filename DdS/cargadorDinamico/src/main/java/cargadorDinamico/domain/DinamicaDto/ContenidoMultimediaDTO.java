package cargadorDinamico.domain.DinamicaDto;

import cargadorDinamico.domain.HechosYColeccionesD.ContenidoMultimedia_D;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContenidoMultimediaDTO {
    private UUID contenidoId;
    private TipoContenidoMultimediaDTO tipoContenido;
    private String contenido;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID hechoId;

    public ContenidoMultimediaDTO() {}

    public ContenidoMultimediaDTO(UUID contenidoId, TipoContenidoMultimediaDTO tipoContenido, String contenido, UUID hechoId) {
        this.contenidoId = contenidoId;
        this.tipoContenido = tipoContenido;
        this.contenido = contenido;
        this.hechoId = hechoId;
    }

    public ContenidoMultimediaDTO(ContenidoMultimedia_D entidad) {
        if (entidad != null) {
            this.contenidoId = entidad.getId_contenido();
            this.contenido = entidad.getContenido();

            // Conversión del Enum de la Entidad al Enum del DTO
            if (entidad.getTipoContenido() != null) {
                try {
                    // Asumimos que los nombres (IMAGEN, VIDEO, AUDIO) son iguales
                    this.tipoContenido = TipoContenidoMultimediaDTO.valueOf(entidad.getTipoContenido().name());
                } catch (IllegalArgumentException e) {
                    this.tipoContenido = null; // O un valor por defecto
                }
            }
        }
    }

    // Getters y Setters
    public UUID getContenidoId() { return contenidoId; }
    public void setContenidoId(UUID contenidoId) { this.contenidoId = contenidoId; }

    public TipoContenidoMultimediaDTO getTipoContenido() { return tipoContenido; }
    public void setTipoContenido(TipoContenidoMultimediaDTO tipoContenido) { this.tipoContenido = tipoContenido; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public UUID getHechoId() { return hechoId; }
    public void setHechoId(UUID hechoId) { this.hechoId = hechoId; }
}