package Estadisticas.Persistencia;

import Agregador.HechosYColecciones.Coleccion;
import Estadisticas.Dominio.Estadisticas;
import utils.BDUtils;
import utils.DTO.ColeccionDTO;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EstadisticasRepositorio {
    private final EntityManager em;

    public EstadisticasRepositorio(EntityManager emNuevo) {
        this.em = emNuevo;
    }

    public void guardar(Estadisticas estadisticas) {
        try {
            BDUtils.comenzarTransaccion(em);

            em.merge(estadisticas);

            BDUtils.commit(em);
        } catch (Exception e) {
            BDUtils.rollback(em);
            e.printStackTrace();
        }
    }

    public Optional<Estadisticas> buscarPorHandle(UUID handle) {
        try {
            TypedQuery<Estadisticas> query = em.createQuery(
                    "SELECT e FROM Estadisticas e WHERE e.estadisticas_id = :handleParam", Estadisticas.class);

            query.setParameter("handleParam", handle);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Integer> buscarSpam() {
        try {
            TypedQuery<Integer> query = em.createQuery(
                    "SELECT e.estadisticas_spam FROM Estadisticas e WHERE e.estadisticas_fecha = (select max(e1.estadisticas_fecha) from Estadisticas e1)", Integer.class);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<String> buscarCategoria_max_hechos() {
        try {
            TypedQuery<String> query = em.createQuery(
                    "SELECT e.estadisticas_categoria_maxHechos FROM Estadisticas e WHERE e.estadisticas_fecha = (select max(e1.estadisticas_fecha) from Estadisticas e1)", String.class);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}