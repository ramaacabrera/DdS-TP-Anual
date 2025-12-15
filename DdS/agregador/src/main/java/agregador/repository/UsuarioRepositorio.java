package agregador.repository;

import agregador.domain.Usuario.Usuario;
import agregador.utils.BDUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.UUID;

public class UsuarioRepositorio {

    public UsuarioRepositorio(){}

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