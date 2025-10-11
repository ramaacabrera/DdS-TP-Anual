package utils.DTO;

import Agregador.HechosYColecciones.Hecho;
import Agregador.Usuario.Usuario;

import java.util.UUID;

public class SolicitudDTO {
    //private UUID id;
    private Hecho hechoAsociado;
    private String justificacion;
    private Usuario usuario;

    public SolicitudDTO() {}


    public void setHechoAsociado(Hecho hechoAsociado) {
        this.hechoAsociado = hechoAsociado;
    }
    public void setusuario(Usuario id_usuario) {this.usuario = id_usuario;}
    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public String getJustificacion() {
        return justificacion;
    }
    public Hecho getHechoAsociado() {return hechoAsociado;}
    public Usuario getUsuario() {return usuario; }


}
