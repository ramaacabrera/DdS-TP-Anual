package DominioGestorAdministrativo.DTO.Solicitudes;

import DominioGestorAdministrativo.DTO.Hechos.UsuarioDTO;

import java.util.UUID;

public class SolicitudDeEliminacionDTO extends SolicitudDTO {
    private EstadoSolicitudEliminacionDTO estado;

    public SolicitudDeEliminacionDTO() {}

    public SolicitudDeEliminacionDTO(UUID hechoAsociadoId, String justificacion, UsuarioDTO usuario, EstadoSolicitudEliminacionDTO estado) {
        super(hechoAsociadoId, justificacion, usuario);
        this.estado = estado;
    }

    // Getters y Setters
    public EstadoSolicitudEliminacionDTO getEstado() { return estado; }
    public void setEstado(EstadoSolicitudEliminacionDTO estado) { this.estado = estado; }
}