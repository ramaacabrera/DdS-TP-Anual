package Persistencia;

import org.example.agregador.DTO.HechoDTO;
import org.example.agregador.DTO.SolicitudDeEliminacionDTO;
import org.example.agregador.DTO.SolicitudDeModificacionDTO;
import org.example.agregador.Solicitudes.SolicitudDeEliminacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DinamicoRepositorio {

    private List<HechoDTO> hechos;
    private List<SolicitudDeModificacionDTO> solicitudesModificacion;
    private List<SolicitudDeEliminacionDTO> solicitudesEliminacion;

    public DinamicoRepositorio() { this.hechos = new ArrayList<>(); this.solicitudesModificacion = new ArrayList<>(); this.solicitudesEliminacion = new ArrayList<>();}

    public void guardarHecho(HechoDTO hecho){ hechos.add(hecho);}

    public List<HechoDTO> obtenerHechos() {
        List<HechoDTO> hechosADevolver = hechos;
        hechos.clear();
        return hechosADevolver;
    }

    public List<SolicitudDeModificacionDTO> obtenerSolicitudDeModificacion() {
        List<SolicitudDeModificacionDTO> solicitudesADevolver = solicitudesModificacion;
        solicitudesModificacion.clear();
        return solicitudesADevolver;
    }

    public List<SolicitudDeEliminacionDTO> obtenerSolicitudDeEliminacion() {
        List<SolicitudDeEliminacionDTO> solicitudesADevolver = solicitudesEliminacion;
        solicitudesEliminacion.clear();
        return solicitudesADevolver;
    }

    public void guardarSolicitudModificacion(SolicitudDeModificacionDTO solicitud){ solicitudesModificacion.add(solicitud);}

    public void guardarSolicitudEliminacion(SolicitudDeEliminacionDTO solicitud){ solicitudesEliminacion.add(solicitud);}
    /*
    public Optional<SolicitudDeEliminacion> buscarPorId(String id){
        return solicitudesEliminacion.stream().findFirst(solicitud -> solicitud.getId().equals(id));

   }

    */
}
