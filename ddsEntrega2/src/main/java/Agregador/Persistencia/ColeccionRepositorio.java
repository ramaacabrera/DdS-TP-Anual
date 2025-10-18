package Agregador.Persistencia;
import Agregador.HechosYColecciones.Coleccion;
import utils.DTO.ColeccionDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import utils.BDUtils;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;


public class ColeccionRepositorio {
    private final EntityManagerFactory emf;
    //private static final List<Coleccion> colecciones = new ArrayList<>();

    public ColeccionRepositorio(EntityManagerFactory emfNuevo) {
        this.emf = emfNuevo;
    }

    //public void guardar(Coleccion coleccion) {
        //colecciones.add(coleccion);
    //}
    public void guardar(Coleccion coleccion) {
        EntityManager em = emf.createEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);

            // em.merge maneja tanto la inserción (si no tiene ID) como la actualización (si tiene ID)
            em.merge(coleccion);

            BDUtils.commit(em);
        } catch (Exception e) {
            BDUtils.rollback(em);
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    //public void guardar(ColeccionDTO coleccionDTO) {
        //colecciones.add(new Coleccion(coleccionDTO));
    //}
    public void guardar(ColeccionDTO coleccionDTO) {Coleccion coleccion = new Coleccion(coleccionDTO);}

    //public List<Coleccion> obtenerTodas() {
        //return colecciones;
    //}
    public List<Coleccion> obtenerTodas() {
        EntityManager em = emf.createEntityManager();
        try {
            // JPQL: Selecciona todos los objetos Coleccion.
            TypedQuery<Coleccion> query = em.createQuery("SELECT c FROM Coleccion c", Coleccion.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    //public Optional<Coleccion> buscarPorHandle(String handle) {
        //return colecciones.stream().filter(c -> c.getHandle().equals(handle)).findFirst();
    //}

    public Optional<Coleccion> buscarPorHandle(String handle) {
        EntityManager em = emf.createEntityManager();
        try {
            // Consulta JPQL para buscar por el campo 'handle'
            TypedQuery<Coleccion> query = em.createQuery(
                    "SELECT c FROM Coleccion c WHERE c.handle = :handleParam", Coleccion.class);

            // Asigna el valor del parámetro de forma segura
            query.setParameter("handleParam", handle);

            // Intenta obtener un único resultado
            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            // Si no encuentra resultados, retorna un Optional vacío
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    /*public void actualizar(Coleccion coleccion) {
        Optional<Coleccion> col = this.buscarPorHandle(coleccion.getHandle());
        Coleccion buscar;
        if(col.isPresent()){
            buscar = col.get();
            colecciones.set(colecciones.indexOf(buscar), coleccion);
        }
    }*/
    public void actualizar(Coleccion coleccion) {
        this.guardar(coleccion);
    }

    //public void eliminar(Coleccion coleccion) {colecciones.remove(coleccion);}

    public void eliminar(Coleccion coleccion) {
        EntityManager em = emf.createEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);

            // 1. Obtener la entidad gestionada (managed) si no lo está.
            // Esto es necesario para poder llamar a em.remove().
            // Asumo que tu entidad Coleccion tiene un método getId().
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
