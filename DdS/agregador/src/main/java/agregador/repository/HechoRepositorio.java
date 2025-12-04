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

public class HechoRepositorio {

    public HechoRepositorio(){}

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
                        // Asumimos que tus criterios usan el alias 'h' para referirse al Hecho
                        queryString.append(" AND ").append(condition);
                    }
                }
            }

            TypedQuery<Hecho> query = em.createQuery(queryString.toString(), Hecho.class);

            // 3. Establecemos los valores de los parámetros
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
        // Reutilizamos la lógica optimizada pasando null (sin criterios = traer todo)
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

            // Luego inicializar las colecciones manualmente
            if (hecho != null) {
                // Inicializar etiquetas
                hecho.getEtiquetas().size();
                // Inicializar contenidoMultimedia
                hecho.getContenidoMultimedia().size();
            }

            return hecho;
        } catch (NoResultException e) {
            System.out.println("⚠️ NO se encontró ningún Hecho con ID: " + id);
            return null;
        } catch (Exception e) {
            System.out.println("❌ Error en buscarPorId: " + e.getClass().getName());
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    /*public Hecho buscarPorId(String idString) {
        try {
            return buscarPorId(UUID.fromString(idString));
        } catch (IllegalArgumentException e) {
            System.err.println("ID inválido: " + idString);
            return null;
        }
    }*/

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

    private Hecho buscarPorTituloInterno(EntityManager em, String titulo) {
        try {
            return em.createQuery("SELECT h FROM Hecho h WHERE h.titulo = :t", Hecho.class)
                    .setParameter("t", titulo)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Hecho buscarPorTitulo(String titulo) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            return buscarPorTituloInterno(em, titulo);
        } finally {
            em.close();
        }
    }

    public void eliminar(Hecho hecho) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);
            Hecho aBorrar = em.merge(hecho);
            em.remove(aBorrar);
            BDUtils.commit(em);
        } catch (Exception e) {
            BDUtils.rollback(em);
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private <T> T gestionarRelacion(EntityManager em, T entidad) {
        if (entidad == null) return null;

        Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entidad);
        if (id != null) {
            T existente = em.find((Class<T>) entidad.getClass(), id);
            if (existente != null) {
                return existente;
            }
        }

        return em.merge(entidad);
    }

    public void actualizar(Hecho hecho) {
        this.guardar(hecho);
    }

    public List<String> buscarCategorias() {
        EntityManager em = BDUtils.getEntityManager();
        try{
            String jpql = "SELECT DISTINCT h.categoria FROM Hecho h WHERE h.categoria IS NOT NULL ORDER BY h.categoria";
            TypedQuery<String> query = em.createQuery(jpql, String.class);
            return query.getResultList();
        } catch (Exception e) {
            BDUtils.rollback(em);
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }

    }
}
