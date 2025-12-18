package gestorPublico.service;

import gestorPublico.domain.Criterios.*;
import gestorPublico.domain.HechosYColecciones.ContenidoMultimedia;
import gestorPublico.domain.HechosYColecciones.EstadoHecho;
import gestorPublico.domain.HechosYColecciones.Hecho;
import gestorPublico.domain.HechosYColecciones.TipoContenidoMultimedia;
import gestorPublico.domain.HechosYColecciones.Ubicacion;
import gestorPublico.dto.FiltroHechosDTO;
import gestorPublico.repository.HechoRepositorio;
import gestorPublico.dto.Hechos.HechoDTO;
import gestorPublico.dto.PageDTO;
import gestorPublico.service.Normalizador.DiccionarioCategorias;
import gestorPublico.service.Normalizador.NormalizadorCategorias;
import gestorPublico.service.Normalizador.ServicioGeoref;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HechoService {
    private final HechoRepositorio hechoRepositorio;
    private final String urlDinamica;
    private final DiccionarioCategorias diccionarioCategorias;
    private final ServicioGeoref servicioGeoref;

    public HechoService(HechoRepositorio hechoRepositorio, DiccionarioCategorias diccionarioCategorias, String urlDinamica) {
        this.hechoRepositorio = hechoRepositorio;
        this.urlDinamica = urlDinamica;
        this.diccionarioCategorias = diccionarioCategorias;
        this.servicioGeoref = new ServicioGeoref(null,null);
    }

    public PageDTO<HechoDTO> buscarHechos(FiltroHechosDTO filtro) {
        List<Criterio> criterios = armarCriterios(filtro);

        long totalElementsLong = hechoRepositorio.contarHechos(criterios);
        int total = (int) totalElementsLong;

        int totalPages = (int) Math.ceil(total / (double) filtro.size);

        if (filtro.page > totalPages && totalPages > 0) filtro.page = totalPages;
        if (filtro.page < 1) filtro.page = 1;

        List<Hecho> hechosPaginados;
        if (total == 0) {
            hechosPaginados = new ArrayList<>();
        } else {
            hechosPaginados = hechoRepositorio.buscarHechos(criterios, filtro.page, filtro.size);
        }

        List<HechoDTO> hechosDTO = hechosPaginados.stream()
                .map(HechoDTO::new)
                .collect(Collectors.toList());

        return new PageDTO<>(hechosDTO, filtro.page, filtro.size, totalPages, total);
    }

    public HechoDTO obtenerHechoPorId(UUID id) {
        Hecho hecho = hechoRepositorio.buscarPorId(id);
        if (hecho == null) return null;
        return new HechoDTO(hecho);
    }

    public HttpResponse<String> crearHechoProxy(String bodyJson) {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlDinamica + "/hechos"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                    .build();

            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con el servicio dinámico: " + e.getMessage(), e);
        }
    }

    public List<String> buscarCategorias() {
        return this.diccionarioCategorias.obtenerCategoriasCanonicas();
    }

    private List<Criterio> armarCriterios(FiltroHechosDTO f) {
        List<Criterio> criterios = new ArrayList<>();

        if (f.textoBusqueda != null && !f.textoBusqueda.trim().isEmpty()) {
            List<String> palabras = Arrays.asList(f.textoBusqueda.trim().split("\\s+"));
            criterios.add(new CriterioDeTexto(palabras, TipoDeTexto.BUSQUEDA));
        }

        if (f.categoria != null && !f.categoria.trim().isEmpty()){
            String categoria = NormalizadorCategorias.normalizar(f.categoria);
            criterios.add(new CriterioDeTexto(categoria.lines().toList(), TipoDeTexto.CATEGORIA));
        }

        if (f.fechaCargaDesde != null || f.fechaCargaHasta != null) {
            criterios.add(new CriterioFecha(f.fechaCargaDesde, f.fechaCargaHasta, "fechaDeCarga"));
        }

        if (f.fechaAcontecimientoDesde != null || f.fechaAcontecimientoHasta != null) {
            criterios.add(new CriterioFecha(f.fechaAcontecimientoDesde, f.fechaAcontecimientoHasta, "fechaDeAcontecimiento"));
        }

        if (f.descripcion != null && !f.descripcion.equals("")) {
            criterios.add(new CriterioUbicacion(f.descripcion));
        }

        if (f.contribuyente != null && !f.contribuyente.trim().isEmpty()) {
            criterios.add(new CriterioContribuyente(f.contribuyente));
        }

        return criterios;
    }
}