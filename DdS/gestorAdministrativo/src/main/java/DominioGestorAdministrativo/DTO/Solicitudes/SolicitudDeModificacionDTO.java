package DominioGestorAdministrativo.DTO.Solicitudes;

import DominioGestorAdministrativo.DTO.Hechos.UsuarioDTO;

import java.util.UUID;

public class SolicitudDeModificacionDTO extends SolicitudDTO {
    private HechoModificadoDTO hechoModificado;
    private EstadoSolicitudModificacionDTO estado;

    public SolicitudDeModificacionDTO() {}

    public SolicitudDeModificacionDTO(UUID hechoAsociadoId, String justificacion, UsuarioDTO usuario,
                                      HechoModificadoDTO hechoModificado, EstadoSolicitudModificacionDTO estado) {
        super(hechoAsociadoId, justificacion, usuario);
        this.hechoModificado = hechoModificado;
        this.estado = estado;
    }

    // Getters y Setters
    public HechoModificadoDTO getHechoModificado() { return hechoModificado; }
    public void setHechoModificado(HechoModificadoDTO hechoModificado) { this.hechoModificado = hechoModificado; }

    public EstadoSolicitudModificacionDTO getEstado() { return estado; }
    public void setEstado(EstadoSolicitudModificacionDTO estado) { this.estado = estado; }
}