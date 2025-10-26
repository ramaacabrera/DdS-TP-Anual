package utils.Persistencia;
import utils.Dominio.Criterios.Criterio;
import utils.Dominio.HechosYColecciones.Coleccion;
import utils.DTO.ColeccionDTO;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import utils.BDUtils;
import utils.Dominio.HechosYColecciones.Hecho;
import utils.Dominio.HechosYColecciones.ModosDeNavegacion;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;


public class ColeccionRepositorio {

    public void guardar(Coleccion coleccion) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);

            em.merge(coleccion);

            BDUtils.commit(em);
        } catch (Exception e) {
            BDUtils.rollback(em);
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void guardar(ColeccionDTO coleccionDTO) {Coleccion coleccion = new Coleccion(coleccionDTO);}


    public List<Coleccion> obtenerTodas() {
        EntityManager em = BDUtils.getEntityManager();
        try {
            TypedQuery<Coleccion> query = em.createQuery("SELECT c FROM Coleccion c", Coleccion.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<Coleccion> buscarPorHandle(String handle) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            TypedQuery<Coleccion> query = em.createQuery(
                    "SELECT c FROM Coleccion c WHERE c.handle = :handleParam", Coleccion.class);

            query.setParameter("handleParam", handle);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public List<Hecho> obtenerHechosEspecificos(String handle, List<Criterio> criterios, ModosDeNavegacion modosDeNavegacion) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            TypedQuery<Coleccion> query = em.createQuery(
                    "SELECT c FROM Coleccion c WHERE c.handle = :handleParam", Coleccion.class);
            query.setParameter("handleParam", handle);

            Coleccion coleccion = query.getSingleResult();
            return coleccion.obtenerHechosQueCumplen(criterios, modosDeNavegacion);

        } catch (NoResultException e) {
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    public void actualizar(Coleccion coleccion) {
        this.guardar(coleccion);
    }

    public void eliminar(Coleccion coleccion) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);

            Coleccion coleccionPersistida = em.contains(coleccion)
                    ? coleccion
                    : em.find(Coleccion.class, coleccion.getHandle());

            if (coleccionPersistida != null) {
                em.remove(coleccionPersistida);
            }

            BDUtils.commit(em);
        } catch (Exception e) {
            BDUtils.rollback(em);
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
