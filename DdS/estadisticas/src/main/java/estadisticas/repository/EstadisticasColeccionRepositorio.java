package estadisticas.repository;

import estadisticas.domainEstadisticas.EstadisticasColeccion;
import estadisticas.domainEstadisticas.Estadisticas;
import estadisticas.utils.BDUtilsEstadisticas;

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
                    "SELECT e FROM EstadisticasColeccion e WHERE e.coleccionId = :handleParam", EstadisticasColeccion.class);

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
            String queryStr = "SELECT ec.provincia, ec.titulo " +
                    "FROM EstadisticasColeccion ec " +
                    "JOIN ec.estadisticas e " +
                    "WHERE ec.coleccionId = :idColeccion " +
                    "ORDER BY e.estadisticas_fecha DESC";

            TypedQuery<Object[]> query = em.createQuery(queryStr, Object[].class);

            query.setParameter("idColeccion", idColeccion);
            query.setMaxResults(1);

            List<Object[]> resultados = query.getResultList();

            if (resultados.isEmpty()) {
                return Optional.empty();
            }

            Object[] resultado = resultados.get(0);
            Map<String, String> datos = new HashMap<>();
            datos.put("provincia", resultado[0] != null ? (String) resultado[0] : "N/A");
            datos.put("nombre", resultado[1] != null ? (String) resultado[1] : "Sin Título");

            return Optional.of(datos);

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }
    public List<EstadisticasColeccion> buscarPorEstadisticaPadre(Estadisticas estadistica) {
        EntityManager em = BDUtilsEstadisticas.getEntityManager();
        try {
            TypedQuery<EstadisticasColeccion> query = em.createQuery(
                    "SELECT ec FROM EstadisticasColeccion ec WHERE ec.estadisticas = :estadistica",
                    EstadisticasColeccion.class);
            query.setParameter("estadistica", estadistica);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}