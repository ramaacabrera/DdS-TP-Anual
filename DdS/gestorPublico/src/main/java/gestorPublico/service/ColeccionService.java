package gestorPublico.service;

import gestorPublico.domain.Criterios.Criterio;
import gestorPublico.domain.Criterios.CriterioFecha;
import gestorPublico.domain.Criterios.CriterioUbicacion;
import gestorPublico.domain.HechosYColecciones.Coleccion;
import gestorPublico.domain.HechosYColecciones.Hecho;
import gestorPublico.domain.HechosYColecciones.ModosDeNavegacion;
import gestorPublico.domain.HechosYColecciones.Ubicacion;
import gestorPublico.dto.FiltroHechosDTO;
import gestorPublico.repository.ColeccionRepositorio;
import gestorPublico.dto.Coleccion.ColeccionDTO;
import gestorPublico.dto.Hechos.HechoDTO;
import gestorPublico.dto.PageDTO;

import java.util.*;
import java.util.stream.Collectors;

public class ColeccionService {
    private final ColeccionRepositorio coleccionRepositorio;

    public ColeccionService(ColeccionRepositorio coleccionRepositorio) {
        this.coleccionRepositorio = coleccionRepositorio;
    }

    public PageDTO<ColeccionDTO> obtenerTodasLasColecciones(int pagina, int limite) {
        if (pagina < 1) pagina = 1;
        if (limite < 1) limite = 10;

        long totalRegistros = coleccionRepositorio.contarTodas();

        int totalPages = (int) Math.ceil(totalRegistros / (double) limite);

        if (pagina > totalPages && totalRegistros > 0) {
            return new PageDTO<>(new ArrayList<>(), pagina, limite, totalPages, (int) totalRegistros);
        }

        List<Coleccion> coleccionesPaginadas = coleccionRepositorio.obtenerPaginadas(pagina, limite);

        List<ColeccionDTO> dtos = coleccionesPaginadas.stream()
                .map(ColeccionDTO::new)
                .collect(Collectors.toList());

        return new PageDTO<>(dtos, pagina, limite, totalPages, (int) totalRegistros);
    }

    public ColeccionDTO obtenerColeccionPorId(UUID id) {
        Coleccion coleccion = coleccionRepositorio.buscarPorHandle(id.toString());
        if (coleccion == null) return null;
        return new ColeccionDTO(coleccion);
    }

    public List<HechoDTO> obtenerHechosDeColeccion(UUID coleccionId, FiltroHechosDTO filtro, String modoNavegacionStr) {
        Coleccion coleccion = coleccionRepositorio.buscarPorHandle(coleccionId.toString());
        if (coleccion == null) return null;

        ModosDeNavegacion modo = ModosDeNavegacion.IRRESTRICTA;
        if ("CURADA".equals(modoNavegacionStr)) {
            try {
                modo = ModosDeNavegacion.valueOf(modoNavegacionStr);
            } catch (Exception e) {}
        }

        List<Criterio> criterios = armarCriterios(filtro);

        List<Hecho> hechosFiltrados = coleccion.obtenerHechosQueCumplen(criterios, modo);

        return hechosFiltrados.stream()
                .map(HechoDTO::new)
                .collect(Collectors.toList());
    }

    private List<Criterio> armarCriterios(FiltroHechosDTO f) {
        List<Criterio> criterios = new ArrayList<>();
        if (f.fechaCargaDesde != null || f.fechaCargaHasta != null)
            criterios.add(new CriterioFecha(f.fechaCargaDesde, f.fechaCargaHasta, "fechaDeCarga"));

        if (f.fechaAcontecimientoDesde != null || f.fechaAcontecimientoHasta != null)
            criterios.add(new CriterioFecha(f.fechaAcontecimientoDesde, f.fechaAcontecimientoHasta, "fechaDeAcontecimiento"));

        if (f.descripcion != null && !f.descripcion.equals(""))
            criterios.add(new CriterioUbicacion(new Ubicacion(0, 0, f.descripcion)));

        // Agrega más si son necesarios (categoria, etc.)
        return criterios;
    }
}