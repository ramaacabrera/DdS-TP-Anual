package estadisticas.repository;

import estadisticas.domain.EstadisticasColeccion;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.*;

public class EstadisticasColeccionRepositorio {

    public EstadisticasColeccionRepositorio() {
    }

        public void guardar(EstadisticasColeccion estadisticas) {
            EntityManager em = BDUtilsEstadisticas.getEntityManager();
        try {
            BDUtilsEstadisticas.comenzarTransaccion(em);

            em.persist(estadisticas);

            BDUtilsEstadisticas.commit(em);
        } catch (Exception e) {
            BDUtilsEstadisticas.rollback(em);
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

        public Optional<EstadisticasColeccion> buscarPorHandle(UUID handle) {
            EntityManager em = BDUtilsEstadisticas.getEntityManager();
        try {
            TypedQuery<EstadisticasColeccion> query = em.createQuery(
                    "SELECT e FROM EstadisticasColeccion e WHERE e.id.coleccion_id = :handleParam", EstadisticasColeccion.class);

            query.setParameter("handleParam", handle);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public Optional<Map<String, String>> buscarProvinciaYNombreColeccion(UUID idColeccion) {
        EntityManager em = BDUtilsEstadisticas.getEntityManager();
        try {
            TypedQuery<Object[]> query = em.createQuery(
                    "SELECT ec.provincia, ec.titulo " +
                            "FROM EstadisticasColeccion ec " +
                            "WHERE ec.coleccionId = :idColeccion " +
                            "AND ec.estadisticas.estadisticas_id = (SELECT MAX(e2.estadisticas_id) FROM Estadisticas e2)",
                    Object[].class);

            query.setParameter("idColeccion", idColeccion);
            query.setMaxResults(1);

            List<Object[]> resultados = query.getResultList();

            if (resultados.isEmpty()) {
                return Optional.empty();
            }

            Object[] resultado = resultados.get(0);
            Map<String, String> datos = new HashMap<>();
            datos.put("provincia", (String) resultado[0]);
            datos.put("nombre", (String) resultado[1]);

            return Optional.of(datos);

        } catch (Exception e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
    }