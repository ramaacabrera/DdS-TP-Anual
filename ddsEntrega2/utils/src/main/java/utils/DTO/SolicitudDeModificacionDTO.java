package utils.DTO;

<<<<<<<< HEAD:DdS/utils/src/main/java/utils/DTO/SolicitudDeModificacionDTO.java
import utils.Dominio.HechosYColecciones.HechoModificado;
import utils.Dominio.Solicitudes.EstadoSolicitudModificacion;
========
import utils.DTO.HechosYColecciones.HechoModificado;
import utils.DTO.Solicitudes.EstadoSolicitudModificacion;
>>>>>>>> 198c43e (Pruebas):ddsEntrega2/utils/src/main/java/utils/DTO/SolicitudDeModificacionDTO.java

public class SolicitudDeModificacionDTO extends SolicitudDTO {
    private HechoModificado hechoModificado;
    private EstadoSolicitudModificacion estadoSolicitudModificacion;

    public SolicitudDeModificacionDTO() {}

   public HechoModificado getHechoModificado() {
        return hechoModificado;
    }

    public void setHechoModificado(HechoModificado hechoModificado) {
        this.hechoModificado = hechoModificado;
    }

    public EstadoSolicitudModificacion getEstadoSolicitudModificacion() {
        return estadoSolicitudModificacion;
    }

    public void setEstadoSolicitudModificacion(EstadoSolicitudModificacion estadoSolicitudModificacion) {
        this.estadoSolicitudModificacion = estadoSolicitudModificacion;
    }
}
