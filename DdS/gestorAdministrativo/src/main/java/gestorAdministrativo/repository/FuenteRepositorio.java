package gestorAdministrativo.repository;

import gestorAdministrativo.utils.BDUtils;
import gestorAdministrativo.domain.fuente.Fuente;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public class FuenteRepositorio {

    public FuenteRepositorio() {}

    public Fuente buscarPorId(UUID id) {
        if (id == null) return null;
        EntityManager em = BDUtils.getEntityManager();
        try {
            return em.find(Fuente.class, id);
        } finally {
            em.close();
        }
    }

    public Fuente buscarPorDescriptor(String descriptor) {
        if (descriptor == null) return null;

        EntityManager em = BDUtils.getEntityManager();
        try {
            TypedQuery<Fuente> query = em.createQuery(
                    "SELECT f FROM Fuente f WHERE f.descriptor = :descParam", Fuente.class);
            query.setParameter("descParam", descriptor);

            return query.getResultStream().findFirst().orElse(null);

        } catch (Exception e) {
            System.err.println("Error buscando fuente: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    public Fuente guardar(Fuente fuente) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);
            Fuente fuenteGestionada = em.merge(fuente);
            BDUtils.commit(em);
            return fuenteGestionada;
        } catch (Exception e) {
            BDUtils.rollback(em);
            throw new RuntimeException("Error guardando fuente: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public List<Fuente> obtenerTodas() {
        EntityManager em = BDUtils.getEntityManager();
        try {
            String jpql = "SELECT f FROM Fuente f";
            TypedQuery<Fuente> query = em.createQuery(jpql, Fuente.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
}