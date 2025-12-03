package gestorPublico.service;

import gestorPublico.domain.Criterios.*;
import gestorPublico.domain.HechosYColecciones.Hecho;
import gestorPublico.domain.HechosYColecciones.Ubicacion;
import gestorPublico.dto.FiltroHechosDTO;
import gestorPublico.repository.HechoRepositorio;
import gestorPublico.dto.Hechos.HechoDTO;
import gestorPublico.dto.PageDTO;
import gestorPublico.service.Normalizador.NormalizadorCategorias;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

public class HechoService {
    private final HechoRepositorio hechoRepositorio;
    private final String urlDinamica;

    public HechoService(HechoRepositorio hechoRepositorio, String urlDinamica) {
        this.hechoRepositorio = hechoRepositorio;
        this.urlDinamica = urlDinamica;
    }

    public PageDTO<HechoDTO> buscarHechos(FiltroHechosDTO filtro) {
        List<Criterio> criterios = armarCriterios(filtro);

        List<Hecho> todosLosHechos = hechoRepositorio.buscarHechos(criterios);

        int total = todosLosHechos.size();
        int totalPages = (int) Math.ceil(total / (double) filtro.limite);

        if (filtro.pagina > totalPages && totalPages > 0) filtro.pagina = totalPages;
        if (filtro.pagina < 1) filtro.pagina = 1;

        int fromIndex = (filtro.pagina - 1) * filtro.limite;
        int toIndex = Math.min(fromIndex + filtro.limite, total);

        List<Hecho> hechosPaginados;
        if (fromIndex >= total) {
            hechosPaginados = new ArrayList<>();
        } else {
            hechosPaginados = todosLosHechos.subList(fromIndex, toIndex);
        }

        List<HechoDTO> hechosDTO = hechosPaginados.stream()
                .map(HechoDTO::new)
                .collect(Collectors.toList());

        return new PageDTO<>(hechosDTO, filtro.pagina, filtro.limite, totalPages, total);
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
        return this.hechoRepositorio.buscarCategorias();
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

        if (f.latitud != null && f.longitud != null) {
            criterios.add(new CriterioUbicacion(new Ubicacion(f.latitud, f.longitud)));
        }

        if (f.contribuyente != null && !f.contribuyente.trim().isEmpty()) {
            criterios.add(new CriterioContribuyente(f.contribuyente));
        }

        return criterios;
    }
}