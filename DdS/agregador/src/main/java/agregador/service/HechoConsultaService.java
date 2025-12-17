package agregador.service;

import agregador.domain.Criterios.*;
import agregador.domain.HechosYColecciones.Etiqueta;
import agregador.domain.HechosYColecciones.Hecho;
import agregador.domain.HechosYColecciones.Ubicacion;
import agregador.domain.fuente.TipoDeFuente;
import agregador.dto.Hechos.HechoDTO;
import agregador.graphQl.dtoGraphQl.HechoFiltroDTO;
import agregador.graphQl.dtoGraphQl.PageHechoDTO;
import agregador.graphQl.dtoGraphQl.PageRequestDTO;
import agregador.repository.HechoRepositorio;
import agregador.utils.BDUtils;
import jakarta.transaction.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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

        //ENTIDADES JPA
        List<Hecho> hechos = hechoRepositorio.buscarHechos(criterios);

        //CONVERTIR INMEDIATAMENTE A DTO
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

        // Búsqueda por título
        if (filtro.getTitulo() != null && !filtro.getTitulo().isBlank()) {
            criterios.add(new CriterioDeTexto(
                    List.of(filtro.getTitulo()),
                    TipoDeTexto.TITULO
            ));
        }

        // Categoría
        if (filtro.getCategoria() != null && !filtro.getCategoria().isBlank()) {
            criterios.add(new CriterioDeTexto(
                    List.of(filtro.getCategoria()),
                    TipoDeTexto.CATEGORIA
            ));
        }

        // Ubicación
        if (filtro.getUbicacion() != null && !filtro.getUbicacion().isBlank()) {
            Ubicacion ubicacion = new Ubicacion();
            ubicacion.setDescripcion(filtro.getUbicacion());
            criterios.add(new CriterioUbicacion(ubicacion.getDescripcion()));
        }

        //Fechas
        Date desde = parsearFecha(filtro.getFechaAcontecimientoDesde());
        Date hasta = parsearFecha(filtro.getFechaAcontecimientoHasta());

        if (desde != null || hasta != null) {
            criterios.add(new CriterioFecha(desde, hasta, "fechaDeAcontecimiento"));
        }

        Date cargaDesde = parsearFecha(filtro.getFechaCargaDesde());
        Date cargaHasta = parsearFecha(filtro.getFechaCargaHasta());

        if (cargaDesde != null || cargaHasta != null) {
            criterios.add(new CriterioFecha(cargaDesde, cargaHasta, "fechaDeCarga"));
        }

        // Contribuyente
        if (filtro.getContribuyente() != null && !filtro.getContribuyente().isBlank()) {
            criterios.add(new CriterioContribuyente(filtro.getContribuyente()));
        }

        // TipoDeFuente
        if (filtro.getTipoDeFuente() != null) {
            try {
                TipoDeFuente tipo = TipoDeFuente.valueOf(filtro.getTipoDeFuente());
                criterios.add(new CriterioTipoFuente(tipo));
            } catch (IllegalArgumentException ignored) {}
        }

        // Etiquetas
        if (filtro.getEtiquetas() != null && !filtro.getEtiquetas().isEmpty()) {
            List<Etiqueta> etiquetas = filtro.getEtiquetas().stream()
                    .map(nombre -> {
                        Etiqueta e = new Etiqueta();
                        e.setNombre(nombre);  // ← Ahora se setea el nombre, no el ID
                        return e;
                    })
                    .toList();

            criterios.add(new CriterioEtiquetas(etiquetas));
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
    @Transactional
    public List<HechoDTO> buscarHechosPorColeccion(UUID coleccionId, boolean consensuados) {
        EntityManager em = BDUtils.getEntityManager();

        try {
            BDUtils.comenzarTransaccion(em);

            List<Hecho> hechos =
                    hechoRepositorio.buscarPorColeccion(em, coleccionId, consensuados);


            for (Hecho h : hechos) {
                h.getEtiquetas().size();          // etiquetas
                if (h.getUbicacion() != null) {
                    h.getUbicacion().getDescripcion();
                }
                if (h.getFuente() != null) {
                    h.getFuente().getId();
                }
                if (h.getContribuyente() != null) {
                    h.getContribuyente().getUsername();
                }
            }
            List<HechoDTO> dtos = hechos.stream()
                    .map(HechoDTO::new)
                    .toList();

            BDUtils.commit(em);
            return dtos;

        } catch (Exception e) {
            BDUtils.rollback(em);
            throw e;

        } finally {
            em.close();
        }
    }
}

