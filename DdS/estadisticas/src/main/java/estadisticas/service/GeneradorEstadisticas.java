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
import java.util.*;

public class GeneradorEstadisticas {
    private final ConexionAgregador conexionAgregador;
    private Estadisticas estadisticasActual = null;

    public GeneradorEstadisticas(ConexionAgregador conexionAgregador){
        this.conexionAgregador = conexionAgregador;
    }

    public void actualizarEstadisticas() {
        EntityManager em = BDUtilsEstadisticas.getEntityManager();
        try {
            System.out.println("=== Iniciando actualización de estadísticas: " + new Date() + " ===");
            em.getTransaction().begin();

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

            Estadisticas estadisticasGuardadas = em.merge(nuevasEstadisticas);
            this.estadisticasActual = estadisticasGuardadas;
            System.out.println("Estadísticas base guardadas - Spam: " + spam + ", Categoría: " + categoria);

        } catch (Exception e) {
            System.err.println("Error actualizando estadísticas base: " + e.getMessage());
            throw e;
        }
    }

    private void actualizarEstadisticasCategoria(EntityManager em) {
        try {
            System.out.println("=== INICIANDO ACTUALIZACIÓN CATEGORÍAS ===");
            Map<String, Map<String, Object>> estadisticas = obtenerEstadisticasCategoria();

            for (Map.Entry<String, Map<String, Object>> entry : estadisticas.entrySet()) {
                String categoriaOriginal = entry.getKey();
                Map<String, Object> datos = entry.getValue();

                String categoria = NormalizadorCategorias.normalizar(categoriaOriginal);
                String provincia = (String) datos.get("provincia");
                Integer hora = (Integer) datos.get("hora");

                System.out.println("Procesando categoría: " + categoria + " → Provincia: " + provincia);

                EstadisticasCategoria estadisticaCategoria = new EstadisticasCategoria(
                        estadisticasActual,
                        categoria,
                        provincia,
                        hora
                );
                em.merge(estadisticaCategoria);
            }
            System.out.println("=== ACTUALIZACIÓN CATEGORÍAS COMPLETADA ===");
        } catch (Exception e) {
            System.err.println("ERROR en actualizarEstadisticasCategoria: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Map<String, Map<String, Object>> obtenerEstadisticasCategoria() {
        Map<String, Map<String, Object>> datosCategorias = new HashMap<>();
        try {
            Map<String, Map<String, Object>> ubicacionesPorCategoria = conexionAgregador.obtenerUbicacionesPorCategoria();
            Map<String, Integer> horasPorCategoria = conexionAgregador.obtenerHorasPicoPorCategoria();

            for (String categoriaOriginal : ubicacionesPorCategoria.keySet()) {
                Map<String, Object> datosUbicacion = ubicacionesPorCategoria.get(categoriaOriginal);

                @SuppressWarnings("unchecked")
                List<String> descripciones = (List<String>) datosUbicacion.get("descripciones");
                Integer hora = horasPorCategoria.getOrDefault(categoriaOriginal, 0);

                String provincia = obtenerProvinciaMasRepetida(descripciones);

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
            Map<UUID, Map<String, Object>> estadisticas = obtenerEstadisticasColeccion();

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
                em.merge(estadisticaColeccion);
            }
            System.out.println("=== ACTUALIZACIÓN COLECCIONES COMPLETADA ===");
        } catch (Exception e) {
            System.err.println("ERROR en actualizarEstadisticasColeccion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Map<UUID, Map<String, Object>> obtenerEstadisticasColeccion() {
        Map<UUID, Map<String, Object>> datosColecciones = conexionAgregador.obtenerDatosColeccionesConUbicacion();
        Map<UUID, Map<String, Object>> resultado = new HashMap<>();

        for (Map.Entry<UUID, Map<String, Object>> entry : datosColecciones.entrySet()) {
            UUID coleccionId = entry.getKey();
            Map<String, Object> datos = entry.getValue();
            String nombre = (String) datos.get("nombre");

            @SuppressWarnings("unchecked")
            List<String> descripciones = (List<String>) datos.get("descripciones");

            String provinciaMasRepetida = obtenerProvinciaMasRepetida(descripciones);

            Map<String, Object> resultadoColeccion = new HashMap<>();
            resultadoColeccion.put("nombre", nombre);
            resultadoColeccion.put("provincia", provinciaMasRepetida);
            resultado.put(coleccionId, resultadoColeccion);
        }
        return resultado;
    }


    private String obtenerProvinciaMasRepetida(List<String> descripciones) {
        if (descripciones == null || descripciones.isEmpty()) {
            return "N/A";
        }

        Map<String, Integer> conteoProvincias = new HashMap<>();

        for (String descripcion : descripciones) {
            String provincia = extraerProvincia(descripcion);
            conteoProvincias.put(provincia, conteoProvincias.getOrDefault(provincia, 0) + 1);
        }

        return conteoProvincias.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Desconocida");
    }

    private String extraerProvincia(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) return "Desconocida";

        String[] partes = descripcion.split(",");

        if (partes.length >= 2) {
            return partes[1].trim();
        } else {
            return descripcion.trim();
        }
    }
}