<<<<<<<< HEAD:DdS/utils/src/main/java/utils/Persistencia/FuenteRepositorio.java
package utils.Persistencia;

import utils.Dominio.fuente.Fuente;
========
package agregador.Persistencia;

import agregador.fuente.Fuente;
>>>>>>>> 198c43e (Pruebas):ddsEntrega2/agregador/src/main/java/agregador/Persistencia/FuenteRepositorio.java
import utils.BDUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.UUID;

public class FuenteRepositorio {

    //private final EntityManagerFactory emf;

    //public FuenteRepositorio(EntityManagerFactory emf) {
        //this.emf = emf;
    //}

    public FuenteRepositorio() {}

    /**
     * Busca una Fuente por su ruta (URL), que actúa como identificador único.
     * @param ruta La URL de la agregador.fuente (ej: http://localhost:8084).
     * @return La entidad Fuente si existe, o null.
     */
    public Fuente buscarPorRuta(String ruta) {
        //EntityManager em = emf.createEntityManager();
        EntityManager em = BDUtils.getEntityManager();
        try {
            TypedQuery<Fuente> query = em.createQuery(
                    "SELECT f FROM Fuente f WHERE f.ruta = :rutaParam", Fuente.class);
            query.setParameter("rutaParam", ruta);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Fuente buscarPorId(UUID id) {
        EntityManager em = BDUtils.getEntityManager();
        try{
            TypedQuery<Fuente> query = em.createQuery(
                    "SELECT f FROM Fuente f WHERE f.id_fuente = :idParam", Fuente.class);
            query.setParameter("idParam", id);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }

    }

    /**
     * Guarda o actualiza una entidad Fuente.
     * @param fuente La entidad Fuente a persistir.
     */
    public Fuente guardar(Fuente fuente) {
        //EntityManager em = emf.createEntityManager();
        EntityManager em = BDUtils.getEntityManager();
        BDUtils.comenzarTransaccion(em);
        try {
            Fuente fuenteGestionada = em.merge(fuente);
            BDUtils.commit(em);
            return fuenteGestionada;
        }
        // Capturamos el error de unicidad (IntegrityConstraintViolationException)
        catch (javax.persistence.PersistenceException e) {
            BDUtils.rollback(em);

            if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                // El registro duplicado es lo esperado, no es un error fatal.
                System.err.println("La Fuente ya existe en la DB.");

                throw new RuntimeException("Duplicado de Fuente detectado.", e);
            }
            throw new RuntimeException("Fallo en la persistencia de Fuente.", e);
        }
        finally {
            em.close();
        }
    }
}
