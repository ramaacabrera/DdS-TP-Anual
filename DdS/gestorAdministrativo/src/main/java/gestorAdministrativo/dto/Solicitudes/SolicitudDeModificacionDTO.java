package gestorAdministrativo.dto.Solicitudes;

import java.util.UUID;

public class SolicitudDeModificacionDTO extends SolicitudDTO {
    private UUID id;

    private HechoModificadoDTO hechoModificado;
    private EstadoSolicitudModificacionDTO estado;

    public SolicitudDeModificacionDTO() {}

    public SolicitudDeModificacionDTO(UUID id, UUID hechoAsociadoId, String justificacion, UUID usuarioId,
                                      HechoModificadoDTO hechoModificado, EstadoSolicitudModificacionDTO estado) {

        super();
        this.id = id;
        this.setHechoId(hechoAsociadoId);
        this.setUsuarioId(usuarioId);
        this.setJustificacion(justificacion);

        this.hechoModificado = hechoModificado;
        this.estado = estado;
    }

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public HechoModificadoDTO getHechoModificado() { return hechoModificado; }
    public void setHechoModificado(HechoModificadoDTO hechoModificado) { this.hechoModificado = hechoModificado; }

    public EstadoSolicitudModificacionDTO getEstado() { return estado; }
    public void setEstado(EstadoSolicitudModificacionDTO estado) { this.estado = estado; }
}