package DominioGestorAdministrativo.DTO.Solicitudes;

import DominioGestorAdministrativo.DTO.Hechos.UsuarioDTO;

import java.util.UUID;

public class SolicitudDTO {
    private UUID solicitudId;
    private UUID hechoAsociadoId;
    private String justificacion;
    private UsuarioDTO usuario;

    public SolicitudDTO() {}

    public SolicitudDTO(UUID hechoAsociadoId, String justificacion, UsuarioDTO usuario) {
        this.hechoAsociadoId = hechoAsociadoId;
        this.justificacion = justificacion;
        this.usuario = usuario;
    }

    // Getters y Setters
    public UUID getSolicitudId() { return solicitudId; }
    public void setSolicitudId(UUID solicitudId) { this.solicitudId = solicitudId; }

    public UUID getHechoAsociadoId() { return hechoAsociadoId; }
    public void setHechoAsociadoId(UUID hechoAsociadoId) { this.hechoAsociadoId = hechoAsociadoId; }

    public String getJustificacion() { return justificacion; }
    public void setJustificacion(String justificacion) { this.justificacion = justificacion; }

    public UsuarioDTO getUsuario() { return usuario; }
    public void setUsuario(UsuarioDTO usuario) { this.usuario = usuario; }
}