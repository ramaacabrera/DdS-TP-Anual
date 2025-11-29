package gestorPublico.repository;

import DominioGestorPublico.HechosYColecciones.Coleccion;
import utils.BDUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ColeccionRepositorio {

    public void guardar(Coleccion coleccion) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);

            em.merge(coleccion);

            BDUtils.commit(em);
        } catch (Exception e) {
            BDUtils.rollback(em);
            throw new RuntimeException("Error guardando colección", e);
        } finally {
            em.close();
        }
    }

    public List<Coleccion> obtenerTodas() {
        EntityManager em = BDUtils.getEntityManager();
        try {
            String jpql = "SELECT DISTINCT c FROM Coleccion c " +
                    "LEFT JOIN FETCH c.hechos " +
                    "LEFT JOIN FETCH c.fuentes " +
                    "LEFT JOIN FETCH c.criteriosDePertenencia";

            TypedQuery<Coleccion> query = em.createQuery(jpql, Coleccion.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Coleccion> obtenerPaginadas(int pagina, int limite) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            String jpql = "SELECT c FROM Coleccion c ORDER BY c.titulo ASC";

            TypedQuery<Coleccion> query = em.createQuery(jpql, Coleccion.class);

            int offset = (pagina - 1) * limite;

            query.setFirstResult(offset);
            query.setMaxResults(limite);

            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public Coleccion buscarPorHandle(String handle) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            String jpql = "SELECT DISTINCT c FROM Coleccion c " +
                    "LEFT JOIN FETCH c.hechos " +
                    "LEFT JOIN FETCH c.fuentes " +
                    "WHERE c.handle = :handle";

            return em.createQuery(jpql, Coleccion.class)
                    .setParameter("handle", UUID.fromString(handle))
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void eliminar(Coleccion coleccion) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);
            Coleccion aBorrar = em.merge(coleccion);
            em.remove(aBorrar);
            BDUtils.commit(em);
        } catch (Exception e) {
            BDUtils.rollback(em);
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void actualizar(Coleccion coleccion) {
        guardar(coleccion);
    }

    public long contarTodas() {
        EntityManager em = BDUtils.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(c) FROM Coleccion c", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
}