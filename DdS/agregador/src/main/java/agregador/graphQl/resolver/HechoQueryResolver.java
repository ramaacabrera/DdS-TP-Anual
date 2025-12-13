package agregador.graphQl.resolver;

import agregador.domain.HechosYColecciones.Hecho;
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
        // Convertir inputs de GraphQL a DTOs
        HechoFiltroDTO filtro = HechoFiltroDTO.fromMap(filtroInput);
        PageRequestDTO pageRequest = PageRequestDTO.fromMap(pageInput);

        // Delegar al servicio
        return hechoConsultaService.buscarHechos(filtro, pageRequest);
    }

    public Hecho hecho(String id) {
        return hechoConsultaService.obtenerHechoPorId(id);
    }
}
