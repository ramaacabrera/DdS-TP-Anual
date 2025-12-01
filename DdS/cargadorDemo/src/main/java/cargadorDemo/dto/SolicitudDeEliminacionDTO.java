package cargadorDemo.dto;

import cargadorDemo.domain.Solicitudes.EstadoSolicitudEliminacion;

import java.util.UUID;

public class SolicitudDeEliminacionDTO {
    private UUID id;
    private UUID hechoId;
    private String justificacion;
    private UsuarioDTO usuario;
    private EstadoSolicitudEliminacion estado;

    public SolicitudDeEliminacionDTO() {}

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getHechoId() { return hechoId; }
    public void setHechoId(UUID hechoId) { this.hechoId = hechoId; }

    public String getJustificacion() { return justificacion; }
    public void setJustificacion(String justificacion) { this.justificacion = justificacion; }

    public UsuarioDTO getUsuario() { return usuario; }
    public void setUsuario(UsuarioDTO usuario) { this.usuario = usuario; }

    public EstadoSolicitudEliminacion getEstado() { return estado; }
    public void setEstado(EstadoSolicitudEliminacion estado) { this.estado = estado; }
}