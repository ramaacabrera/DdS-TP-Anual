package estadisticas.repository;

import estadisticas.utils.BDUtilsAgregador;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConexionAgregador {

    public ConexionAgregador(){}

    public Long obtenerSpamActual() {
        EntityManager em = BDUtilsAgregador.getEntityManager();
        try {
            Query query = em.createNativeQuery("SELECT count(*) FROM Solicitud WHERE esSpam = true");

            Object resultado = query.getSingleResult();

            if (resultado != null) {
                return ((Number) resultado).longValue();
            }
            return 0L;

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public String obtenerCategoriaMaxHechos() {
        EntityManager em = BDUtilsAgregador.getEntityManager();
        try {
            Query query = em.createNativeQuery("SELECT categoria FROM Hecho GROUP BY categoria ORDER BY COUNT(*) DESC");

            query.setMaxResults(1);
            Object resultado = query.getSingleResult();

            return resultado != null ? resultado.toString() : "Sin categorías";

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
            String sql = "SELECT h.categoria, u.descripcion, COUNT(*) " +
                    "FROM Hecho h " +
                    "JOIN Ubicacion u ON h.id_ubicacion = u.id_ubicacion " +
                    "WHERE h.categoria IS NOT NULL AND u.descripcion IS NOT NULL " +
                    "GROUP BY h.categoria, u.descripcion " +
                    "ORDER BY h.categoria, COUNT(*) DESC";

            List<Object[]> resultados = em.createNativeQuery(sql).getResultList();

            Map<String, Map<String, Object>> datosCategorias = new HashMap<>();

            for (Object[] fila : resultados) {
                String categoria = (String) fila[0];
                String descripcion = (String) fila[1];
                // SQL Nativo devuelve BigInteger para COUNT, casteamos seguro:
                Long count = ((Number) fila[2]).longValue();

                if (descripcion == null || descripcion.trim().isEmpty()) continue;

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
            e.printStackTrace();
            return new HashMap<>();
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    public Map<String, Integer> obtenerHorasPicoPorCategoria() {
        EntityManager em = BDUtilsAgregador.getEntityManager();
        try {
            String sql = "SELECT h.categoria, HOUR(h.fechaDeAcontecimiento), COUNT(*) " +
                    "FROM Hecho h " +
                    "WHERE h.categoria IS NOT NULL AND h.fechaDeAcontecimiento IS NOT NULL " +
                    "GROUP BY h.categoria, HOUR(h.fechaDeAcontecimiento) " +
                    "ORDER BY h.categoria, COUNT(*) DESC";

            List<Object[]> resultados = em.createNativeQuery(sql).getResultList();

            return this.obtenerMaximoPor(resultados,
                    fila -> (String) fila[0],
                    fila -> ((Number) fila[1]).intValue()
            );

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
            String sql = "SELECT c.handle, c.titulo, u.descripcion " +
                    "FROM Coleccion c " +
                    "JOIN ColeccionXHecho cxh on c.handle = cxh.handle " +
                    "JOIN Hecho h ON cxh.hecho_id = h.hecho_id " +
                    "JOIN Ubicacion u ON h.id_ubicacion = u.id_ubicacion " +
                    "WHERE u.descripcion IS NOT NULL AND u.descripcion <> ''";

            List<Object[]> resultados = em.createNativeQuery(sql).getResultList();

            Map<UUID, Map<String, Object>> datosColecciones = new HashMap<>();

            for (Object[] fila : resultados) {
                String handle = (String) fila[0];
                UUID coleccionId = UUID.fromString(handle);
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