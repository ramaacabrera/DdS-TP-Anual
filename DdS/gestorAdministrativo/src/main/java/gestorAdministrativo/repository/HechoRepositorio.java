package gestorAdministrativo.repository;

import org.hibernate.Hibernate;
import utils.BDUtils;
import DominioGestorAdministrativo.Criterios.Criterio;
import DominioGestorAdministrativo.HechosYColecciones.Etiqueta;
import DominioGestorAdministrativo.HechosYColecciones.Hecho;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.stream.Collectors;

public class HechoRepositorio {
    public HechoRepositorio(){}

    public List<Hecho> getHechos() {
        //EntityManager em = emf.createEntityManager();
        EntityManager em = BDUtils.getEntityManager();
        try {
            // JPQL para seleccionar todos los objetos Hecho
            TypedQuery<Hecho> query = em.createQuery("SELECT h FROM Hecho h", Hecho.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Hecho> buscarHechos(List<Criterio> criterios) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            StringBuilder queryString = new StringBuilder("SELECT h FROM Hecho h");

            if (criterios != null && !criterios.isEmpty()) {
                queryString.append(" WHERE 1=1");

                // Construir condiciones
                for (Criterio criterio : criterios) {
                    String condition = criterio.getQueryCondition();
                    if (condition != null && !condition.trim().isEmpty()) {
                        queryString.append(" AND ").append(condition);
                    }
                }
            }

            TypedQuery<Hecho> query = em.createQuery(queryString.toString(), Hecho.class);

            // Establecer parámetros
            if (criterios != null) {
                for (Criterio criterio : criterios) {
                    Map<String, Object> params = criterio.getQueryParameters();
                    for (Map.Entry<String, Object> param : params.entrySet()) {
                        query.setParameter(param.getKey(), param.getValue());
                    }
                }
            }

            List<Hecho> resultados = query.getResultList();

            // INICIALIZAR RELACIONES antes de cerrar la sesión
            for (Hecho hecho : resultados) {
                Hibernate.initialize(hecho.getEtiquetas());
                Hibernate.initialize(hecho.getContenidoMultimedia());
                Hibernate.initialize(hecho.getUbicacion());
                Hibernate.initialize(hecho.getContribuyente());
            }

            return resultados;
        } finally {
            em.close();
        }
    }

    public Hecho buscarPorTitulo(String titulo) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            TypedQuery<Hecho> query = em.createQuery(
                    "SELECT h FROM Hecho h WHERE h.titulo = :paramTitulo", Hecho.class);

            query.setParameter("paramTitulo", titulo);

            return query.getSingleResult();

        } catch (javax.persistence.NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Hecho buscarPorId(String idString) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            UUID id = UUID.fromString(idString);
            Hecho hecho = em.find(Hecho.class, id);

            if (hecho != null) {
                // FORZAR LA CARGA DE LAS COLECCIONES ANTES DE CERRAR LA SESIÓN
                Hibernate.initialize(hecho.getEtiquetas());

                Hibernate.initialize(hecho.getContenidoMultimedia());

                // Si tiene Usuario o Ubicacion LAZY, también inicialízalos aquí OJO ESTOOO
                Hibernate.initialize(hecho.getUbicacion());
                Hibernate.initialize(hecho.getContribuyente());
            }

            return hecho;
        } catch (IllegalArgumentException e) {

            System.err.println("ID de hecho no válido: " + idString);
            return null;
        } finally {
            em.close();
        }
    }

    public Hecho buscarPorId(UUID id) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            System.out.println("Buscando el hecho por ID");
            Hecho hecho = em.find(Hecho.class, id);

            if (hecho != null) {
                // FORZAR LA CARGA DE LAS COLECCIONES ANTES DE CERRAR LA SESIÓN
                Hibernate.initialize(hecho.getEtiquetas());
                Hibernate.initialize(hecho.getContenidoMultimedia());
                Hibernate.initialize(hecho.getUbicacion());
                Hibernate.initialize(hecho.getContribuyente());
            }

            return hecho;
        } catch (Exception e) {
            System.err.println("Error al buscar hecho por ID: " + id + " - " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    public void guardar(Hecho hecho) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            Hecho existente = this.buscarPorTitulo(hecho.getTitulo());

            if (existente != null && existente.tieneMismosAtributosQue(hecho)) {
                hecho.setHecho_id(existente.getHecho_id());
                System.out.println("Duplicado encontrado. Actualizando Hecho con ID: " + existente.getHecho_id());
            } else {
                System.out.println("OK: Guardando nuevo Hecho.");
            }

            BDUtils.comenzarTransaccion(em);

            hecho.setFuente(gestionarRelacion(em, hecho.getFuente()));
            hecho.setUbicacion(gestionarRelacion(em, hecho.getUbicacion()));

            // Gestionar colección de etiquetas
            if (hecho.getEtiquetas() != null) {
                Set<Etiqueta> etiquetasGestionadas = hecho.getEtiquetas().stream()
                        .map(etiqueta -> gestionarRelacion(em, etiqueta))
                        .collect(Collectors.toSet());

                hecho.setEtiquetas(new ArrayList<>(etiquetasGestionadas));
            }

            em.merge(hecho);
            BDUtils.commit(em);

        } catch (Exception e) {
            BDUtils.rollback(em);
            System.err.println("ERROR al guardar Hecho: " + e.getMessage());
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

    public void remover(Hecho hecho) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);

            // Para eliminar, primero debemos traer la entidad al contexto de persistencia si no lo está
            Hecho hechoPersistido = em.contains(hecho) ? hecho : em.find(Hecho.class, hecho.getHecho_id());

            if (hechoPersistido != null) {
                em.remove(hechoPersistido);
            }

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
            BDUtils.rollback(em);
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }

    }
}
