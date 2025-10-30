package estadisticas.agregador;

import utils.BDUtils;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConexionAgregador {

    /*
    private final EntityManager em;

    public ConexionAgregador(EntityManager emNuevo) {
        this.em = emNuevo;
    }
     */
    public ConexionAgregador(){}

    public Long obtenerSpamActual() {
        TypedQuery<Long> query = BDUtils.getEntityManager().createQuery("SELECT count(*) FROM Solicitud s WHERE s.esSpam = true ", Long.class);
        return query.getSingleResult();
    }

    public String obtenerCategoriaMaxHechos() {
        try {
            TypedQuery<String> query = BDUtils.getEntityManager().createQuery(
                    "SELECT h.categoria FROM Hecho h GROUP BY h.categoria ORDER BY COUNT(h) DESC", String.class);

            query.setMaxResults(1);
            return query.getSingleResult();

        } catch (NoResultException e) {
            return "Sin categorías";
        } catch (Exception e) {
            System.err.println("Error obteniendo categoría máxima: " + e.getMessage());
            return "Error";
        }
    }

    public Map<String, String> obtenerProvinciasPorCategoria() {
        try {
            String query = "SELECT h.categoria, u.latitud, u.longitud, COUNT(h) FROM Hecho h JOIN h.ubicacion u " +
                    "WHERE h.categoria IS NOT NULL AND u.latitud IS NOT NULL AND u.longitud IS NOT NULL " +
                    "GROUP BY h.categoria, u.latitud, u.longitud " +
                    "ORDER BY h.categoria, COUNT(h) DESC";

            List<Object[]> resultados = BDUtils.getEntityManager().createQuery(query, Object[].class).getResultList();

            return this.obtenerMaximoPor(resultados,
                    fila -> (String) fila[0],
                    fila -> {
                        Double latitud = (Double) fila[1];
                        Double longitud = (Double) fila[2];
                        return latitud + "," + longitud; // Formato: "latitud,longitud"
                    });

        } catch (Exception e) {
            System.err.println("Error en obtenerProvinciasPorCategoria: " + e.getMessage());
            return new HashMap<>();
        }
    }

    public Map<String, Integer> obtenerHorasPicoPorCategoria() {
        try {
            String query = "SELECT h.categoria, FUNCTION('HOUR', h.fechaDeAcontecimiento), COUNT(h) " +
                    "FROM Hecho h " +
                    "WHERE h.categoria IS NOT NULL AND h.fechaDeAcontecimiento IS NOT NULL " +
                    "GROUP BY h.categoria, FUNCTION('HOUR', h.fechaDeAcontecimiento) " +
                    "ORDER BY h.categoria, COUNT(h) DESC";

            List<Object[]> resultados = BDUtils.getEntityManager().createQuery(query, Object[].class).getResultList();

            return this.obtenerMaximoPor(resultados, fila -> (String) fila[0], fila -> ((Number) fila[1]).intValue());

        } catch (Exception e) {
            System.err.println("Error en obtenerHorasPicoPorCategoria: " + e.getMessage());
            return new HashMap<>();
        }
    }

    public Map<UUID, Map<String, Object>> obtenerDatosColeccionesConCoordenadas() {
        try {
            String query = "SELECT c.handle, c.titulo, h.id, u.latitud, u.longitud " +
                    "FROM Coleccion c JOIN c.hechos h JOIN h.ubicacion u " +
                    "WHERE u.latitud IS NOT NULL AND u.longitud IS NOT NULL"; // ← FILTRO IMPORTANTE

            List<Object[]> resultados = BDUtils.getEntityManager()
                    .createQuery(query, Object[].class)
                    .getResultList();

            Map<UUID, Map<String, Object>> datosColecciones = new HashMap<>();

            for (Object[] fila : resultados) {
                UUID coleccionId = (UUID) fila[0];
                String titulo = (String) fila[1];
                // El tipo de h.id depende de tu entidad - podría ser Long, UUID, etc.
                Object hechoId = fila[2];
                Double latitud = (Double) fila[3];
                Double longitud = (Double) fila[4];

                // Validar coordenadas
                if (latitud == null || longitud == null ||
                        latitud < -90 || latitud > 90 ||
                        longitud < -180 || longitud > 180) {
                    continue; // Saltar coordenadas inválidas
                }

                // Si es la primera vez que vemos esta colección, inicializar sus datos
                if (!datosColecciones.containsKey(coleccionId)) {
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("nombre", titulo);
                    datos.put("coordenadas", new ArrayList<Map<String, Double>>());
                    datosColecciones.put(coleccionId, datos);
                }

                // Agregar las coordenadas del hecho actual
                Map<String, Double> coordenada = new HashMap<>();
                coordenada.put("latitud", latitud);
                coordenada.put("longitud", longitud);

                @SuppressWarnings("unchecked")
                List<Map<String, Double>> coordenadas =
                        (List<Map<String, Double>>) datosColecciones.get(coleccionId).get("coordenadas");
                coordenadas.add(coordenada);
            }

            // Filtrar colecciones sin coordenadas válidas
            return datosColecciones.entrySet().stream()
                    .filter(entry -> !((List<?>) entry.getValue().get("coordenadas")).isEmpty())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        } catch (Exception e) {
            System.err.println("Error en obtenerDatosColeccionesConCoordenadas: " + e.getMessage());
            return new HashMap<>();
        }
    }

    public <C, V> Map<C, V> obtenerMaximoPor(List<Object[]> resultados,
                                             Function<Object[], C> transformadorClave,
                                             Function<Object[], V> transformadorValor) {

        Map<C, V> resultado = new HashMap<>();
        if (resultados == null || resultados.isEmpty()) {
            return resultado;
        }

        // Usar grouping para mejor performance con muchos datos
        Map<C, Object[]> maxPorClave = resultados.stream()
                .filter(fila -> fila != null && fila.length >= 3 && fila[2] != null)
                .collect(Collectors.toMap(
                        fila -> transformadorClave.apply(fila),
                        fila -> fila,
                        (fila1, fila2) -> {
                            Long count1 = ((Number) fila1[2]).longValue();
                            Long count2 = ((Number) fila2[2]).longValue();
                            return count1 >= count2 ? fila1 : fila2;
                        }
                ));

        // Construir resultado
        maxPorClave.forEach((clave, fila) -> {
            try {
                V valor = transformadorValor.apply(fila);
                resultado.put(clave, valor);
            } catch (Exception e) {
                System.err.println("Error transformando valor para clave: " + clave);
            }
        });

        return resultado;
    }

}
