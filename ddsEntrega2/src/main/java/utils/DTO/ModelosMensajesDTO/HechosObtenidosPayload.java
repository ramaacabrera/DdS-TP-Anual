package utils.DTO.ModelosMensajesDTO;

import utils.DTO.HechoDTO;

import java.util.ArrayList;
import java.util.List;

public class HechosObtenidosPayload {
    public List<HechoDTO> hechos;
    public HechosObtenidosPayload() {};

    public HechosObtenidosPayload(List<HechoDTO> h) {
        this.hechos = h;
    }

    public List<HechoDTO> getHechos() {
        return hechos;
    };

    public void setHechos(List<HechoDTO> hechos) {
        this.hechos = hechos;
    };
}