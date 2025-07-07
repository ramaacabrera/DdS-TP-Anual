package org.example.fuenteDinamica;

import org.example.agregador.SolicitudDeEliminacion;
import org.example.agregador.SolicitudDeModificacion;
import org.example.fuente.*;
import java.util.ArrayList;
import java.util.List;

public class DinamicoRepositorio {

    private List<HechoDTO> hechos;
    private List<SolicitudDeModificacionDTO> solicitudesModificacion;
    private List<SolicitudDeEliminacionDTO> solicitudesEliminacion;

    public DinamicoRepositorio() { this.hechos = new ArrayList<>(); this.solicitudesModificacion = new ArrayList<>(); this.solicitudesEliminacion = new ArrayList<>();}

    public void guardarHecho(HechoDTO hecho){ hechos.add(hecho);}

    public List<HechoDTO> obtenerHechos() {
        List<HechoDTO> hechosADevolver = hechos;
        hechos = new ArrayList<>();
        return hechosADevolver;
    }

    public List<SolicitudDeModificacionDTO> obtenerSolicitudDeModificacion() {
        List<SolicitudDeModificacionDTO> solicitudesADevolver = solicitudesModificacion;
        solicitudesModificacion = new ArrayList<>();
        return solicitudesADevolver;
    }

    public List<SolicitudDeEliminacionDTO> obtenerSolicitudDeEliminacion() {
        List<SolicitudDeEliminacionDTO> solicitudesADevolver = solicitudesEliminacion;
        solicitudesEliminacion = new ArrayList<>();
        return solicitudesADevolver;
    }

    public void guardarSolicitudModificacion(SolicitudDeModificacionDTO solicitud){ solicitudesModificacion.add(solicitud);}

    public void guardarSolicitudEliminacion(SolicitudDeEliminacionDTO solicitud){ solicitudesEliminacion.add(solicitud);}

}
