package agregador.graphQl.resolver;

import agregador.domain.HechosYColecciones.Hecho;
import agregador.dto.Hechos.HechoDTO;
import agregador.graphQl.dtoGraphQl.HechoFiltroDTO;
import agregador.graphQl.dtoGraphQl.PageHechoDTO;
import agregador.graphQl.dtoGraphQl.PageRequestDTO;
import agregador.service.HechoConsultaService;

import java.util.Map;

public class HechoQueryResolver {
    private final HechoConsultaService hechoConsultaService;

    public HechoQueryResolver(HechoConsultaService hechoConsultaService) {
        this.hechoConsultaService = hechoConsultaService;
    }

    public PageHechoDTO hechos(Map<String, Object> filtroInput,
                               Map<String, Object> pageInput) {
        return hechoConsultaService.buscarHechos(
                HechoFiltroDTO.fromMap(filtroInput),
                PageRequestDTO.fromMap(pageInput)
        );
    }


    public HechoDTO hecho(String id) {
        Hecho hecho = hechoConsultaService.obtenerHechoPorId(id);
        return new HechoDTO(hecho);
    }
}
