package estadisticas.service;

import estadisticas.domain.Estadisticas;
import estadisticas.domain.EstadisticasCategoria;
import estadisticas.domain.EstadisticasColeccion;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import estadisticas.repository.ConexionAgregador;
import estadisticas.repository.BDUtilsEstadisticas;
import utils.PaqueteNormalizador.NormalizadorCategorias;

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

            // USAR MERGE() en lugar de persist() para entidades con ID pre-asignado
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
                        estadisticasActual,  // Relación directa - entidad managed
                        categoria,           // Campo directo
                        provincia,           // Campo directo
                        hora                 // Campo directo
                );

                em.merge(estadisticaCategoria); // Usar merge
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
            // Obtener coordenadas por categoría
            Map<String, Map<String, Object>> coordenadasPorCategoria = conexionAgregador.obtenerCoordenadasPorCategoria();
            Map<String, Integer> horasPorCategoria = conexionAgregador.obtenerHorasPicoPorCategoria();

            for (String categoriaOriginal : coordenadasPorCategoria.keySet()) {
                Map<String, Object> datosCategoria = coordenadasPorCategoria.get(categoriaOriginal);

                @SuppressWarnings("unchecked")
                List<Map<String, Double>> coordenadas = (List<Map<String, Double>>) datosCategoria.get("coordenadas");

                Integer hora = horasPorCategoria.getOrDefault(categoriaOriginal, 0);

                // Determinar provincia usando la coordenada más frecuente (primera de la lista)
                String provincia = "N/A";
                if (coordenadas != null && !coordenadas.isEmpty()) {
                    Map<String, Double> coord = coordenadas.get(0); // La más frecuente por ORDER BY COUNT DESC
                    Double latitud = coord.get("latitud");
                    Double longitud = coord.get("longitud");

                    provincia = llamarAPIProvincia(latitud, longitud);
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
                        estadisticasActual,  // Relación directa - entidad managed
                        coleccionId,         // Campo directo
                        nombre,              // Campo directo
                        provincia            // Campo directo
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
            System.out.println("Coordenadas: " + latitud + ", " + longitud + " → " + provincia);

            if (provincia.startsWith("Error") || provincia.equals("N/A")) {
                fallidas++;
                provincia = obtenerProvinciaPorCoordenadas(latitud, longitud);
            } else {
                exitosas++;
            }

            conteoProvincias.put(provincia, conteoProvincias.getOrDefault(provincia, 0) + 1);
        }

        System.out.println("Llamadas a API - Exitosas: " + exitosas + ", Fallidas: " + fallidas);

        return conteoProvincias.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Buenos Aires");
    }

    private String obtenerProvinciaPorCoordenadas(Double latitud, Double longitud) {
        if (latitud == null || longitud == null) return "Desconocida";

        if (latitud < -40) return "Tierra del Fuego";
        if (latitud < -45) return "Santa Cruz";
        if (latitud < -50) return "Chubut";
        if (longitud < -65 && latitud > -30) return "Mendoza";
        if (longitud < -60 && latitud > -35) return "Córdoba";
        if (longitud < -58 && latitud > -35) return "Santa Fe";
        if (longitud < -57 && latitud > -35) return "Entre Ríos";
        if (latitud > -35 && latitud < -34 && longitud > -59 && longitud < -58) {
            return "Buenos Aires";
        }

        return "Buenos Aires";
    }

    private String llamarAPIProvincia(Double latitud, Double longitud) {
        try {
            if (latitud == null || longitud == null ||
                    latitud < -90 || latitud > 90 ||
                    longitud < -180 || longitud > 180) {
                return "Coordenadas inválidas";
            }

            // Usar el endpoint principal con parámetros optimizados
            String url = "https://apis.datos.gob.ar/georef/api/v2.0/ubicacion?lat=" + latitud +
                    "&lon=" + longitud + "&aplanar=true&campos=estandar";

            System.out.println("Consultando API: " + url);
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
                    System.out.println("✅ Provincia encontrada: " + nombreProvincia);
                    return nombreProvincia;
                }
            }

            System.out.println("❌ No se pudo encontrar la provincia en la respuesta JSON");
            return "N/A - Provincia no encontrada";

        } catch (Exception e) {
            System.err.println("💥 Error parseando respuesta JSON: " + e.getMessage());
            return "Error parsing";
        }
    }
}