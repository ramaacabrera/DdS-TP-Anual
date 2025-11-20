package domain.DinamicaDto;

import domain.HechosYColeccionesD.Hecho_D;
import domain.Usuario.Usuario_D;

import java.util.UUID;

public class SolicitudModificacion_D_DTO {
    private UUID ID_hechoAsociado;
    private String justificacion;
    private Usuario_D usuario;
    private Hecho_D hechoModificado;

    SolicitudModificacion_D_DTO() {}

    SolicitudModificacion_D_DTO(UUID ID_hechoAsociado, String justificacion, Usuario_D usuario, Hecho_D hechoModificado) {
        this.ID_hechoAsociado = ID_hechoAsociado;
        this.justificacion = justificacion;
        this.usuario = usuario;
        this.hechoModificado = hechoModificado;
    }

    public void setID_HechoAsociado(UUID hechoAsociado) {
        this.ID_hechoAsociado = hechoAsociado;
    }
    public void setusuario(Usuario_D id_usuario) {this.usuario = id_usuario;}
    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }
    public void setHechoModificado(Hecho_D hechoModificado) {this.hechoModificado = hechoModificado; }

    public String getJustificacion() {
        return justificacion;
    }
    public UUID getID_HechoAsociado() {return ID_hechoAsociado;}
    public Usuario_D getUsuario() {return usuario; }
    public Hecho_D getHechoModificado() {return hechoModificado; }
}
