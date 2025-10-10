package Agregador.Persistencia;

import Agregador.Criterios.Criterio;
import Agregador.HechosYColecciones.Hecho;

import utils.BDUtils;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HechoRepositorio {

    public HechoRepositorio() {}

    // Método para obtener todos los hechos
    public List<Hecho> getHechos() {
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
            StringBuilder queryString = new StringBuilder("SELECT * FROM Hecho h");
            if(criterios != null){
                queryString.append(" WHERE 1=1");
            }
            for (Criterio criterio : criterios) {
                queryString.append(" AND ").append(criterio.getQueryCondition());
            }

            // JPQL para seleccionar todos los objetos Hecho
            TypedQuery<Hecho> query = em.createQuery(queryString.toString(), Hecho.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Hecho buscarPorTitulo(String titulo) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            // Consulta JPQL para buscar por un atributo específico
            TypedQuery<Hecho> query = em.createQuery(
                    "SELECT h FROM Hecho h WHERE h.titulo = :paramTitulo", Hecho.class);

            // Asignamos el valor al parámetro en la consulta
            query.setParameter("paramTitulo", titulo);

            // Intentamos obtener un único resultado.
            // Si no se encuentra, getSingleResult() lanza una excepción NoResultException.
            return query.getSingleResult();

        } catch (javax.persistence.NoResultException e) {
            // Esto es normal si no se encuentra el hecho. Retornamos null.
            return null;
        } finally {
            em.close();
        }
    }

    public List<Hecho> buscarSimilares(String titulo) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            // Consulta JPQL para buscar hechos con títulos similares (usa LIKE para similitud)
            TypedQuery<Hecho> query = em.createQuery(
                    "SELECT h FROM Hecho h WHERE h.titulo LIKE :paramTitulo", Hecho.class);

            // Usamos % para búsqueda parcial (similitud)
            query.setParameter("paramTitulo", "%" + titulo + "%");

            // getResultList() retorna lista vacía si no hay resultados
            return query.getResultList();

        } catch (Exception e) {
            // Log del error y retornar lista vacía
            System.err.println("Error al buscar hechos similares: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
    
    public void guardar(Hecho hecho) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            // 1. Verificar Duplicado
            // Busca si ya existe un Hecho con el mismo título
            Hecho existente = this.buscarPorTitulo(hecho.getTitulo());

            if (existente != null) {
                hecho.setHecho_id(existente.getHecho_id()); // Aseguramos que el ID del objeto a guardar sea el del existente

                System.out.println("Duplicado encontrado. Actualizando Hecho con ID: " + existente.getHecho_id());

            } else {
                // No existe. Es una nueva inserción.
                System.out.println("OK: Guardando nuevo Hecho.");
            }

            // 2. Ejecutar la Transacción
            BDUtils.comenzarTransaccion(em);

            // em.merge() maneja la inserción (si es nuevo) o la actualización (si ya existe el ID)
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
}
