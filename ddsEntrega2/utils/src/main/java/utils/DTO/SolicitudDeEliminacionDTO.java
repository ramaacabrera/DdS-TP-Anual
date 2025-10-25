package utils.DTO;

<<<<<<<< HEAD:DdS/utils/src/main/java/utils/DTO/SolicitudDeEliminacionDTO.java
import utils.Dominio.Solicitudes.EstadoSolicitudEliminacion;
========
import utils.DTO.Solicitudes.EstadoSolicitudEliminacion;
>>>>>>>> 198c43e (Pruebas):ddsEntrega2/utils/src/main/java/utils/DTO/SolicitudDeEliminacionDTO.java

public class SolicitudDeEliminacionDTO extends SolicitudDTO {
    private EstadoSolicitudEliminacion estado;

    public SolicitudDeEliminacionDTO() {}

    public EstadoSolicitudEliminacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitudEliminacion estado) {
        this.estado = estado;
    }
}
