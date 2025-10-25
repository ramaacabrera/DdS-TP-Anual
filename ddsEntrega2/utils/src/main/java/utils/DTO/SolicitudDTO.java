package utils.DTO;


import java.util.UUID;

public class SolicitudDTO {
    //private UUID id;
    private UUID hechoAsociado;
    private String justificacion;
    private UUID usuario;

    public SolicitudDTO() {}


    public void setHechoAsociado(UUID hechoAsociado) {
        this.hechoAsociado = hechoAsociado;
    }
    public void setusuario(UUID id_usuario) {this.usuario = id_usuario;}
    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public String getJustificacion() {
        return justificacion;
    }
    public UUID getHechoAsociado() {return hechoAsociado;}
    public UUID getUsuario() {return usuario; }


}
