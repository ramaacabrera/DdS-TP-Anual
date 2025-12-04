package gestorPublico.dto.Criterios;

import gestorPublico.domain.Criterios.CriterioTipoMultimedia;
import gestorPublico.domain.HechosYColecciones.TipoContenidoMultimedia;
import gestorPublico.dto.Hechos.TipoContenidoMultimediaDTO;

import java.util.Map;
import java.util.UUID;

public class CriterioTipoMultimediaDTO extends CriterioDTO {
    private TipoContenidoMultimediaDTO tipoContenidoMultimedia;

    public CriterioTipoMultimediaDTO() {
        super(null, "CRITERIO_TIPO_MULTIMEDIA");
    }
    public CriterioTipoMultimediaDTO(CriterioTipoMultimedia c){
        super(c.getId(), "CRITERIO_TIPO_MULTIMEDIA");
        this.tipoContenidoMultimedia = this.convertirTipo(c.getTipoContenidoMultimedia());
    }

    public TipoContenidoMultimediaDTO convertirTipo(TipoContenidoMultimedia t){
        switch (t){
            case IMAGEN:
                return TipoContenidoMultimediaDTO.IMAGEN;
            case VIDEO:
                return TipoContenidoMultimediaDTO.VIDEO;
            case AUDIO:
                return TipoContenidoMultimediaDTO.AUDIO;
            default:
                return null;
        }
    }

    public CriterioTipoMultimediaDTO(UUID criterioId, TipoContenidoMultimediaDTO tipoContenidoMultimedia) {
        super(criterioId, "CRITERIO_TIPO_MULTIMEDIA");
        this.tipoContenidoMultimedia = tipoContenidoMultimedia;
    }

    // Getters y Setters
    public TipoContenidoMultimediaDTO getTipoContenidoMultimedia() { return tipoContenidoMultimedia; }
    public void setTipoContenidoMultimedia(TipoContenidoMultimediaDTO tipoContenidoMultimedia) {
        this.tipoContenidoMultimedia = tipoContenidoMultimedia;
    }

    @Override
    public String getQueryCondition() {
        return ""; // Según tu implementación original
    }

    @Override
    public Map<String, Object> getQueryParameters() {
        return Map.of(); // Según tu implementación original
    }
}