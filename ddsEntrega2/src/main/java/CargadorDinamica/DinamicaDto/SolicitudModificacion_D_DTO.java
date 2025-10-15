package CargadorDinamica.DinamicaDto;

import CargadorDinamica.Dominio.HechosYColecciones.Hecho_D;
import CargadorDinamica.Dominio.Usuario.Usuario_D;

import java.util.UUID;

public class SolicitudModificacion_D_DTO {
    private Hecho_D hechoAsociado;
    private String justificacion;
    private Usuario_D usuario;
    private Hecho_D hechoModificado;

    SolicitudModificacion_D_DTO() {}

    SolicitudModificacion_D_DTO(Hecho_D hechoAsociado, String justificacion, Usuario_D usuario, Hecho_D hechoModificado) {
        this.hechoAsociado = hechoAsociado;
        this.justificacion = justificacion;
        this.usuario = usuario;
        this.hechoModificado = hechoModificado;
    }

    public void setHechoAsociado(Hecho_D hechoAsociado) {
        this.hechoAsociado = hechoAsociado;
    }
    public void setusuario(Usuario_D id_usuario) {this.usuario = id_usuario;}
    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }
    public void setHechoModificado(Hecho_D hechoModificado) {this.hechoModificado = hechoModificado; }

    public String getJustificacion() {
        return justificacion;
    }
    public Hecho_D getHechoAsociado() {return hechoAsociado;}
    public Usuario_D getUsuario() {return usuario; }
    public Hecho_D getHechoModificado() {return hechoModificado; }
}
