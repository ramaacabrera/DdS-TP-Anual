package estadisticas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import estadisticas.Dominio.*;
import estadisticas.agregador.ConexionAgregador;
import estadisticas.agregador.EstadisticasCategoriaRepositorio;
import estadisticas.agregador.EstadisticasColeccionRepositorio;
import estadisticas.agregador.EstadisticasRepositorio;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GeneradorEstadisticas {
    private final ConexionAgregador conexionAgregador;
    private final EstadisticasRepositorio estadisticasRepositorio;
    private final EstadisticasCategoriaRepositorio estadisticasCategoriaRepositorio;
    private final EstadisticasColeccionRepositorio estadisticasColeccionRepositorio;
    private Estadisticas estadisticasActual = null;
    private final ScheduledExecutorService scheduler;
    private volatile boolean enEjecucion = false;

    public GeneradorEstadisticas(ConexionAgregador conexionAgregador,
                                 EstadisticasRepositorio estadisticasRepositorioNuevo,
                                 EstadisticasCategoriaRepositorio estadisticasCategoriaRepositorioNuevo,
                                 EstadisticasColeccionRepositorio estadisticasColeccionRepositorioNuevo){
        this.conexionAgregador = conexionAgregador;
        this.estadisticasRepositorio = estadisticasRepositorioNuevo;
        this.estadisticasCategoriaRepositorio = estadisticasCategoriaRepositorioNuevo;
        this.estadisticasColeccionRepositorio = estadisticasColeccionRepositorioNuevo;

        this.scheduler = Executors.newScheduledThreadPool(1);
        this.iniciarScheduling();
    }

    public void iniciarScheduling() {
        System.out.println("Iniciando scheduler de estadísticas...");
        scheduler.scheduleAtFixedRate(this::actualizarEstadisticas, 0, 360, TimeUnit.SECONDS);
        System.out.println("Scheduler iniciado - ejecución cada 360 segundos");
    }

    public void detenerScheduling() {
        System.out.println("Deteniendo scheduler de estadísticas...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void actualizarEstadisticas() {
        if (enEjecucion) {
            System.out.println("Actualización ya en curso, omitiendo...");
            return;
        }

        enEjecucion = true;
        try {
            System.out.println("=== Iniciando actualización de estadísticas: " + new Date() + " ===");
            this.actualizarEstadisticasBase();
            this.actualizarEstadisticasCategoria();
            this.actualizarEstadisticasColeccion();
            System.out.println("=== Actualización completada exitosamente ===");
        } catch (Exception e) {
            System.err.println("=== Error en actualización de estadísticas: " + e.getMessage() + " ===");
            e.printStackTrace();
        } finally {
            enEjecucion = false;
        }
    }

    private void actualizarEstadisticasBase() {
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
            estadisticasRepositorio.guardar(nuevasEstadisticas);
            this.estadisticasActual = nuevasEstadisticas;

            System.out.println("Estadísticas base guardadas - Spam: " + spam + ", Categoría: " + categoria);

        } catch (Exception e) {
            System.err.println("Error actualizando estadísticas base: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private void actualizarEstadisticasCategoria() {
        try {
            System.out.println("=== INICIANDO ACTUALIZACIÓN CATEGORÍAS ===");
            if (estadisticasActual == null) {
                System.err.println("ERROR: estadisticasActual es null");
                return;
            }

            Map<String, String> coordenadasPorCategoria = conexionAgregador.obtenerProvinciasPorCategoria();
            Map<String, Integer> horasPorCategoria = conexionAgregador.obtenerHorasPicoPorCategoria();

            System.out.println("Categorías a procesar: " + coordenadasPorCategoria.size());

            for (String categoria : coordenadasPorCategoria.keySet()) {
                String coordenadas = coordenadasPorCategoria.get(categoria);
                Integer hora = horasPorCategoria.getOrDefault(categoria, 0);

                String provincia = obtenerProvinciaDesdeCoordenadas(coordenadas);
                System.out.println("Procesando categoría: " + categoria + " → " + provincia + " (hora: " + hora + ")");

                EstadisticasCategoriaId id = new EstadisticasCategoriaId(
                        estadisticasActual.getEstadisticas_id(),
                        categoria
                );

                EstadisticasCategoria estadisticaCategoria = new EstadisticasCategoria(id, provincia, hora);
                estadisticasCategoriaRepositorio.guardar(estadisticaCategoria);
                System.out.println("✅ Categoría guardada: " + categoria);
            }

            System.out.println("=== ACTUALIZACIÓN CATEGORÍAS COMPLETADA ===");

        } catch (Exception e) {
            System.err.println("ERROR en actualizarEstadisticasCategoria: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String obtenerProvinciaDesdeCoordenadas(String coordenadas) {
        try {
            if (coordenadas == null || coordenadas.equals("Coordenadas inválidas")) {
                return "N/A";
            }

            String[] partes = coordenadas.split(",");
            if (partes.length != 2) {
                return "Formato inválido";
            }

            Double latitud = Double.parseDouble(partes[0]);
            Double longitud = Double.parseDouble(partes[1]);

            return llamarAPIProvincia(latitud, longitud);

        } catch (Exception e) {
            System.err.println("Error parseando coordenadas: " + coordenadas + " - " + e.getMessage());
            return "Error";
        }
    }

    private void actualizarEstadisticasColeccion(){
        try {
            System.out.println("=== INICIANDO ACTUALIZACIÓN COLECCIONES ===");
            if (estadisticasActual == null) {
                System.err.println("ERROR: estadisticasActual es null");
                return;
            }

            Map<UUID, Map<String, Object>> estadisticas = obtenerEstadisticasColeccion();
            System.out.println("Colecciones a procesar: " + estadisticas.size());

            for (Map.Entry<UUID, Map<String, Object>> entry : estadisticas.entrySet()) {
                UUID coleccionId = entry.getKey();
                Map<String, Object> datos = entry.getValue();

                String provincia = (String) datos.get("provincia");
                String nombre = (String) datos.get("nombre");

                System.out.println("Procesando colección: " + nombre + " → Provincia: " + provincia);

                EstadisticasColeccionId id = new EstadisticasColeccionId(
                        estadisticasActual.getEstadisticas_id(),
                        coleccionId,
                        nombre
                );

                EstadisticasColeccion estadisticaColeccion = new EstadisticasColeccion();
                estadisticaColeccion.setId(id);
                estadisticaColeccion.setEstadisticasColeccion_provincia(provincia);

                estadisticasColeccionRepositorio.guardar(estadisticaColeccion);
                System.out.println("✅ Colección guardada: " + nombre);
            }

            System.out.println("=== ACTUALIZACIÓN COLECCIONES COMPLETADA ===");

        } catch (Exception e) {
            System.err.println("ERROR en actualizarEstadisticasColeccion: " + e.getMessage());
            e.printStackTrace();
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
            String url = "https://apis.datos.gob.ar/georef/api/ubicacion?lat=" + latitud +
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
                    .header("User-Agent", "Metamapa-Stats/1.0")
                    .header("accept", "application/json")
                    .GET()
                    .timeout(java.time.Duration.ofSeconds(15))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Response status: " + response.statusCode());

            if (response.statusCode() == 200) {
                return parsearRespuesta(response.body());
            } else if (response.statusCode() == 429) {
                System.err.println("Límite de tasa excedido, esperando...");
                Thread.sleep(2000); // Esperar 2 segundos antes de reintentar
                return "Error API - Límite excedido";
            } else {
                return "Error API - Status: " + response.statusCode();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Interrumpido";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String parsearRespuesta(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            // DEBUG: Mostrar estructura de la respuesta
            System.out.println("Estructura de respuesta: " + root.getNodeType() + " con " + root.size() + " campos");

            // Estrategia 1: Buscar en ubicaciones (estructura más común)
            JsonNode ubicaciones = root.path("ubicaciones");
            if (ubicaciones.isArray() && ubicaciones.size() > 0) {
                JsonNode primeraUbicacion = ubicaciones.get(0);
                JsonNode provincia = primeraUbicacion.path("provincia");
                if (provincia.has("nombre")) {
                    String nombreProvincia = provincia.get("nombre").asText();
                    if (!nombreProvincia.isEmpty()) {
                        System.out.println("✅ Provincia encontrada en ubicaciones[0].provincia.nombre: " + nombreProvincia);
                        return nombreProvincia;
                    }
                }
            }

            // Estrategia 2: Buscar directamente en provincia
            JsonNode provincia = root.path("provincia");
            if (provincia.has("nombre")) {
                String nombreProvincia = provincia.get("nombre").asText();
                if (!nombreProvincia.isEmpty()) {
                    System.out.println("✅ Provincia encontrada en provincia.nombre: " + nombreProvincia);
                    return nombreProvincia;
                }
            }

            // Estrategia 3: Buscar en resultados (estructura alternativa)
            JsonNode resultados = root.path("resultados");
            if (resultados.isArray() && resultados.size() > 0) {
                JsonNode primerResultado = resultados.get(0);
                JsonNode provinciaResultado = primerResultado.path("provincia");
                if (provinciaResultado.has("nombre")) {
                    String nombreProvincia = provinciaResultado.get("nombre").asText();
                    if (!nombreProvincia.isEmpty()) {
                        System.out.println("✅ Provincia encontrada en resultados[0].provincia.nombre: " + nombreProvincia);
                        return nombreProvincia;
                    }
                }
            }

            // Estrategia 4: Búsqueda recursiva en todo el JSON
            String provinciaEncontrada = buscarProvinciaRecursivamente(root);
            if (provinciaEncontrada != null) {
                System.out.println("✅ Provincia encontrada en búsqueda recursiva: " + provinciaEncontrada);
                return provinciaEncontrada;
            }

            System.out.println("❌ No se pudo encontrar la provincia en la respuesta JSON");
            System.out.println("Estructura completa: " + root.toPrettyString());
            return "N/A - Estructura no reconocida";

        } catch (Exception e) {
            System.err.println("💥 Error parseando respuesta JSON: " + e.getMessage());
            return "Error parsing";
        }
    }

    private String buscarProvinciaRecursivamente(JsonNode node) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> campos = node.fields();
            while (campos.hasNext()) {
                Map.Entry<String, JsonNode> campo = campos.next();
                String nombreCampo = campo.getKey().toLowerCase();
                JsonNode valor = campo.getValue();

                // Si el campo contiene "provincia" y es texto, devolverlo
                if (nombreCampo.contains("provincia") && valor.isTextual()) {
                    String provincia = valor.asText();
                    if (!provincia.isEmpty() && !provincia.equals("null")) {
                        return provincia;
                    }
                }

                // Si el campo es "nombre" y el padre es "provincia"
                if (nombreCampo.equals("nombre") && campo.getKey().equals("nombre")) {
                    JsonNode padre = node;
                    if (padre.has("provincia") || node.toString().toLowerCase().contains("provincia")) {
                        String nombre = valor.asText();
                        if (!nombre.isEmpty() && !nombre.equals("null")) {
                            return nombre;
                        }
                    }
                }

                // Buscar recursivamente
                if (valor.isObject() || valor.isArray()) {
                    String resultado = buscarProvinciaRecursivamente(valor);
                    if (resultado != null) {
                        return resultado;
                    }
                }
            }
        } else if (node.isArray()) {
            for (JsonNode elemento : node) {
                String resultado = buscarProvinciaRecursivamente(elemento);
                if (resultado != null) {
                    return resultado;
                }
            }
        }
        return null;
    }
}