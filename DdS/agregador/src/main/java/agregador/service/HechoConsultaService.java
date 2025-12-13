package agregador.service;

import agregador.domain.Criterios.*;
import agregador.domain.HechosYColecciones.Hecho;
import agregador.domain.HechosYColecciones.Ubicacion;
import agregador.dto.Hechos.HechoDTO;
import agregador.graphQl.dtoGraphQl.HechoFiltroDTO;
import agregador.graphQl.dtoGraphQl.PageHechoDTO;
import agregador.graphQl.dtoGraphQl.PageRequestDTO;
import agregador.repository.HechoRepositorio;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class HechoConsultaService {

    private final HechoRepositorio hechoRepositorio;

    public HechoConsultaService(HechoRepositorio hechoRepositorio) {
        this.hechoRepositorio = hechoRepositorio;
    }

    // =========================
    // CONSULTA LISTADA
    // =========================
    public PageHechoDTO buscarHechos(HechoFiltroDTO filtro, PageRequestDTO pageRequest) {

        List<Criterio> criterios = construirCriterios(filtro);

        // 🔴 ENTIDADES JPA (solo acá)
        List<Hecho> hechos = hechoRepositorio.buscarHechos(criterios);

        // 🟢 CONVERTIR INMEDIATAMENTE A DTO
        List<HechoDTO> dtos = hechos.stream()
                .map(HechoDTO::new)
                .toList();

        // Paginado en memoria
        int total = dtos.size();
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();

        int from = Math.min((page - 1) * size, total);
        int to = Math.min(from + size, total);

        List<HechoDTO> contenido = dtos.subList(from, to);

        int totalPages = (int) Math.ceil((double) total / size);

        return new PageHechoDTO(
                contenido,
                page,
                size,
                totalPages,
                total
        );
    }

    // =========================
    // CONSULTA POR ID
    // =========================
    public Hecho obtenerHechoPorId(String id) {
        return hechoRepositorio.buscarPorId(UUID.fromString(id));
    }

    // =========================
    // ARMADO DE CRITERIOS
    // =========================
    private List<Criterio> construirCriterios(HechoFiltroDTO filtro) {

        List<Criterio> criterios = new ArrayList<>();
        if (filtro == null) return criterios;

        // 🔎 Búsqueda por título
        if (filtro.getTitulo() != null && !filtro.getTitulo().isBlank()) {
            criterios.add(new CriterioDeTexto(
                    List.of(filtro.getTitulo()),
                    TipoDeTexto.TITULO
            ));
        }

        // 🗂 Categoría
        if (filtro.getCategoria() != null && !filtro.getCategoria().isBlank()) {
            criterios.add(new CriterioDeTexto(
                    List.of(filtro.getCategoria()),
                    TipoDeTexto.CATEGORIA
            ));
        }

        // 📍 Ubicación
        if (filtro.getUbicacion() != null && !filtro.getUbicacion().isBlank()) {
            Ubicacion ubicacion = new Ubicacion();
            ubicacion.setDescripcion(filtro.getUbicacion());
            criterios.add(new CriterioUbicacion(ubicacion));
        }

        // 📅 Fecha de acontecimiento
        Date desde = parsearFecha(filtro.getFechaAcontecimientoDesde());
        Date hasta = parsearFecha(filtro.getFechaAcontecimientoHasta());

        if (desde != null || hasta != null) {
            criterios.add(new CriterioFecha(desde, hasta, "fechaDeAcontecimiento"));
        }

        return criterios;
    }

    private Date parsearFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) return null;

        return Date.from(
                LocalDate.parse(fecha)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        );
    }
}

