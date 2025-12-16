package gestorAdministrativo.dto.Solicitudes;

import gestorAdministrativo.domain.Solicitudes.SolicitudDeEliminacion;
import gestorAdministrativo.domain.Usuario.Usuario;
import gestorAdministrativo.dto.Hechos.UsuarioDTO; // Asumo que tienes este DTO
import gestorAdministrativo.domain.Solicitudes.EstadoSolicitudEliminacion;
import java.util.UUID;

public class SolicitudDeEliminacionDTO {
    private UUID id;
    private UUID hechoId;
    private String justificacion;
    private UsuarioDTO usuarioId;
    private EstadoSolicitudEliminacion estado;

    public SolicitudDeEliminacionDTO() {}

    public SolicitudDeEliminacionDTO(SolicitudDeEliminacion s) {
        this.id = s.getId();
        this.justificacion = s.getJustificacion();
        this.hechoId = s.getHechoAsociado().getHecho_id();
        this.usuarioId = convertirDTO(s.getUsuario());
        this.estado = s.getEstado();
    }

    private UsuarioDTO convertirDTO(Usuario u) {
        return new UsuarioDTO(u.getId_usuario(), u.getUsername());
    }

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getHechoId() { return hechoId; }
    public void setHechoId(UUID hechoId) { this.hechoId = hechoId; }

    public String getJustificacion() { return justificacion; }
    public void setJustificacion(String justificacion) { this.justificacion = justificacion; }

    public UsuarioDTO getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UsuarioDTO usuario) { this.usuarioId = usuario; }

    public EstadoSolicitudEliminacion getEstado() { return estado; }
    public void setEstado(EstadoSolicitudEliminacion estado) { this.estado = estado; }
}