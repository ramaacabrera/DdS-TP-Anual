package utils.DTO;


import utils.Dominio.Usuario.Usuario;

public class SolicitudDTO {
    //private UUID id;
    private HechoDTO hechoAsociado;
    private String justificacion;
    private Usuario usuario;

    public SolicitudDTO() {}


    public void setHechoAsociado(HechoDTO hechoAsociado) {
        this.hechoAsociado = hechoAsociado;
    }
    public void setusuario(Usuario id_usuario) {this.usuario = id_usuario;}
    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public String getJustificacion() {
        return justificacion;
    }
    public HechoDTO getHechoAsociado() {return hechoAsociado;}
    public Usuario getUsuario() {return usuario; }


}
