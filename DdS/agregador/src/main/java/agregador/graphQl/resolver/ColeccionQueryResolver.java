package agregador.graphQl.resolver;

import agregador.domain.HechosYColecciones.Coleccion;
import agregador.dto.Coleccion.ColeccionDTO;
import agregador.graphQl.dtoGraphQl.ColeccionFiltroDTO;
import agregador.graphQl.dtoGraphQl.PageRequestDTO;
import agregador.graphQl.dtoGraphQl.PageColeccionDTO;
import agregador.service.ColeccionConsultaService;

import java.util.Map;

public class ColeccionQueryResolver {

    private final ColeccionConsultaService coleccionConsultaService;

    public ColeccionQueryResolver(ColeccionConsultaService service) {
        this.coleccionConsultaService = service;
    }

    public PageColeccionDTO colecciones(Map<String, Object> filtroInput,
                                        Map<String, Object> pageInput) {

        return coleccionConsultaService.buscarColecciones(
                ColeccionFiltroDTO.fromMap(filtroInput),
                PageRequestDTO.fromMap(pageInput)
        );
    }

    public ColeccionDTO coleccion(String id) {
        return new ColeccionDTO(
                coleccionConsultaService.obtenerColeccionPorHandle(id)
        );
    }
}

