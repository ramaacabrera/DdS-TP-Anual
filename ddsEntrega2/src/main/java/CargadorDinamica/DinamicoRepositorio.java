package CargadorDinamica;

import utils.DTO.HechoDTO;
import utils.DTO.SolicitudDeModificacionDTO;
import utils.DTO.SolicitudDeEliminacionDTO;

import java.util.ArrayList;
import java.util.List;

public class DinamicoRepositorio {
    private List<HechoDTO> hechos;
    private List<SolicitudDeModificacionDTO> solicitudesModificacion;
    private List <SolicitudDeEliminacionDTO> solicitudesEliminacion;

    public DinamicoRepositorio() {
        this.hechos = new ArrayList<>();
        this.solicitudesModificacion = new ArrayList<>();
        this.solicitudesEliminacion = new ArrayList<>();
    }

    public List<HechoDTO> buscarHechos() {
        return hechos;
    }

    public void guardar(HechoDTO hecho) {
        hechos.add(hecho);
    }
//se guardan ambas solicitudes, de eliminacion y de modificacion
    public List<SolicitudDeModificacionDTO> buscarSolicitudes(){return solicitudesModificacion;}

    public void guardarSolicitud(SolicitudDeModificacionDTO solicitud){solicitudesModificacion.add(solicitud);}

    public List<SolicitudDeEliminacionDTO> buscarSolicitudesEliminacion() {
        return solicitudesEliminacion;
    }
    public void guardarSolicitudEliminacion(SolicitudDeEliminacionDTO solicitud) {
        solicitudesEliminacion.add(solicitud);
    }


    public void resetearHechos() { hechos = new ArrayList<>();
    }

    public void resetearSolicitudes() { solicitudesModificacion = new ArrayList<>();
        solicitudesEliminacion = new ArrayList<>();
    }
}
