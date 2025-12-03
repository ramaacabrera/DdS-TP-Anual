package gestorAdministrativo.repository;

import gestorAdministrativo.utils.BDUtils;
import gestorAdministrativo.domain.Usuario.RolUsuario;
import gestorAdministrativo.domain.Usuario.Usuario;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.UUID;

public class UsuarioRepositorio {

    public UsuarioRepositorio(){}

    public Usuario guardar(Usuario usuario) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);

            Usuario usuarioGestionado;
            if (usuario.getId_usuario() == null) {
                em.persist(usuario);
                usuarioGestionado = usuario;
            } else {
                usuarioGestionado = em.merge(usuario);
            }

            BDUtils.commit(em);
            return usuarioGestionado;

        } catch (Exception e) {
            BDUtils.rollback(em);
            System.err.println("ERROR al guardar Usuario: " + e.getMessage());
            e.printStackTrace();
            throw e; // Relanzar para que el servicio se entere
        } finally {
            em.close();
        }
    }

    public Usuario buscarPorId(UUID id) {
        if (id == null) return null;
        EntityManager em = BDUtils.getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public Usuario buscarPorUsername(String username){
        if (username == null || username.trim().isEmpty()) return null;

        EntityManager em = BDUtils.getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.username = :paramUsername", Usuario.class);

            query.setParameter("paramUsername", username.trim());

            return query.getResultStream().findFirst().orElse(null);

        } finally {
            em.close();
        }
    }

    public Usuario buscarPorNombreYApellido(String nombre, String apellido) {
        if (nombre == null || apellido == null) return null;

        EntityManager em = BDUtils.getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.nombre = :nombre AND u.apellido = :apellido", Usuario.class);

            query.setParameter("nombre", nombre.trim());
            query.setParameter("apellido", apellido.trim());

            return query.getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    public Usuario buscarAdmin(String username){
        EntityManager em = BDUtils.getEntityManager();
        try {
            System.out.println("Buscando admin");
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.username = :paramUsername AND u.rol = :paramRol", Usuario.class);

            query.setParameter("paramUsername", username);
            query.setParameter("paramRol", RolUsuario.ADMINISTRADOR);

            return query.getResultStream().findFirst().orElse(null);

        } finally {
            em.close();
        }
    }
}