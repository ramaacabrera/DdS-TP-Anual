package cargadorDinamico.DinamicaDto;

import cargadorDinamico.Dominio.Usuario.Usuario_D;
import cargadorDinamico.Dominio.HechosYColeccionesD.Hecho_D;
import java.util.UUID;

public class SolicitudEliminacion_D_DTO {
    private UUID id_hechoAsociado;
    private String justificacion;
    private Usuario_D usuario;

    SolicitudEliminacion_D_DTO() {}

    SolicitudEliminacion_D_DTO(UUID id_hechoAsociado, String justificacion, Usuario_D usuario) {
        this.id_hechoAsociado = id_hechoAsociado; //que esta en el agregador
        this.justificacion = justificacion;
        this.usuario = usuario;
    }

    public void setID_hechoAsociado(UUID id_hechoAsociado) {
        this.id_hechoAsociado = id_hechoAsociado;
    }
    public void setusuario(Usuario_D id_usuario) {this.usuario = id_usuario;}
    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public String getJustificacion() {
        return justificacion;
    }
    public UUID getID_hechoAsociado() {return id_hechoAsociado;}
    public Usuario_D getUsuario() {return usuario; }
}
