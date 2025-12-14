package agregador.service;

import agregador.domain.Criterios.Criterio;
import agregador.domain.Criterios.CriterioDeTexto;
import agregador.domain.Criterios.CriterioEtiquetas;
import agregador.domain.Criterios.TipoDeTexto;
import agregador.domain.HechosYColecciones.Coleccion;
import agregador.domain.HechosYColecciones.Etiqueta;
import agregador.dto.Coleccion.ColeccionDTO;
import agregador.graphQl.dtoGraphQl.ColeccionFiltroDTO;
import agregador.graphQl.dtoGraphQl.PageColeccionDTO;
import agregador.graphQl.dtoGraphQl.PageRequestDTO;
import agregador.repository.ColeccionRepositorio;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ColeccionConsultaService {

    private final ColeccionRepositorio coleccionRepositorio;

    public ColeccionConsultaService(ColeccionRepositorio coleccionRepositorio) {
        this.coleccionRepositorio = coleccionRepositorio;
    }

    // =========================
    // CONSULTA LISTADA
    // =========================
    public PageColeccionDTO buscarColecciones(ColeccionFiltroDTO filtro,
                                              PageRequestDTO pageRequest) {

        List<Coleccion> colecciones = coleccionRepositorio.obtenerTodas();

        List<ColeccionDTO> filtradas = colecciones.stream()
                .filter(c -> cumpleFiltro(c, filtro))
                .map(ColeccionDTO::new)
                .toList();

        int total = filtradas.size();
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();

        int from = Math.min((page - 1) * size, total);
        int to = Math.min(from + size, total);

        List<ColeccionDTO> contenido = filtradas.subList(from, to);
        int totalPages = (int) Math.ceil((double) total / size);

        return new PageColeccionDTO(
                contenido,
                page,
                size,
                totalPages,
                total
        );
    }

    // =========================
    // CONSULTA POR HANDLE
    // =========================
    public Coleccion obtenerColeccionPorHandle(String handle) {
        return coleccionRepositorio.buscarPorHandle(handle);
    }

    // =========================
    // FILTROS
    // =========================
    private boolean cumpleFiltro(Coleccion c, ColeccionFiltroDTO f) {

        if (f == null) return true;

        // Título
        if (f.getTitulo() != null &&
                !c.getTitulo().toLowerCase().contains(f.getTitulo().toLowerCase())) {
            return false;
        }

        // Descripción
        if (f.getDescripcion() != null &&
                (c.getDescripcion() == null ||
                        !c.getDescripcion().toLowerCase()
                                .contains(f.getDescripcion().toLowerCase()))) {
            return false;
        }

        // Algoritmo específico
        if (f.getAlgoritmoDeConsenso() != null) {
            if (c.getAlgoritmoDeConsenso() == null ||
                    !c.getAlgoritmoDeConsenso().name()
                            .equalsIgnoreCase(f.getAlgoritmoDeConsenso())) {
                return false;
            }
        }

        // Tiene / no tiene algoritmo
        if (f.getTieneAlgoritmo() != null) {
            if (f.getTieneAlgoritmo() && c.getAlgoritmoDeConsenso() == null)
                return false;
            if (!f.getTieneAlgoritmo() && c.getAlgoritmoDeConsenso() != null)
                return false;
        }

        // Cantidad mínima de hechos
        int cantHechos = c.getHechos() != null ? c.getHechos().size() : 0;

        if (f.getMinHechos() != null && cantHechos < f.getMinHechos())
            return false;

        if (f.getMaxHechos() != null && cantHechos > f.getMaxHechos())
            return false;

        return true;
    }
}