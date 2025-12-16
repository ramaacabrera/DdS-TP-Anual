package agregador.repository;

import agregador.domain.Criterios.Criterio;
import agregador.domain.HechosYColecciones.Hecho;
import agregador.utils.BDUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class HechoRepositorio {

    public HechoRepositorio() {
    }

    public List<Hecho> buscarHechos(List<Criterio> criterios) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            StringBuilder queryString = new StringBuilder(
                    "SELECT DISTINCT h FROM Hecho h " +
                            "LEFT JOIN FETCH h.etiquetas " +
                            "LEFT JOIN FETCH h.contenidoMultimedia " +
                            "LEFT JOIN FETCH h.ubicacion " +
                            "LEFT JOIN FETCH h.contribuyente " +
                            "LEFT JOIN FETCH h.fuente " +
                            "WHERE 1=1"
            );

            if (criterios != null && !criterios.isEmpty()) {
                for (Criterio criterio : criterios) {
                    String condition = criterio.getQueryCondition();
                    if (condition != null && !condition.trim().isEmpty()) {
                        queryString.append(" AND ").append(condition);
                    }
                }
            }

            TypedQuery<Hecho> query = em.createQuery(queryString.toString(), Hecho.class);

            if (criterios != null) {
                for (Criterio criterio : criterios) {
                    Map<String, Object> params = criterio.getQueryParameters();
                    for (Map.Entry<String, Object> param : params.entrySet()) {
                        query.setParameter(param.getKey(), param.getValue());
                    }
                }
            }

            return query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public List<Hecho> getHechos() {
        return buscarHechos(null);
    }

    public Hecho buscarPorId(UUID id) {
        System.out.println("entramos a la f buscar por id- ID: " + id);
        EntityManager em = BDUtils.getEntityManager();
        try {
            // Primero traer el hecho con algunas relaciones
            String jpql = "SELECT DISTINCT h FROM Hecho h " +
                    "LEFT JOIN FETCH h.ubicacion " +
                    "LEFT JOIN FETCH h.contribuyente " +
                    "WHERE h.id = :id";

            System.out.println("JPQL: " + jpql);

            TypedQuery<Hecho> query = em.createQuery(jpql, Hecho.class);
            query.setParameter("id", id);

            Hecho hecho = query.getSingleResult();

            if (hecho != null) {
                hecho.getEtiquetas().size();
                hecho.getContenidoMultimedia().size();
            }

            return hecho;
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public void guardar(Hecho hecho) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);

            hecho.setHecho_id(null);
            if (hecho.getHecho_id() == null) {
                em.persist(hecho);
            } else {
                em.merge(hecho);
            }

            BDUtils.commit(em);
        } catch (Exception e) {
            BDUtils.rollback(em);
            e.printStackTrace();
            throw new RuntimeException("Error guardando hecho", e);
        } finally {
            em.close();
        }
    }

    public List<Hecho> buscarPorColeccion(EntityManager em, UUID coleccionId, boolean consensuados) {

        String jpql;

        if (consensuados) {
            jpql =
                    "SELECT DISTINCT h " +
                            "FROM Coleccion c " +
                            "JOIN c.hechosConsensuados h " +
                            "LEFT JOIN FETCH h.etiquetas " +
                            "LEFT JOIN FETCH h.contenidoMultimedia " +
                            "LEFT JOIN FETCH h.ubicacion " +
                            "LEFT JOIN FETCH h.contribuyente " +
                            "LEFT JOIN FETCH h.fuente " +
                            "WHERE c.handle = :id";
        } else {
            jpql =
                    "SELECT DISTINCT h " +
                            "FROM Coleccion c " +
                            "JOIN c.hechos h " +
                            "LEFT JOIN FETCH h.etiquetas " +
                            "LEFT JOIN FETCH h.contenidoMultimedia " +
                            "LEFT JOIN FETCH h.ubicacion " +
                            "LEFT JOIN FETCH h.contribuyente " +
                            "LEFT JOIN FETCH h.fuente " +
                            "WHERE c.handle = :id";
        }

        return em.createQuery(jpql, Hecho.class)
                .setParameter("id", coleccionId)
                .getResultList();
    }

    public List<Hecho> recargarLista(List<Hecho> hechosDesconectados) {
        if (hechosDesconectados == null || hechosDesconectados.isEmpty()) return new ArrayList<>();

        EntityManager em = BDUtils.getEntityManager();
        List<Hecho> hechosFrescos = new ArrayList<>();

        List<UUID> ids = hechosDesconectados.stream()
                .map(Hecho::getHecho_id)
                .collect(Collectors.toList());

        TypedQuery<Hecho> query = em.createQuery("SELECT h FROM Hecho h WHERE h.id IN :ids", Hecho.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }
}
