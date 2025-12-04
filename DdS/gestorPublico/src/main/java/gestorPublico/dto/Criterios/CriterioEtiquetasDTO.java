package gestorPublico.dto.Criterios;

import gestorPublico.domain.Criterios.CriterioEtiquetas;
import gestorPublico.domain.HechosYColecciones.Etiqueta;
import gestorPublico.dto.Hechos.EtiquetaDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CriterioEtiquetasDTO extends CriterioDTO {
    private List<EtiquetaDTO> etiquetas = new ArrayList<>();

    public CriterioEtiquetasDTO() {
        super(null, "CRITERIO_ETIQUETAS");
    }
    public CriterioEtiquetasDTO(CriterioEtiquetas c){
        super(c.getId(), "CRITERIO_ETIQUETAS");
        this.etiquetas = this.convertirEtiquetas(c.getEtiquetas());
    }

    public List<EtiquetaDTO> convertirEtiquetas(List<Etiqueta> lista){
        List<EtiquetaDTO> etiquetas = new ArrayList<>();
        for(Etiqueta e : lista){
            EtiquetaDTO dto = new EtiquetaDTO(e);
            etiquetas.add(dto);
        }
        return etiquetas;
    }

    public CriterioEtiquetasDTO(UUID criterioId, List<EtiquetaDTO> etiquetas) {
        super(criterioId, "CRITERIO_ETIQUETAS");
        this.etiquetas = etiquetas;
    }

    // Getters y Setters
    public List<EtiquetaDTO> getEtiquetas() { return etiquetas; }
    public void setEtiquetas(List<EtiquetaDTO> etiquetas) { this.etiquetas = etiquetas; }

    @Override
    public String getQueryCondition() {
        return ""; // Según tu implementación original
    }

    @Override
    public Map<String, Object> getQueryParameters() {
        return Map.of(); // Según tu implementación original
    }
}