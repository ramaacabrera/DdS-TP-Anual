package cargadorDinamico.DinamicaDto;

import cargadorDinamico.Dominio.Usuario.Usuario_D;
import cargadorDinamico.Dominio.HechosYColeccionesD.Hecho_D;
import java.util.UUID;

public class SolicitudEliminacion_D_DTO {
    private UUID ID_hechoAsociado;
    private String justificacion;
    private Usuario_D usuario;

    SolicitudEliminacion_D_DTO() {}

    SolicitudEliminacion_D_DTO(UUID ID_hechoAsociado, String justificacion, Usuario_D usuario) {
        this.ID_hechoAsociado = ID_hechoAsociado;
        this.justificacion = justificacion;
        this.usuario = usuario;
    }

    public void setHechoAsociado(UUID ID_hechoAsociado) {
        this.ID_hechoAsociado = ID_hechoAsociado;
    }
    public void setusuario(Usuario_D id_usuario) {this.usuario = id_usuario;}
    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public String getJustificacion() {
        return justificacion;
    }
    public UUID getHechoAsociado() {return ID_hechoAsociado;}
    public Usuario_D getUsuario() {return usuario; }
}
