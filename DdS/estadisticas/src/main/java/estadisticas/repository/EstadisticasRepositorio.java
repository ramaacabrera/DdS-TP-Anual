package estadisticas.repository;

import estadisticas.domainEstadisticas.Estadisticas;
import estadisticas.utils.BDUtilsEstadisticas;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EstadisticasRepositorio {
    public EstadisticasRepositorio() {
    }

    public void guardar(Estadisticas estadisticas) {
        EntityManager em = BDUtilsEstadisticas.getEntityManager();
        try {
            em.getTransaction().begin();

            em.merge(estadisticas);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }finally {
            em.close();
        }
    }

    public Optional<Estadisticas> buscarPorHandle(UUID handle) {
        EntityManager em = BDUtilsEstadisticas.getEntityManager();
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

    public Optional<Long> buscarSpam() {
        EntityManager em = BDUtilsEstadisticas.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT e.estadisticas_spam FROM Estadisticas e WHERE e.estadisticas_fecha = (SELECT MAX(e1.estadisticas_fecha) FROM Estadisticas e1)",
                    Long.class);

            query.setMaxResults(1); // ← AÑADE ESTO
            List<Long> resultados = query.getResultList();

            return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));

        } catch (Exception e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public Optional<String> buscarCategoria_max_hechos() {
        EntityManager em = BDUtilsEstadisticas.getEntityManager();
        try {
            TypedQuery<String> query = em.createQuery(
                    "SELECT e.estadisticas_categoria_max_hechos FROM Estadisticas e WHERE e.estadisticas_fecha = (SELECT MAX(e1.estadisticas_fecha) FROM Estadisticas e1)",
                    String.class);

            query.setMaxResults(1);
            List<String> resultados = query.getResultList();

            if (resultados.isEmpty() || resultados.get(0) == null) {
                System.out.println("No se encontró categoría máxima");
                return Optional.empty();
            }

            String categoria = resultados.get(0);
            System.out.println("Categoría máxima encontrada: " + categoria);
            return Optional.of(categoria);

        } catch (Exception e) {
            System.err.println("Error buscando categoría máxima: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public Optional<Estadisticas> obtenerUltimaEstadistica() {
        EntityManager em = BDUtilsEstadisticas.getEntityManager();
        try {
            TypedQuery<Estadisticas> query = em.createQuery(
                    "SELECT e FROM Estadisticas e WHERE e.estadisticas_fecha = (SELECT MAX(e1.estadisticas_fecha) FROM Estadisticas e1)",
                    Estadisticas.class);

            query.setMaxResults(1);
            List<Estadisticas> resultados = query.getResultList();

            return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}