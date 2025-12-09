package gestorAdministrativo.repository;

import gestorAdministrativo.domain.HechosYColecciones.Etiqueta;
import gestorAdministrativo.domain.fuente.Fuente;
import gestorAdministrativo.utils.BDUtils;
import gestorAdministrativo.domain.Criterios.Criterio;
import gestorAdministrativo.domain.HechosYColecciones.Hecho;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.*;

public class HechoRepositorio {

    public HechoRepositorio(){}

    public List<Hecho> buscarHechos(List<Criterio> criterios) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            StringBuilder queryString = new StringBuilder(
                    "SELECT DISTINCT h FROM Hecho h " +
                            "LEFT JOIN h.etiquetas " +
                            "LEFT JOIN h.contenidoMultimedia " +
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

    // En HechoRepositorio.java

    public List<Hecho> buscarHechos(List<Criterio> criterios, List<Fuente> fuentesFiltro) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            StringBuilder queryString = new StringBuilder(
                    "SELECT DISTINCT h FROM Hecho h " +
                            "LEFT JOIN h.etiquetas " +
                            "LEFT JOIN h.contenidoMultimedia " +
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

            if (fuentesFiltro != null && !fuentesFiltro.isEmpty()) {
                queryString.append(" AND h.fuente IN :listaFuentes ");
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

            if (fuentesFiltro != null && !fuentesFiltro.isEmpty()) {
                query.setParameter("listaFuentes", fuentesFiltro);
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
        EntityManager em = BDUtils.getEntityManager();
        try {
            String jpql = "SELECT DISTINCT h FROM Hecho h " +
                    "LEFT JOIN FETCH h.etiquetas " +
                    "LEFT JOIN FETCH h.contenidoMultimedia " +
                    "LEFT JOIN FETCH h.ubicacion " +
                    "LEFT JOIN FETCH h.contribuyente " +
                    "WHERE h.id = :id";

            TypedQuery<Hecho> query = em.createQuery(jpql, Hecho.class);
            query.setParameter("id", id);

            return query.getSingleResult();
        } catch (Exception e) {
            // Si no encuentra nada o falla, retorna null
            return null;
        } finally {
            em.close();
        }
    }

    public Hecho buscarPorId(String idString) {
        try {
            return buscarPorId(UUID.fromString(idString));
        } catch (IllegalArgumentException e) {
            System.err.println("ID inválido: " + idString);
            return null;
        }
    }

    public void guardar(Hecho hecho) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);

            if (hecho.getHecho_id() == null && hecho.getTitulo() != null) {
                Hecho existente = buscarPorTituloInterno(em, hecho.getTitulo());
                if (existente != null) {
                    hecho.setHecho_id(existente.getHecho_id());
                }
            }

            em.merge(hecho);

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

    public Etiqueta buscarEtiquetaPorNombre(String nombreLimpio) {
        EntityManager em = BDUtils.getEntityManager();
        try{
            return em.createQuery("SELECT e FROM Etiqueta e WHERE e.nombre = :t", Etiqueta.class)
                    .setParameter("t", nombreLimpio)
                    .getSingleResult();
        }catch(Exception e){
            BDUtils.rollback(em);
            return null;
        } finally {
            em.close();
        }
    }
}
