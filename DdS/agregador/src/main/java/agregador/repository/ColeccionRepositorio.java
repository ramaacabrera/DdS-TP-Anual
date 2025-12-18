package agregador.repository;

import agregador.domain.Criterios.Criterio;
import agregador.domain.Criterios.CriterioEtiquetas;
import agregador.domain.HechosYColecciones.Coleccion;
import agregador.domain.fuente.Fuente;
import agregador.domain.HechosYColecciones.Hecho;
import agregador.utils.BDUtils;
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
                    "LEFT JOIN c.hechos " +
                    "LEFT JOIN c.fuentes " +
                    "LEFT JOIN c.criteriosDePertenencia";

            TypedQuery<Coleccion> query = em.createQuery(jpql, Coleccion.class);
            for(Coleccion coleccion : query.getResultList()){
                Hibernate.initialize(coleccion.getHechos());
                Hibernate.initialize(coleccion.getCriteriosDePertenencia());
                Hibernate.initialize(coleccion.getFuente());
                Hibernate.initialize(coleccion.getHechosConsensuados());
                for(Hecho h : coleccion.getHechos()){
                    Hibernate.initialize(h.getContenidoMultimedia());
                    Hibernate.initialize(h.getEtiquetas());
                }
            }
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

            List<Coleccion> resultados = query.getResultList();

            for (Coleccion c : resultados) {
                Hibernate.initialize(c.getCriteriosDePertenencia());

                for (Criterio crit : c.getCriteriosDePertenencia()) {
                    if (crit instanceof CriterioEtiquetas) {
                        Hibernate.initialize(((CriterioEtiquetas) crit).getEtiquetas());
                    }
                }

                Hibernate.initialize(c.getHechos());

                Hibernate.initialize(c.getFuente());
            }
            return resultados;

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

    public void actualizar(Coleccion coleccion) {
        guardar(coleccion);
    }

    public void actualizarTodas(List<Coleccion> colecciones) {
        if (colecciones == null || colecciones.isEmpty()) {
            return;
        }

        EntityManager em = BDUtils.getEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);

            int batchSize = 50;

            for (int i = 0; i < colecciones.size(); i++) {
                if (colecciones.get(i) != null) {
                    em.merge(colecciones.get(i));
                }
                if (i > 0 && i % batchSize == 0) {
                    em.flush();
                    em.clear();
                }
            }
            BDUtils.commit(em);
        } catch (Exception e) {
            BDUtils.rollback(em);
            e.printStackTrace();
            throw new RuntimeException("Error en actualización masiva de colecciones", e);
        } finally {
            em.close();
        }
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

    public List<Fuente> buscarFuentesPorColeccion(EntityManager em, UUID coleccionId) {
        String jpql = "SELECT f FROM Coleccion c JOIN c.fuentes f WHERE c.handle = :id";
        return em.createQuery(jpql, Fuente.class)
                .setParameter("id", coleccionId)
                .getResultList();
    }

    public void actualizarLoteYLimpiar(List<Coleccion> colecciones) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);

            for (Coleccion c : colecciones) {
                em.merge(c);
            }

            em.flush();
            em.clear();

            BDUtils.commit(em);
        } catch (Exception e) {
            BDUtils.rollback(em);
            throw e;
        }
    }
}