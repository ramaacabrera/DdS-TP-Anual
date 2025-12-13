package web.dto.Solicitudes;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SolicitudDeModificacionDTO extends SolicitudDTO {
    private UUID id;
    private HechoModificadoDTO hechoModificado;
    private EstadoSolicitudModificacionDTO estado;
    private String hechoTitulo;
    private List<Map<String, String>> cambios;

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

    // Getters y Setters new
    public String getHechoTitulo() { return hechoTitulo; }
    public void setHechoTitulo(String hechoTitulo) { this.hechoTitulo = hechoTitulo; }

    public List<Map<String, String>> getCambios() { return cambios; }
    public void setCambios(List<Map<String, String>> cambios) { this.cambios = cambios; }
}