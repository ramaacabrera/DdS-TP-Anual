package utils.DTO;


import utils.Dominio.Usuario.Usuario;

public class SolicitudDTO {
    //private UUID id;
<<<<<<<< HEAD:DdS/utils/src/main/java/utils/DTO/SolicitudDTO.java
    private HechoDTO hechoAsociado;
========
    private UUID hechoAsociado;
>>>>>>>> 198c43e (Pruebas):ddsEntrega2/utils/src/main/java/utils/DTO/SolicitudDTO.java
    private String justificacion;
    private UUID usuario;

    public SolicitudDTO() {}


<<<<<<<< HEAD:DdS/utils/src/main/java/utils/DTO/SolicitudDTO.java
    public void setHechoAsociado(HechoDTO hechoAsociado) {
========
    public void setHechoAsociado(UUID hechoAsociado) {
>>>>>>>> 198c43e (Pruebas):ddsEntrega2/utils/src/main/java/utils/DTO/SolicitudDTO.java
        this.hechoAsociado = hechoAsociado;
    }
    public void setusuario(UUID id_usuario) {this.usuario = id_usuario;}
    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public String getJustificacion() {
        return justificacion;
    }
<<<<<<<< HEAD:DdS/utils/src/main/java/utils/DTO/SolicitudDTO.java
    public HechoDTO getHechoAsociado() {return hechoAsociado;}
    public Usuario getUsuario() {return usuario; }
========
    public UUID getHechoAsociado() {return hechoAsociado;}
    public UUID getUsuario() {return usuario; }
>>>>>>>> 198c43e (Pruebas):ddsEntrega2/utils/src/main/java/utils/DTO/SolicitudDTO.java


}
