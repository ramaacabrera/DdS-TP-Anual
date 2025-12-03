package cargadorDinamico.domain.DinamicaDto;

import cargadorDinamico.domain.Usuario.Usuario_D;

import java.util.UUID;

public class SolicitudEliminacion_D_DTO {
    private UUID hechoId;
    private String justificacion;
    private Usuario_D usuario;

    SolicitudEliminacion_D_DTO() {}

    SolicitudEliminacion_D_DTO(UUID id_hechoAsociado, String justificacion, Usuario_D usuario) {
        this.hechoId = id_hechoAsociado; //que esta en el agregador
        this.justificacion = justificacion;
        this.usuario = usuario;
    }

    public void setID_hechoAsociado(UUID id_hechoAsociado) {
        this.hechoId = id_hechoAsociado;
    }
    public void setusuario(Usuario_D id_usuario) {this.usuario = id_usuario;}
    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public String getJustificacion() {
        return justificacion;
    }
    public UUID getID_hechoAsociado() {return hechoId;}
    public Usuario_D getUsuario() {return usuario; }
}
