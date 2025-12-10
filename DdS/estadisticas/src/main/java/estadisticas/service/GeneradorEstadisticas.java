package estadisticas.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import estadisticas.domainEstadisticas.Estadisticas;
import estadisticas.domainEstadisticas.EstadisticasCategoria;
import estadisticas.domainEstadisticas.EstadisticasColeccion;
import estadisticas.repository.ConexionAgregador;
import estadisticas.repository.BDUtilsEstadisticas;
import estadisticas.service.normalizador.NormalizadorCategorias;

import javax.persistence.EntityManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class GeneradorEstadisticas {
    private final ConexionAgregador conexionAgregador;
    private Estadisticas estadisticasActual = null;
    private volatile boolean enEjecucion = false;

    public GeneradorEstadisticas(ConexionAgregador conexionAgregador){
        this.conexionAgregador = conexionAgregador;
    }

    public void actualizarEstadisticas() {
        if (enEjecucion) {
            System.out.println("Actualización ya en curso, omitiendo...");
            return;
        }

        enEjecucion = true;
        EntityManager em = BDUtilsEstadisticas.getEntityManager();

        try {
            System.out.println("=== Iniciando actualización de estadísticas: " + new Date() + " ===");
            em.getTransaction().begin();

            // Resetear la referencia anterior
            this.estadisticasActual = null;

            this.actualizarEstadisticasBase(em);
            this.actualizarEstadisticasCategoria(em);
            this.actualizarEstadisticasColeccion(em);

            em.getTransaction().commit();
            System.out.println("=== Actualización completada exitosamente ===");

        } catch (Exception e) {
            System.err.println("=== Error en actualización de estadísticas: " + e.getMessage() + " ===");
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            enEjecucion = false;
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    private void actualizarEstadisticasBase(EntityManager em) {
        try {
            System.out.println("Actualizando estadísticas base...");
            Long spam = conexionAgregador.obtenerSpamActual();
            String categoria = conexionAgregador.obtenerCategoriaMaxHechos();

            Estadisticas nuevasEstadisticas = new Estadisticas(
                    UUID.randomUUID(),
                    new Date(),
                    spam,
                    categoria
            );

            System.out.println("Guardando Estadisticas con ID: " + nuevasEstadisticas.getEstadisticas_id());

            Estadisticas estadisticasGuardadas = em.merge(nuevasEstadisticas);
            this.estadisticasActual = estadisticasGuardadas;

            System.out.println("Estadísticas base guardadas - Spam: " + spam + ", Categoría: " + categoria);

        } catch (Exception e) {
            System.err.println("Error actualizando estadísticas base: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private void actualizarEstadisticasCategoria(EntityManager em) {
        try {
            System.out.println("=== INICIANDO ACTUALIZACIÓN CATEGORÍAS ===");
            if (estadisticasActual == null) {
                System.err.println("ERROR: estadisticasActual es null");
                return;
            }

            Map<String, Map<String, Object>> estadisticas = obtenerEstadisticasCategoria();
            System.out.println("Categorías a procesar: " + estadisticas.size());
            System.out.println("ID Estadísticas actual: " + estadisticasActual.getEstadisticas_id());

            for (Map.Entry<String, Map<String, Object>> entry : estadisticas.entrySet()) {
                String categoriaOriginal = entry.getKey();
                Map<String, Object> datos = entry.getValue();

                String categoria = NormalizadorCategorias.normalizar(categoriaOriginal);
                String provincia = (String) datos.get("provincia");
                Integer hora = (Integer) datos.get("hora");

                System.out.println("Procesando categoría: " + categoria + " → Provincia: " + provincia + " (hora: " + hora + ")");

                EstadisticasCategoria estadisticaCategoria = new EstadisticasCategoria(
                        estadisticasActual,
                        categoria,
                        provincia,
                        hora
                );

                em.merge(estadisticaCategoria);
                System.out.println("✅ Categoría guardada: " + categoria + " con estadisticas_id: " + estadisticasActual.getEstadisticas_id());
            }

            System.out.println("=== ACTUALIZACIÓN CATEGORÍAS COMPLETADA ===");

        } catch (Exception e) {
            System.err.println("ERROR en actualizarEstadisticasCategoria: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public Map<String, Map<String, Object>> obtenerEstadisticasCategoria() {
        Map<String, Map<String, Object>> datosCategorias = new HashMap<>();

        try {
            Map<String, Map<String, Object>> coordenadasPorCategoria = conexionAgregador.obtenerCoordenadasPorCategoria();
            Map<String, Integer> horasPorCategoria = conexionAgregador.obtenerHorasPicoPorCategoria();

            for (String categoriaOriginal : coordenadasPorCategoria.keySet()) {
                Map<String, Object> datosCategoria = coordenadasPorCategoria.get(categoriaOriginal);

                @SuppressWarnings("unchecked")
                List<Map<String, Double>> coordenadas = (List<Map<String, Double>>) datosCategoria.get("coordenadas");

                Integer hora = horasPorCategoria.getOrDefault(categoriaOriginal, 0);

                String provincia = "N/A";
                if (coordenadas != null && !coordenadas.isEmpty()) {
                    Map<String, Double> coord = coordenadas.get(0);
                    Double latitud = coord.get("latitud");
                    Double longitud = coord.get("longitud");

                    provincia = llamarAPIProvincia(latitud, longitud);

                    if (provincia.startsWith("Error") || provincia.contains("N/A") || provincia.contains("no encontrada")) {
                        System.out.println("⚠️ API Falló para categoría " + categoriaOriginal + ". Usando Fallback Local.");
                        provincia = obtenerProvinciaPorCoordenadas(latitud, longitud);
                    }
                }

                // Crear mapa de datos para esta categoría
                Map<String, Object> datos = new HashMap<>();
                datos.put("provincia", provincia);
                datos.put("hora", hora);

                datosCategorias.put(categoriaOriginal, datos);
            }

        } catch (Exception e) {
            System.err.println("Error en obtenerEstadisticasCategoria: " + e.getMessage());
        }

        return datosCategorias;
    }

    private void actualizarEstadisticasColeccion(EntityManager em) {
        try {
            System.out.println("=== INICIANDO ACTUALIZACIÓN COLECCIONES ===");
            if (estadisticasActual == null) {
                System.err.println("ERROR: estadisticasActual es null");
                return;
            }

            Map<UUID, Map<String, Object>> estadisticas = obtenerEstadisticasColeccion();
            System.out.println("Colecciones a procesar: " + estadisticas.size());
            System.out.println("ID Estadísticas actual: " + estadisticasActual.getEstadisticas_id());

            for (Map.Entry<UUID, Map<String, Object>> entry : estadisticas.entrySet()) {
                UUID coleccionId = entry.getKey();
                Map<String, Object> datos = entry.getValue();

                String provincia = (String) datos.get("provincia");
                String nombre = (String) datos.get("nombre");

                System.out.println("Procesando colección: " + nombre + " → Provincia: " + provincia);

                EstadisticasColeccion estadisticaColeccion = new EstadisticasColeccion(
                        estadisticasActual,
                        coleccionId,
                        nombre,
                        provincia
                );

                em.merge(estadisticaColeccion); // Usar merge
                System.out.println("✅ Colección guardada: " + nombre + " con estadisticas_id: " + estadisticasActual.getEstadisticas_id());
            }

            System.out.println("=== ACTUALIZACIÓN COLECCIONES COMPLETADA ===");

        } catch (Exception e) {
            System.err.println("ERROR en actualizarEstadisticasColeccion: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public Map<UUID, Map<String, Object>> obtenerEstadisticasColeccion() {
        Map<UUID, Map<String, Object>> datosColecciones = conexionAgregador.obtenerDatosColeccionesConCoordenadas();
        Map<UUID, Map<String, Object>> resultado = new HashMap<>();

        for (Map.Entry<UUID, Map<String, Object>> entry : datosColecciones.entrySet()) {
            UUID coleccionId = entry.getKey();
            Map<String, Object> datos = entry.getValue();
            String nombre = (String) datos.get("nombre");

            @SuppressWarnings("unchecked")
            List<Map<String, Double>> coordenadas = (List<Map<String, Double>>) datos.get("coordenadas");

            String provinciaMasRepetida = obtenerProvinciaMasRepetidaDesdeAPI(coordenadas);

            Map<String, Object> resultadoColeccion = new HashMap<>();
            resultadoColeccion.put("nombre", nombre);
            resultadoColeccion.put("provincia", provinciaMasRepetida);
            resultado.put(coleccionId, resultadoColeccion);
        }

        return resultado;
    }

    private String obtenerProvinciaMasRepetidaDesdeAPI(List<Map<String, Double>> coordenadas) {
        Map<String, Integer> conteoProvincias = new HashMap<>();
        int exitosas = 0;
        int fallidas = 0;

        if (coordenadas == null || coordenadas.isEmpty()) {
            return "Sin coordenadas";
        }

        for (Map<String, Double> coordenada : coordenadas) {
            Double latitud = coordenada.get("latitud");
            Double longitud = coordenada.get("longitud");

            String provincia = llamarAPIProvincia(latitud, longitud);

            if (provincia.startsWith("Error") || provincia.contains("N/A") || provincia.contains("no encontrada")) {
                provincia = obtenerProvinciaPorCoordenadas(latitud, longitud);
                fallidas++;
            } else {
                exitosas++;
            }

            conteoProvincias.put(provincia, conteoProvincias.getOrDefault(provincia, 0) + 1);
        }

        System.out.println("Llamadas a API - Exitosas: " + exitosas + ", Fallidas (usaron fallback): " + fallidas);

        return conteoProvincias.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Desconocida");
    }

    private static final String[] LUGARES = {
            "Uruguay", "Chile", "Bolivia", "Paraguay", "Brasil",
            "CABA", "Buenos Aires", "Catamarca", "Chaco", "Chubut", "Córdoba", "Corrientes",
            "Entre Ríos", "Formosa", "Jujuy", "La Pampa", "La Rioja", "Mendoza", "Misiones",
            "Neuquén", "Río Negro", "Salta", "San Juan", "San Luis", "Santa Cruz", "Santa Fe",
            "Santiago del Estero", "Tierra del Fuego", "Tucumán"
    };

    private static final double[][] CENTROS = {
            {-32.8, -56.0}, {-33.5, -70.9}, {-19.6, -65.8}, {-23.3, -58.2}, {-24.9, -53.4}, // Países
            {-34.6, -58.4}, {-36.7, -60.6}, {-28.5, -65.8}, {-26.4, -60.8}, {-43.8, -68.5}, {-32.1, -63.8}, {-28.8, -57.8}, // Provincias
            {-32.1, -59.2}, {-24.9, -59.9}, {-23.3, -65.8}, {-37.1, -65.4}, {-29.7, -67.2}, {-34.6, -68.6}, {-26.9, -54.7},
            {-38.6, -70.1}, {-40.3, -66.9}, {-24.3, -64.8}, {-30.9, -68.9}, {-33.8, -66.0}, {-48.8, -70.0}, {-30.7, -60.9},
            {-27.8, -63.3}, {-54.2, -67.8}, {-26.9, -65.4}
    };

    private String obtenerProvinciaPorCoordenadas(Double latitud, Double longitud) {
        if (latitud == null || longitud == null) return "Desconocida";

        String mejorLugar = "Desconocida";
        double menorDistancia = Double.MAX_VALUE;

        for (int i = 0; i < LUGARES.length; i++) {
            double distLat = latitud - CENTROS[i][0];
            double distLon = longitud - CENTROS[i][1];
            double distancia = (distLat * distLat) + (distLon * distLon);

            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                mejorLugar = LUGARES[i];
            }
        }

        return (menorDistancia > 50.0) ? "Fuera de Rango" : mejorLugar;
    }

    private String llamarAPIProvincia(Double latitud, Double longitud) {
        try {
            if (latitud == null || longitud == null ||
                    latitud < -90 || latitud > 90 ||
                    longitud < -180 || longitud > 180) {
                return "Coordenadas inválidas";
            }

            String url = "https://apis.datos.gob.ar/georef/api/v2.0/ubicacion?lat=" + latitud +
                    "&lon=" + longitud + "&aplanar=true&campos=estandar";

            String resultado = llamarURL(url);

            if (!resultado.startsWith("Error")) {
                return resultado;
            }

            return "Error API - Endpoint falló";

        } catch (Exception e) {
            System.err.println("Error llamando API de georef: " + e.getMessage());
            return "Error";
        }
    }

    private String llamarURL(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("accept", "application/json")
                    .GET()
                    .timeout(java.time.Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200 ?
                    parsearRespuesta(response.body()) :
                    "Error API - Status: " + response.statusCode();

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String parsearRespuesta(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            // Acceder directamente a ubicacion.provincia_nombre
            JsonNode ubicacion = root.path("ubicacion");
            if (!ubicacion.isMissingNode()) {
                String nombreProvincia = ubicacion.path("provincia_nombre").asText();
                if (!nombreProvincia.isEmpty()) {
                    return nombreProvincia;
                }
            }

            return "N/A - Provincia no encontrada";

        } catch (Exception e) {
            System.err.println("💥 Error parseando respuesta JSON: " + e.getMessage());
            return "Error parsing";
        }
    }
}