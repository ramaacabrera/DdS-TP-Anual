package gestorAdministrativo.repository;

import utils.BDUtils;
import utils.Dominio.Usuario.RolUsuario;
import utils.Dominio.Usuario.Usuario;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class UsuarioRepositorio {
    public UsuarioRepositorio(){}

    public void guardar(Usuario usuario) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            // Ejecutar la Transacción
            BDUtils.comenzarTransaccion(em);
            em.merge(usuario);
            BDUtils.commit(em);

        } catch (Exception e) {
            BDUtils.rollback(em);
            System.err.println("ERROR al guardar Usuario: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public Usuario buscarPorUsername(String username){
        EntityManager em = BDUtils.getEntityManager();
        try {
            // Consulta JPQL para buscar por un atributo específico
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.username = :paramUsername", Usuario.class);

            // Asignamos el valor al parámetro en la consulta
            query.setParameter("paramUsername", username);

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

    public Usuario buscarAdmin(String username){
        EntityManager em = BDUtils.getEntityManager();
        try {
            // Consulta JPQL para buscar por un atributo específico
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.username = :paramUsername and u.rol = :paramRol", Usuario.class);

            // Asignamos el valor al parámetro en la consulta
            query.setParameter("paramUsername", username);
            query.setParameter("paramRol", RolUsuario.ADMINISTRADOR);

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

}