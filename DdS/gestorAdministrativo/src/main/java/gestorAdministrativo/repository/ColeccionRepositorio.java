package gestorAdministrativo.repository;

import utils.BDUtils;
import DominioGestorAdministrativo.HechosYColecciones.Coleccion;
import utils.Dominio.HechosYColecciones.Hecho;
import utils.Dominio.fuente.Fuente;
import utils.Dominio.Criterios.Criterio;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
                    "LEFT JOIN FETCH c.fuente " +
                    "LEFT JOIN FETCH c.criteriosDePertenencia";

            TypedQuery<Coleccion> query = em.createQuery(jpql, Coleccion.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Coleccion buscarPorHandle(String handle) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            String jpql = "SELECT DISTINCT c FROM Coleccion c " +
                    "LEFT JOIN FETCH c.hechos " +
                    "LEFT JOIN FETCH c.fuente " +
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
}