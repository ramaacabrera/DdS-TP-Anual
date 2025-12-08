package gestorPublico.repository;

import gestorPublico.domain.Criterios.Criterio;
import gestorPublico.domain.HechosYColecciones.EstadoHecho;
import gestorPublico.domain.HechosYColecciones.Hecho;
import gestorPublico.utils.BDUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HechoRepositorio {

    public HechoRepositorio(){}

    public List<Hecho> buscarHechos(List<Criterio> criterios, int page, int size) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            StringBuilder queryString = new StringBuilder(
                    "SELECT DISTINCT h FROM Hecho h " +
                            "LEFT JOIN FETCH h.etiquetas " +
                            "LEFT JOIN h.contenidoMultimedia " +
                            "LEFT JOIN FETCH h.ubicacion " +
                            "LEFT JOIN FETCH h.contribuyente " +
                            "LEFT JOIN FETCH h.fuente " +
                            "WHERE h.estadoHecho = :estadoActivo "
            );

            agregarCriteriosAQuery(queryString, criterios);

            TypedQuery<Hecho> query = em.createQuery(queryString.toString(), Hecho.class);

            query.setParameter("estadoActivo", EstadoHecho.ACTIVO);

            setearParametros(query, criterios);

            int fromIndex = (page - 1) * size;
            query.setFirstResult(fromIndex);
            query.setMaxResults(size);

            List<Hecho> resultados = query.getResultList();

            for (Hecho h : resultados) {
                if (h.getContenidoMultimedia() != null) {
                    h.getContenidoMultimedia().size();
                }
            }
            return resultados;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public long contarHechos(List<Criterio> criterios) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            // Query optimizada para contar (sin FETCH JOINS innecesarios)
            StringBuilder queryString = new StringBuilder(
                    "SELECT COUNT(DISTINCT h) FROM Hecho h " +
                            "WHERE h.estadoHecho = :estadoActivo "
            );

            agregarCriteriosAQuery(queryString, criterios);

            TypedQuery<Long> query = em.createQuery(queryString.toString(), Long.class);
            query.setParameter("estadoActivo", EstadoHecho.ACTIVO);
            setearParametros(query, criterios);

            return query.getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

    // Helpers privados para evitar duplicar código entre el Count y el Select
    private void agregarCriteriosAQuery(StringBuilder sb, List<Criterio> criterios) {
        if (criterios != null && !criterios.isEmpty()) {
            for (Criterio criterio : criterios) {
                String condition = criterio.getQueryCondition();
                if (condition != null && !condition.trim().isEmpty()) {
                    sb.append(" AND ").append(condition);
                }
            }
        }
    }

    private void setearParametros(Query query, List<Criterio> criterios) {
        if (criterios != null) {
            for (Criterio criterio : criterios) {
                Map<String, Object> params = criterio.getQueryParameters();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    query.setParameter(param.getKey(), param.getValue());
                }
            }
        }
    }

    public List<Hecho> getHechos() {
        return buscarHechos(null, 1, 10);
    }

    public Hecho buscarPorId(UUID id) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            String jpql = "SELECT DISTINCT h FROM Hecho h " +
                    "LEFT JOIN FETCH h.etiquetas " +
                    "LEFT JOIN h.contenidoMultimedia " +
                    "LEFT JOIN FETCH h.ubicacion " +
                    "LEFT JOIN FETCH h.contribuyente " +
                    "WHERE h.hecho_id = :id";

            TypedQuery<Hecho> query = em.createQuery(jpql, Hecho.class);
            query.setParameter("id", id);

            Hecho resultado = query.getSingleResult();

            // Inicializar manualmente
            if (resultado.getContenidoMultimedia() != null) {
                resultado.getContenidoMultimedia().size();
            }

            return resultado;

        } catch (Exception e) {
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
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
}