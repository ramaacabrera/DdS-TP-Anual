package estadisticas.agregador;

import estadisticas.BDUtilsEstadisticas;
import estadisticas.Dominio.EstadisticasCategoria;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public Optional<EstadisticasCategoria> buscarPorHandle(String handle) {
        EntityManager em = BDUtilsEstadisticas.getEntityManager();
        try {
            TypedQuery<EstadisticasCategoria> query = em.createQuery(
                    "SELECT e FROM EstadisticasCategoria e WHERE e.id.estadisticas_id = :handleParam", EstadisticasCategoria.class);

            query.setParameter("handleParam", handle);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public List<String> obtenerTodasLasCategorias() {
        EntityManager em = BDUtilsEstadisticas.getEntityManager();
        try {
            TypedQuery<String> query = em.createQuery(
                    "SELECT ec.id.categoria FROM EstadisticasCategoria ec " +
                            "JOIN Estadisticas e ON ec.id.estadisticas_id = e.estadisticas_id " +
                            "ORDER BY e.estadisticas_fecha DESC, ec.id.categoria",
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
                    "SELECT ec.estadisticasCategoria_provincia FROM EstadisticasCategoria ec " +
                            "JOIN Estadisticas e ON ec.id.estadisticas_id = e.estadisticas_id " +
                            "WHERE ec.id.categoria = :categoria " +
                            "ORDER BY e.estadisticas_fecha DESC",
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
                    "SELECT ec.estadisticasCategoria_hora FROM EstadisticasCategoria ec " +
                            "JOIN Estadisticas e ON ec.id.estadisticas_id = e.estadisticas_id " +
                            "WHERE ec.id.categoria = :categoria " +
                            "ORDER BY e.estadisticas_fecha DESC",
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
