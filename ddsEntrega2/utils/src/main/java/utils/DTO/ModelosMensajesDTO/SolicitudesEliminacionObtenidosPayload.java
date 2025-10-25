package utils.DTO.ModelosMensajesDTO;

import utils.DTO.SolicitudDeEliminacionDTO;
import utils.DTO.SolicitudDeModificacionDTO;

import java.util.List;

public class SolicitudesEliminacionObtenidosPayload {

    public List<SolicitudDeEliminacionDTO> solicitudes;
    public SolicitudesEliminacionObtenidosPayload(){};

    public SolicitudesEliminacionObtenidosPayload(List<SolicitudDeEliminacionDTO> sol){
        this.solicitudes = sol;
    };

    public List<SolicitudDeEliminacionDTO> getSolicitudes(){
        return solicitudes;
    };

    public void setSolicitudes(List<SolicitudDeEliminacionDTO> solicitudes){
        this.solicitudes = solicitudes;
    }
}
