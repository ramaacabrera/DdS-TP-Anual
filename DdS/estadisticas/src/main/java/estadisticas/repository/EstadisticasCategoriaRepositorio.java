package estadisticas.repository;

import estadisticas.domain.EstadisticasCategoria;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EstadisticasCategoriaRepositorio {

    public EstadisticasCategoriaRepositorio() {

    }

    public void guardar(EstadisticasCategoria estadisticas) {
        EntityManager em = BDUtilsEstadisticas.getEntityManager();
        try {
            BDUtilsEstadisticas.comenzarTransaccion(em);

            em.persist(estadisticas);

            BDUtilsEstadisticas.commit(em);
        } catch (Exception e) {
            BDUtilsEstadisticas.rollback(em);
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public List<String> obtenerTodasLasCategorias() {
        EntityManager em = BDUtilsEstadisticas.getEntityManager();
        try {
            TypedQuery<String> query = em.createQuery(
                    "SELECT ec.categoria FROM EstadisticasCategoria ec " +
                            "WHERE ec.estadisticas.estadisticas_id = (SELECT MAX(e2.estadisticas_id) FROM Estadisticas e2) " +
                            "ORDER BY ec.categoria",
                    String.class);

            return query.getResultList();

        } catch (Exception e) {
            System.err.println("Error obteniendo categorías: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public Optional<String> buscarProvinciaCategoria(String categoria) {
        EntityManager em = BDUtilsEstadisticas.getEntityManager();
        try {
            TypedQuery<String> query = em.createQuery(
                    "SELECT ec.provincia FROM EstadisticasCategoria ec " +
                            "WHERE ec.categoria = :categoria " +
                            "AND ec.estadisticas.estadisticas_id = (SELECT MAX(e2.estadisticas_id) FROM Estadisticas e2)",
                    String.class);

            query.setParameter("categoria", categoria);
            query.setMaxResults(1);

            List<String> resultados = query.getResultList();

            return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));

        } catch (Exception e) {
            System.err.println("Error buscando provincia para categoría '" + categoria + "': " + e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public Optional<Integer> buscarHoraCategoria(String categoria) {
        EntityManager em = BDUtilsEstadisticas.getEntityManager();
        try {
            TypedQuery<Integer> query = em.createQuery(
                    "SELECT ec.hora FROM EstadisticasCategoria ec " +
                            "WHERE ec.categoria = :categoria " +
                            "AND ec.estadisticas.estadisticas_id = (SELECT MAX(e2.estadisticas_id) FROM Estadisticas e2)",
                    Integer.class);

            query.setParameter("categoria", categoria);
            query.setMaxResults(1);

            List<Integer> resultados = query.getResultList();

            return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));

        } catch (Exception e) {
            System.err.println("Error buscando hora para categoría '" + categoria + "': " + e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}
