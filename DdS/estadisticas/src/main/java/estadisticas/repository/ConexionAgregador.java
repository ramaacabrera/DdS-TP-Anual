package estadisticas.repository;

import estadisticas.utils.BDUtilsAgregador;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConexionAgregador {

    public ConexionAgregador(){}

    public Long obtenerSpamActual() {
        EntityManager em = BDUtilsAgregador.getEntityManager(); // 1. Obtener
        try {
            TypedQuery<Long> query = em.createQuery("SELECT count(*) FROM Solicitud s WHERE s.esSpam = true ", Long.class);
            return query.getSingleResult();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public String obtenerCategoriaMaxHechos() {
        EntityManager em = BDUtilsAgregador.getEntityManager();
        try {
            TypedQuery<String> query = em.createQuery(
                    "SELECT h.categoria FROM Hecho h GROUP BY h.categoria ORDER BY COUNT(h) DESC", String.class);

            query.setMaxResults(1);
            return query.getSingleResult();

        } catch (NoResultException e) {
            return "Sin categorías";
        } catch (Exception e) {
            System.err.println("Error obteniendo categoría máxima: " + e.getMessage());
            return "Error";
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    public Map<String, Map<String, Object>> obtenerUbicacionesPorCategoria() {
        EntityManager em = BDUtilsAgregador.getEntityManager();
        try {
            String query = "SELECT h.categoria, u.descripcion, COUNT(h) FROM Hecho h JOIN h.ubicacion u " +
                    "WHERE h.categoria IS NOT NULL AND u.descripcion IS NOT NULL " +
                    "GROUP BY h.categoria, u.descripcion " +
                    "ORDER BY h.categoria, COUNT(h) DESC";

            List<Object[]> resultados = em.createQuery(query, Object[].class).getResultList();

            Map<String, Map<String, Object>> datosCategorias = new HashMap<>();

            for (Object[] fila : resultados) {
                String categoria = (String) fila[0];
                String descripcion = (String) fila[1];
                Long count = (Long) fila[2];

                if (descripcion.trim().isEmpty()) continue;

                if (!datosCategorias.containsKey(categoria)) {
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("descripciones", new ArrayList<String>());
                    datos.put("count", 0L);
                    datosCategorias.put(categoria, datos);
                }

                @SuppressWarnings("unchecked")
                List<String> descripciones = (List<String>) datosCategorias.get(categoria).get("descripciones");
                descripciones.add(descripcion);

                Long currentCount = (Long) datosCategorias.get(categoria).get("count");
                if (count > currentCount) {
                    datosCategorias.get(categoria).put("count", count);
                }
            }
            return datosCategorias;

        } catch (Exception e) {
            System.err.println("Error en obtenerUbicacionesPorCategoria: " + e.getMessage());
            return new HashMap<>();
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    public Map<String, Integer> obtenerHorasPicoPorCategoria() {
        EntityManager em = BDUtilsAgregador.getEntityManager();
        try {
            String query = "SELECT h.categoria, FUNCTION('HOUR', h.fechaDeAcontecimiento), COUNT(h) " +
                    "FROM Hecho h " +
                    "WHERE h.categoria IS NOT NULL AND h.fechaDeAcontecimiento IS NOT NULL " +
                    "GROUP BY h.categoria, FUNCTION('HOUR', h.fechaDeAcontecimiento) " +
                    "ORDER BY h.categoria, COUNT(h) DESC";

            List<Object[]> resultados = em.createQuery(query, Object[].class).getResultList();

            return this.obtenerMaximoPor(resultados, fila -> (String) fila[0], fila -> ((Number) fila[1]).intValue());

        } catch (Exception e) {
            System.err.println("Error en obtenerHorasPicoPorCategoria: " + e.getMessage());
            return new HashMap<>();
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    public Map<UUID, Map<String, Object>> obtenerDatosColeccionesConUbicacion() {
        EntityManager em = BDUtilsAgregador.getEntityManager();
        try {
            String query = "SELECT c.handle, c.titulo, u.descripcion " +
                    "FROM Coleccion c JOIN c.hechos h JOIN h.ubicacion u " +
                    "WHERE u.descripcion IS NOT NULL AND u.descripcion <> ''";

            List<Object[]> resultados = em.createQuery(query, Object[].class).getResultList();

            Map<UUID, Map<String, Object>> datosColecciones = new HashMap<>();

            for (Object[] fila : resultados) {
                UUID coleccionId = (UUID) fila[0];
                String titulo = (String) fila[1];
                String descripcion = (String) fila[2];

                if (descripcion == null || descripcion.trim().isEmpty()) {
                    continue;
                }

                if (!datosColecciones.containsKey(coleccionId)) {
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("nombre", titulo);
                    datos.put("descripciones", new ArrayList<String>());
                    datosColecciones.put(coleccionId, datos);
                }

                @SuppressWarnings("unchecked")
                List<String> descripciones = (List<String>) datosColecciones.get(coleccionId).get("descripciones");
                descripciones.add(descripcion);
            }

            return datosColecciones;

        } catch (Exception e) {
            System.err.println("Error en obtenerDatosColeccionesConUbicacion: " + e.getMessage());
            return new HashMap<>();
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    public <C, V> Map<C, V> obtenerMaximoPor(List<Object[]> resultados,
                                             Function<Object[], C> transformadorClave,
                                             Function<Object[], V> transformadorValor) {
        Map<C, V> resultado = new HashMap<>();
        if (resultados == null || resultados.isEmpty()) return resultado;

        Map<C, Object[]> maxPorClave = resultados.stream()
                .filter(fila -> fila != null && fila.length >= 3 && fila[2] != null)
                .collect(Collectors.toMap(
                        transformadorClave,
                        fila -> fila,
                        (fila1, fila2) -> {
                            Long count1 = ((Number) fila1[2]).longValue();
                            Long count2 = ((Number) fila2[2]).longValue();
                            return count1 >= count2 ? fila1 : fila2;
                        }
                ));

        maxPorClave.forEach((clave, fila) -> {
            try {
                resultado.put(clave, transformadorValor.apply(fila));
            } catch (Exception e) {
                System.err.println("Error transformando valor para clave: " + clave);
            }
        });

        return resultado;
    }
}