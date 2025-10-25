package estadisticas.agregador;

import utils.BDUtils;

import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.function.Function;

public class ConexionAgregador {

    /*
    private final EntityManager em;

    public ConexionAgregador(EntityManager emNuevo) {
        this.em = emNuevo;
    }
     */
    public ConexionAgregador(){}

    public int obtenerSpamActual() {
        TypedQuery<Integer> query = BDUtils.getEntityManager().createQuery("SELECT count(*) FROM Solicitud s WHERE s.esSpam = true ", Integer.class);
        return query.getSingleResult();
    }

    public String obtenerCategoriaMaxHechos() {
        TypedQuery<String> query = BDUtils.getEntityManager().createQuery("SELECT h.categoria FROM Hecho h group by h.categoria order by count(h.categoria) desc limit 1", String.class);
        return query.getSingleResult();
    }

    public Map<String, String> obtenerProvinciasPorCategoria() {
        String query = "SELECT h.categoria, u.descripcion, COUNT(h) FROM Hecho h JOIN h.ubicacion u GROUP BY h.categoria, u.descripcion";
        List<Object[]> resultados = BDUtils.getEntityManager().createQuery(query, Object[].class).getResultList();

        return this.obtenerMaximoPor(resultados, fila -> (String) fila[0], fila -> (String) fila[1]);
    }

    public Map<String, Integer> obtenerHorasPicoPorCategoria() {
        String query = "SELECT h.categoria, hour(h.fechaDeAcontecimiento), count(h) FROM Hecho h GROUP BY h.categoria, hour(h.fechaDeAcontecimiento)";
        List<Object[]> resultados = BDUtils.getEntityManager().createQuery(query, Object[].class).getResultList();

        return this.obtenerMaximoPor(resultados, fila -> (String) fila[0], fila -> (Integer) fila[1]);
    }

    public Map<UUID, String> obtenerProvinciaPorColeccion() {
        String query = "SELECT c.handle, u.descripcion, COUNT(h) FROM Coleccion c JOIN c.hechos h JOIN h.ubicacion u GROUP BY c.handle, u.descripcion";
        List<Object[]> resultados = BDUtils.getEntityManager().createQuery(query, Object[].class).getResultList();

        return this.obtenerMaximoPor(resultados, fila -> (UUID) fila[0], fila -> (String) fila[1]);
    }

    public <C, V> Map<C, V> obtenerMaximoPor(List<Object[]> resultados, Function<Object[], C> transformadorClave, Function<Object[], V> transformadorValor) {

        Map<C, V> resultado = new HashMap<>();
        Map<C, Long> maximos = new HashMap<>();

        for(Object[] fila : resultados){
            C clave = transformadorClave.apply(fila);
            V valor = transformadorValor.apply(fila);
            Long cantidad = (Long) fila[2];

            if(!maximos.containsKey(clave) || cantidad > maximos.get(clave)){
                maximos.put(clave, cantidad);
                resultado.put(clave, valor);
            }
        }

        return resultado;
    }

}
