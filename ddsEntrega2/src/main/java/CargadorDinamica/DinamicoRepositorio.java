package CargadorDinamica;

import CargadorDinamica.DinamicaDto.Hecho_D_DTO;
import utils.DTO.HechoDTO;
import utils.DTO.SolicitudDeModificacionDTO;
import utils.DTO.SolicitudDeEliminacionDTO;

import java.util.ArrayList;
import java.util.List;

public class DinamicoRepositorio {
    private List<Hecho_D_DTO> hechos;
    private List<SolicitudDeModificacionDTO> solicitudesModificacion;
    private List <SolicitudDeEliminacionDTO> solicitudesEliminacion;

    public DinamicoRepositorio() {
        this.hechos = new ArrayList<>();
        this.solicitudesModificacion = new ArrayList<>();
        this.solicitudesEliminacion = new ArrayList<>();
    }

    public List<Hecho_D_DTO> buscarHechos() {
        return hechos;
    }

    public void guardarHecho(Hecho_D_DTO hecho) {
        hechos.add(hecho);
    }

    public List<SolicitudDeModificacionDTO> buscarSolicitudesModificacion(){return solicitudesModificacion;}

    public void guardarSolicitudModificacion(SolicitudDeModificacionDTO solicitud){solicitudesModificacion.add(solicitud);}

    public List<SolicitudDeEliminacionDTO> buscarSolicitudesEliminacion() {
        return solicitudesEliminacion;
    }

    public void guardarSolicitudEliminacion(SolicitudDeEliminacionDTO solicitud) {solicitudesEliminacion.add(solicitud);}

    public void resetearHechos() { hechos = new ArrayList<>();}

    public void resetearSolicitudesModificacion() { solicitudesModificacion = new ArrayList<>();}

    public void resetearSolicitudesEliminacion() { solicitudesEliminacion = new ArrayList<>();}
}
