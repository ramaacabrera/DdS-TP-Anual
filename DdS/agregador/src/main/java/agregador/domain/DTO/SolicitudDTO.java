package agregador.domain.DTO;


import agregador.domain.Usuario.Usuario;

import java.util.UUID;

public class SolicitudDTO {
    //private UUID id;
    private UUID ID_hechoAsociado;
    private String justificacion;
    private Usuario usuario;

    public SolicitudDTO() {}


    public void setID_HechoAsociado(UUID ID_hechoAsociado) {
        this.ID_hechoAsociado = ID_hechoAsociado;
    }
    public void setusuario(Usuario id_usuario) {this.usuario = id_usuario;}
    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public String getJustificacion() {
        return justificacion;
    }
    public UUID getID_HechoAsociado() {return ID_hechoAsociado;}
    public Usuario getUsuario() {return usuario; }


}
