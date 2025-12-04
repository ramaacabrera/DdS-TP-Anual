package gestorPublico.repository;

import gestorPublico.domain.Criterios.Criterio;
import gestorPublico.domain.Criterios.CriterioDeTexto;
import gestorPublico.domain.Criterios.CriterioEtiquetas;
import gestorPublico.domain.HechosYColecciones.Coleccion;
import gestorPublico.domain.HechosYColecciones.Hecho;
import gestorPublico.utils.BDUtils;
import org.hibernate.Hibernate;

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

            List<Coleccion> colecciones = query.getResultList();
            for(Coleccion coleccion : colecciones){
                this.inicializarColeccion(coleccion);
            }

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
        System.out.println("ID de la coleccion: " + handle);
        try {
            String jpql = "SELECT DISTINCT c FROM Coleccion c " +
                    "WHERE c.handle = :handle";

            Coleccion coleccion = em.createQuery(jpql, Coleccion.class)
                    .setParameter("handle", UUID.fromString(handle))
                    .getSingleResult();

            this.inicializarColeccion(coleccion);

            return coleccion;
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void inicializarColeccion(Coleccion coleccion){
        Hibernate.initialize(coleccion.getHechos());
        for(Hecho hecho : coleccion.getHechos()){
            Hibernate.initialize(hecho.getEtiquetas());
            Hibernate.initialize(hecho.getContenidoMultimedia());
        }
        Hibernate.initialize(coleccion.getHechosConsensuados());
        Hibernate.initialize(coleccion.getFuente());
        Hibernate.initialize(coleccion.getCriteriosDePertenencia());
        for(Criterio criterio : coleccion.getCriteriosDePertenencia()){
            if(criterio instanceof CriterioEtiquetas){
                Hibernate.initialize(((CriterioEtiquetas) criterio).getEtiquetas());
            }
            if(criterio instanceof CriterioDeTexto){
                Hibernate.initialize(((CriterioDeTexto) criterio).getPalabras());
            }
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