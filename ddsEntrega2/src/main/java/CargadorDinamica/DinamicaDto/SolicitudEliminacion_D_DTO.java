package CargadorDinamica.DinamicaDto;

import CargadorDinamica.Dominio.HechosYColecciones.Hecho_D;
import CargadorDinamica.Dominio.Usuario.Usuario_D;
import CargadorDinamica.Dominio.HechosYColecciones.Hecho_D;
import java.util.UUID;

public class SolicitudEliminacion_D_DTO {
    private Hecho_D hechoAsociado;
    private String justificacion;
    private Usuario_D usuario;

    SolicitudEliminacion_D_DTO() {}

    SolicitudEliminacion_D_DTO(Hecho_D hechoAsociado, String justificacion, Usuario_D usuario) {
        this.hechoAsociado = hechoAsociado;
        this.justificacion = justificacion;
        this.usuario = usuario;
    }

    public void setHechoAsociado(Hecho_D hechoAsociado) {
        this.hechoAsociado = hechoAsociado;
    }
    public void setusuario(Usuario_D id_usuario) {this.usuario = id_usuario;}
    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public String getJustificacion() {
        return justificacion;
    }
    public Hecho_D getHechoAsociado() {return hechoAsociado;}
    public Usuario_D getUsuario() {return usuario; }
}
