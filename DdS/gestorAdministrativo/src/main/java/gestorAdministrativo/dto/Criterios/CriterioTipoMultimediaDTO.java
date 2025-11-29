package gestorAdministrativo.dto.Criterios;

import gestorAdministrativo.dto.Hechos.TipoContenidoMultimediaDTO;

import java.util.Map;
import java.util.UUID;

public class CriterioTipoMultimediaDTO extends CriterioDTO {
    private TipoContenidoMultimediaDTO tipoContenidoMultimedia;

    public CriterioTipoMultimediaDTO() {
        super(null, "CRITERIO_TIPO_MULTIMEDIA");
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