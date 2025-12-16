package cargadorDinamico.domain.DinamicaDto.ModelosMensajesDTO;

import cargadorDinamico.domain.DinamicaDto.SolicitudDeModificacionDTO;
import cargadorDinamico.domain.DinamicaDto.SolicitudModificacionSalienteDTO;

import java.util.List;

public class SolicitudesModificacionObtenidosPayload {

    public List<SolicitudModificacionSalienteDTO> solicitudes;
    public SolicitudesModificacionObtenidosPayload(){};

    public SolicitudesModificacionObtenidosPayload(List<SolicitudModificacionSalienteDTO> sol){
        this.solicitudes = sol;
    };

    public List<SolicitudModificacionSalienteDTO> getSolicitudes(){
        return solicitudes;
    };

    public void setSolicitudes(List<SolicitudModificacionSalienteDTO> solicitudes){
        this.solicitudes = solicitudes;
    }
}
