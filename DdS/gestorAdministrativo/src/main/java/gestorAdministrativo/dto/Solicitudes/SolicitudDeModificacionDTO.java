package gestorAdministrativo.dto.Solicitudes;
import gestorAdministrativo.dto.Hechos.UsuarioDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SolicitudDeModificacionDTO {
    private UUID id;
    private HechoModificadoDTO hechoModificado;
    private EstadoSolicitudModificacionDTO estado;
    private String justificacion;
    private String hechoTitulo;
    private List<Map<String, String>> cambios;
    private UsuarioDTO usuarioId;
    private UUID hechoId;

    public SolicitudDeModificacionDTO() {}

    public SolicitudDeModificacionDTO(UUID id, UUID hechoAsociadoId, String justificacion, UsuarioDTO usuarioId,
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

    public UsuarioDTO getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UsuarioDTO usuarioId) { this.usuarioId = usuarioId; }

    public String getJustificacion() { return justificacion; }
    public void setJustificacion(String justificacion) { this.justificacion = justificacion; }

    public UUID getHechoId() { return hechoId; }
    public void setHechoId(UUID hechoId) { this.hechoId = hechoId; }
}