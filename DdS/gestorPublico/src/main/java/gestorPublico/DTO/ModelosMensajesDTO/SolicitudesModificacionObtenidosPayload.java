package gestorPublico.DTO.ModelosMensajesDTO;

import gestorPublico.DTO.Solicitudes.SolicitudDeModificacionDTO;

import java.util.List;

public class SolicitudesModificacionObtenidosPayload {

    public List<SolicitudDeModificacionDTO> solicitudes;
    public SolicitudesModificacionObtenidosPayload(){};

    public SolicitudesModificacionObtenidosPayload(List<SolicitudDeModificacionDTO> sol){
        this.solicitudes = sol;
    };

    public List<SolicitudDeModificacionDTO> getSolicitudes(){
        return solicitudes;
    };

    public void setSolicitudes(List<SolicitudDeModificacionDTO> solicitudes){
        this.solicitudes = solicitudes;
    }
}
