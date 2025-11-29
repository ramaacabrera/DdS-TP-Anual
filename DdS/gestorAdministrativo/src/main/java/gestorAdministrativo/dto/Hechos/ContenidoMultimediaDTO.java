package gestorAdministrativo.dto.Hechos;

import java.util.UUID;

public class ContenidoMultimediaDTO {
    private UUID contenidoId;
    private TipoContenidoMultimediaDTO tipoContenido;
    private String contenido;
    private UUID hechoId;

    public ContenidoMultimediaDTO() {}

    public ContenidoMultimediaDTO(UUID contenidoId, TipoContenidoMultimediaDTO tipoContenido, String contenido, UUID hechoId) {
        this.contenidoId = contenidoId;
        this.tipoContenido = tipoContenido;
        this.contenido = contenido;
        this.hechoId = hechoId;
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