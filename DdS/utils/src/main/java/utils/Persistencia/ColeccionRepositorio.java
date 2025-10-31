package utils.Persistencia;
import org.hibernate.Hibernate;
import utils.DTO.PageDTO;
import utils.Dominio.Criterios.Criterio;
import utils.Dominio.HechosYColecciones.Coleccion;
import utils.DTO.ColeccionDTO;

import java.util.*;
import java.util.stream.Collectors;

import utils.BDUtils;
import utils.Dominio.HechosYColecciones.Hecho;
import utils.Dominio.HechosYColecciones.ModosDeNavegacion;
import utils.Dominio.fuente.Fuente;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;


public class ColeccionRepositorio {

    public void guardar(Coleccion coleccion) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);

            gestionarTodasLasRelaciones(em, coleccion);

            em.merge(coleccion);
            BDUtils.commit(em);
        } catch (Exception e) {
            BDUtils.rollback(em);
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    private void gestionarTodasLasRelaciones(EntityManager em, Coleccion coleccion) {
        if (coleccion.getHechos() != null && !coleccion.getHechos().isEmpty()) {
            Set<Hecho> hechosGestionados = coleccion.getHechos().stream()
                    .map(hecho -> gestionarHechoExistente(em, hecho))
                    .filter(Objects::nonNull) // Filtrar nulos
                    .collect(Collectors.toSet());
            coleccion.setHechos(new ArrayList<>(hechosGestionados));
        }

        if (coleccion.getHechosConsensuados() != null &&  !coleccion.getHechosConsensuados().isEmpty()) {
            Set<Hecho> hechosConsensuadosGestionados = coleccion.getHechosConsensuados().stream()
                    .map(hecho -> gestionarHechoExistente(em, hecho))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            coleccion.setHechosConsensuados(new ArrayList<>(hechosConsensuadosGestionados));
        }

        if (coleccion.getFuente() != null &&  !coleccion.getFuente().isEmpty()) {
            Set<Fuente> fuentesGestionadas = coleccion.getFuente().stream()
                    .map(fuente -> gestionarFuenteExistente(em, fuente))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            coleccion.setFuente(new ArrayList<>(fuentesGestionadas));
        }

        if (coleccion.getCriteriosDePertenencia() != null &&  !coleccion.getCriteriosDePertenencia().isEmpty()) {
            Set<Criterio> criteriosGestionados = coleccion.getCriteriosDePertenencia().stream()
                    .map(criterio -> gestionarCriterioExistente(em, criterio))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            coleccion.setCriteriosDePertenencia(new ArrayList<>(criteriosGestionados));
        }
    }

    private Hecho gestionarHechoExistente(EntityManager em, Hecho hecho) {
        if (hecho == null) return null;

        Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(hecho);
        if (id != null) {
            Hecho hechoExistente = em.find(Hecho.class, id);
            if (hechoExistente != null) {
                return hechoExistente;
            }
        }

        if (hecho.getTitulo() != null) {
            try {
                Hecho hechoPorTitulo = em.createQuery(
                                "SELECT h FROM Hecho h WHERE LOWER(h.titulo) = LOWER(:titulo)", Hecho.class)
                        .setParameter("titulo", hecho.getTitulo().trim())
                        .getResultStream()
                        .findFirst()
                        .orElse(null);

                if (hechoPorTitulo != null) {
                    return hechoPorTitulo;
                }
            } catch (Exception e) {
                System.out.println("⚠️ No se pudo buscar hecho por título: " + e.getMessage());
            }
        }

        return em.merge(hecho);
    }

    /**
     * Busca y asigna fuentes existentes de la base de datos
     */
    private Fuente gestionarFuenteExistente(EntityManager em, Fuente fuente) {
        if (fuente == null) return null;

        // Buscar por ID si existe
        Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(fuente);
        if (id != null) {
            Fuente fuenteExistente = em.find(Fuente.class, id);
            if (fuenteExistente != null) {
                System.out.println("🔍 Fuente existente encontrada por ID: " + id);
                return fuenteExistente;
            }
        }

        if (fuente.getDescriptor() != null) {
            try {
                Fuente fuentePorDescriptor = em.createQuery(
                                "SELECT f FROM Fuente f WHERE LOWER(f.descriptor) = LOWER(:descriptor)", Fuente.class)
                        .setParameter("descriptor", fuente.getDescriptor().trim())
                        .getResultStream()
                        .findFirst()
                        .orElse(null);

                if (fuentePorDescriptor != null) {
                    System.out.println("🔍 Fuente existente encontrada por descriptor: " + fuente.getDescriptor());
                    return fuentePorDescriptor;
                }
            } catch (Exception e) {
                System.out.println("⚠️ No se pudo buscar fuente por descriptor: " + e.getMessage());
            }
        }

        return em.merge(fuente);
    }

    private Criterio gestionarCriterioExistente(EntityManager em, Criterio criterio) {
        if (criterio == null) return null;

        Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(criterio);
        if (id != null) {
            Criterio criterioExistente = em.find(Criterio.class, id);
            if (criterioExistente != null) {
                return criterioExistente;
            }
        }
        return em.merge(criterio);
    }

    public List<Coleccion> obtenerTodas() {
        EntityManager em = BDUtils.getEntityManager();
        try {
            TypedQuery<Coleccion> query = em.createQuery("SELECT c FROM Coleccion c", Coleccion.class);
            List<Coleccion> resultado = query.getResultList();

            for(Coleccion c : resultado){
                Hibernate.initialize(c.getHechos());
                Hibernate.initialize(c.getFuente());
                Hibernate.initialize(c.getHechosConsensuados());
                Hibernate.initialize(c.getCriteriosDePertenencia());
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<Coleccion> buscarPorHandle(String handle) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            TypedQuery<Coleccion> query = em.createQuery(
                    "SELECT c FROM Coleccion c WHERE c.handle = :handleParam", Coleccion.class);

            query.setParameter("handleParam", handle);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public List<Hecho> obtenerHechosEspecificos(String handle, List<Criterio> criterios, ModosDeNavegacion modosDeNavegacion) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            TypedQuery<Coleccion> query = em.createQuery(
                    "SELECT c FROM Coleccion c WHERE c.handle = :handleParam", Coleccion.class);
            query.setParameter("handleParam", handle);

            Coleccion coleccion = query.getSingleResult();
            return coleccion.obtenerHechosQueCumplen(criterios, modosDeNavegacion);

        } catch (NoResultException e) {
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    public void actualizar(Coleccion coleccion) {
        this.guardar(coleccion);
    }

    public void eliminar(Coleccion coleccion) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);

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
