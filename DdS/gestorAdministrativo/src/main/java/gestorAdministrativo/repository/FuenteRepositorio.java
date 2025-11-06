package gestorAdministrativo.repository;

import utils.BDUtils;
import utils.Dominio.fuente.Fuente;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.UUID;

public class FuenteRepositorio {

    public FuenteRepositorio() {}

    public Fuente buscarPorRuta(String ruta) {
        //EntityManager em = emf.createEntityManager();
        System.out.println("Buscando fuente: " + ruta);
        EntityManager em = BDUtils.getEntityManager();
        try {
            TypedQuery<Fuente> query = em.createQuery(
                    "SELECT f FROM Fuente f WHERE f.descriptor = :rutaParam", Fuente.class);
            query.setParameter("rutaParam", ruta);
            System.out.println("Ya se busco");
            return query.getSingleResult();
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
