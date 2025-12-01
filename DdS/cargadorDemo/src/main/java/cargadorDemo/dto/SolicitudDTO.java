package cargadorDemo.dto;

import java.util.UUID;

public class SolicitudDTO {
    private UUID hechoId;
    private String justificacion;
    private UUID usuarioId;

    public SolicitudDTO() {}

    public UUID getHechoId() { return hechoId; }
    public void setHechoId(UUID hechoId) { this.hechoId = hechoId; }

    public String getJustificacion() { return justificacion; }
    public void setJustificacion(String justificacion) { this.justificacion = justificacion; }

    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
}