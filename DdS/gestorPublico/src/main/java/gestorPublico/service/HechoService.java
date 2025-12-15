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
        // Asumiendo que ServicioGeoref tiene el constructor sin argumentos que arreglamos antes
        this.servicioGeoref = new ServicioGeoref();
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

    public void modificarHecho(String hechoId, String usernameSolicitante, Map<String, Object> cambios) {
        Hecho hecho = hechoRepositorio.buscarPorId(UUID.fromString(hechoId));
        if (hecho == null) {
            throw new IllegalArgumentException("El hecho no existe.");
        }

        String usernamePropietario = hecho.getContribuyente().getUsername();

        if (!usernamePropietario.equals(usernameSolicitante)) {
            throw new SecurityException("No tienes permiso para editar este hecho. Solo el autor (" + usernamePropietario + ") puede hacerlo.");
        }

        Date fechaCarga = hecho.getFechaDeCarga();
        Date fechaActual = new Date();
        long diasTranscurridos = TimeUnit.MILLISECONDS.toDays(fechaActual.getTime() - fechaCarga.getTime());

        if (diasTranscurridos > 7) {
            throw new IllegalStateException("El periodo de edición de 7 días ha expirado.");
        }

        if (cambios.containsKey("titulo")) hecho.setTitulo((String) cambios.get("titulo"));
        if (cambios.containsKey("descripcion")) hecho.setDescripcion((String) cambios.get("descripcion"));
        if (cambios.containsKey("categoria")) hecho.setCategoria((String) cambios.get("categoria"));

        if (cambios.containsKey("fechaDeAcontecimiento")) {
            Object fechaObj = cambios.get("fechaDeAcontecimiento");

            if (fechaObj != null && !fechaObj.toString().isEmpty()) {
                try {
                    Instant instant = Instant.parse(fechaObj.toString());

                    hecho.setFechaDeAcontecimiento(Date.from(instant));

                } catch (Exception e) {
                    System.err.println("Error parseando fecha: " + fechaObj + ". Error: " + e.getMessage());
                }
            }
        }

        if (cambios.containsKey("ubicacion")) {
            Map<String, Object> ubicacionMap = (Map<String, Object>) cambios.get("ubicacion");

            double lat = Double.parseDouble(ubicacionMap.get("latitud").toString());
            double lon = Double.parseDouble(ubicacionMap.get("longitud").toString());
            String desc = (String) ubicacionMap.get("descripcion");

            String descripcionFinal = servicioGeoref.enriquecerDescripcion(lat, lon, desc);

            Ubicacion ubicacionNueva = new Ubicacion(lat, lon, descripcionFinal);
            hecho.setUbicacion(ubicacionNueva);
        }

        if (cambios.containsKey("contenidoMultimedia")) {
            try {
                List<Map<String, Object>> listaMedia = (List<Map<String, Object>>) cambios.get("contenidoMultimedia");

                if (hecho.getContenidoMultimedia() != null) {
                    hecho.getContenidoMultimedia().clear();
                } else {
                    hecho.setContenidoMultimedia(new ArrayList<>());
                }

                if (listaMedia != null && !listaMedia.isEmpty()) {
                    List<ContenidoMultimedia> nuevosContenidos = new ArrayList<>();

                    for (Map<String, Object> item : listaMedia) {
                        String url = (String) item.get("contenido");
                        String tipoStr = (String) item.get("tipoContenido");

                        if (url != null && tipoStr != null) {
                            try {
                                TipoContenidoMultimedia tipo = TipoContenidoMultimedia.valueOf(tipoStr);
                                ContenidoMultimedia cm = new ContenidoMultimedia(tipo, url);
                                cm.setHecho(hecho);
                                nuevosContenidos.add(cm);
                            } catch (IllegalArgumentException e) {
                                System.err.println("Tipo de contenido desconocido: " + tipoStr);
                            }
                        }
                    }
                    hecho.getContenidoMultimedia().addAll(nuevosContenidos);
                }
            } catch (Exception e) {
                System.err.println("Error procesando multimedia: " + e.getMessage());
                e.printStackTrace();
            }
        }

        hechoRepositorio.actualizar(hecho);
    }
}