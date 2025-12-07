package agregador.repository;

import agregador.domain.Usuario.Usuario;
import agregador.utils.BDUtils;

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
            // Si no tiene ID, es nuevo -> Persist
            if (usuario.getId_usuario() == null) {
                em.persist(usuario);
                usuarioGestionado = usuario;
            } else {
                // Si tiene ID, es existente -> Merge
                usuarioGestionado = em.merge(usuario);
            }

            BDUtils.commit(em);
            return usuarioGestionado;

        } catch (Exception e) {
            // Check de seguridad antes de rollback
            if (em.getTransaction().isActive()) {
                BDUtils.rollback(em);
            }
            System.err.println("ERROR al guardar Usuario: " + e.getMessage());
            e.printStackTrace();

            // CORRECCIÓN: Envolvemos en RuntimeException para no obligar a try-catch sucios arriba
            throw new RuntimeException("Error guardando usuario en base de datos", e);
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
}