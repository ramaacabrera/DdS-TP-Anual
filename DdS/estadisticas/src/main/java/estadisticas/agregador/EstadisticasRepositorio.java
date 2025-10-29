package estadisticas.agregador;

import estadisticas.Dominio.Estadisticas;
import utils.BDUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EstadisticasRepositorio {
    private final EntityManagerFactory emf;

    public EstadisticasRepositorio(EntityManagerFactory emNuevo) {
        this.emf = emNuevo;
    }

    public void guardar(Estadisticas estadisticas) {
            EntityManager em = emf.createEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);

            em.merge(estadisticas);

            BDUtils.commit(em);
        } catch (Exception e) {
            BDUtils.rollback(em);
            e.printStackTrace();
        }  finally {
            em.close();
        }
    }

    public Optional<Estadisticas> buscarPorHandle(UUID handle) {
            EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Estadisticas> query = em.createQuery(
                    "SELECT e FROM Estadisticas e WHERE e.estadisticas_id = :handleParam", Estadisticas.class);

            query.setParameter("handleParam", handle);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public Optional<Integer> buscarSpam() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Integer> query = em.createQuery(
                    "SELECT e.estadisticas_spam FROM Estadisticas e WHERE e.estadisticas_fecha = (SELECT MAX(e1.estadisticas_fecha) FROM Estadisticas e1)",
                    Integer.class);

            query.setMaxResults(1); // ← AÑADE ESTO
            List<Integer> resultados = query.getResultList();

            return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));

        } catch (Exception e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public Optional<String> buscarCategoria_max_hechos() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<String> query = em.createQuery(
                    "SELECT e.estadisticas_categoria_max_hechos FROM Estadisticas e WHERE e.estadisticas_fecha = (SELECT MAX(e1.estadisticas_fecha) FROM Estadisticas e1)",
                    String.class);

            query.setMaxResults(1); // ← Evita múltiples resultados
            List<String> resultados = query.getResultList(); // ← Usa getResultList()

            // Verifica si hay resultados y si el valor no es null
            if (resultados.isEmpty() || resultados.get(0) == null) {
                return Optional.empty();
            }
            return Optional.of(resultados.get(0));

        } catch (Exception e) {
            return Optional.empty(); // ← Captura cualquier excepción
        } finally {
            em.close();
        }
    }
}