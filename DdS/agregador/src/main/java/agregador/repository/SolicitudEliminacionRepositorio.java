package agregador.repository;

import org.hibernate.Hibernate;
import utils.BDUtils;
import utils.DTO.SolicitudDeEliminacionDTO;
import utils.Dominio.HechosYColecciones.Hecho;
import utils.Dominio.Solicitudes.EstadoSolicitudEliminacion;
import utils.Dominio.Solicitudes.SolicitudDeEliminacion;
import utils.Dominio.Usuario.Usuario;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SolicitudEliminacionRepositorio {
    private final HechoRepositorio hechoRepositorio;

    public SolicitudEliminacionRepositorio(HechoRepositorio hechoRepositorio) {
        this.hechoRepositorio = hechoRepositorio;
    }

 public List<SolicitudDeEliminacion> buscarTodas() {
     EntityManager em = BDUtils.getEntityManager();
     try {
         TypedQuery<SolicitudDeEliminacion> query = em.createQuery(
                 "SELECT s FROM SolicitudDeEliminacion s", SolicitudDeEliminacion.class);

         List<SolicitudDeEliminacion> resultados = query.getResultList();

         // ✅ INICIALIZAR RELACIONES LAZY ANTES DE CERRAR LA SESIÓN
         for (SolicitudDeEliminacion solicitud : resultados) {
             // Inicializar el hecho asociado y sus relaciones
             if (solicitud.getHechoAsociado() != null) {
                 Hecho hecho = solicitud.getHechoAsociado();
                 Hibernate.initialize(hecho.getContenidoMultimedia()); // ← ESTE ERA EL PROBLEMA
                 Hibernate.initialize(hecho.getEtiquetas());
                 Hibernate.initialize(hecho.getUbicacion());
             }

             // Inicializar el usuario si existe
             if (solicitud.getUsuario() != null) {
                 Hibernate.initialize(solicitud.getUsuario());
             }
         }

         System.out.println("✅ Encontradas " + resultados.size() + " solicitudes de eliminación");
         return resultados;

     } catch (Exception e) {
         System.err.println("❌ Error en buscarTodas: " + e.getMessage());
         e.printStackTrace();
         return new ArrayList<>();
     } finally {
         em.close();
     }
 }
    public void agregarSolicitudDeEliminacion(SolicitudDeEliminacion solicitud) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);

            if (solicitud.getUsuario() != null) {
                this.setearUsuario(solicitud, em);
            }

            System.out.println("Agregando SolicitudDeEliminacion: "+solicitud);
            em.merge(solicitud);
            BDUtils.commit(em);
        } catch (Exception e) {
            BDUtils.rollback(em);
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void setearUsuario(SolicitudDeEliminacion solicitud, EntityManager em) {
            Usuario usuarioExistente = null;

            if (solicitud.getUsuario().getId_usuario() != null) {
                usuarioExistente = em.find(Usuario.class, solicitud.getUsuario().getId_usuario());
            }

            if (usuarioExistente == null && solicitud.getUsuario().getUsername() != null) {
                try {
                    usuarioExistente = em.createQuery(
                                    "SELECT u FROM Usuario u WHERE u.username = :username", Usuario.class)
                            .setParameter("username", solicitud.getUsuario().getUsername())
                            .getResultStream()
                            .findFirst()
                            .orElse(null);
                } catch (Exception e) {
                    System.out.println("⚠️ Error buscando usuario por username: " + e.getMessage());
                }
            }

            if (usuarioExistente == null &&
                    solicitud.getUsuario().getNombre() != null &&
                    solicitud.getUsuario().getApellido() != null) {
                try {
                    usuarioExistente = em.createQuery(
                                    "SELECT u FROM Usuario u WHERE u.nombre = :nombre AND u.apellido = :apellido", Usuario.class)
                            .setParameter("nombre", solicitud.getUsuario().getNombre())
                            .setParameter("apellido", solicitud.getUsuario().getApellido())
                            .getResultStream()
                            .findFirst()
                            .orElse(null);
                } catch (Exception e) {
                    System.out.println("⚠️ Error buscando usuario por nombre/apellido: " + e.getMessage());
                }
            }

            if (usuarioExistente == null) {
                em.persist(solicitud.getUsuario()); // Persistir el nuevo usuario
                usuarioExistente = solicitud.getUsuario();
            }
            solicitud.setUsuario(usuarioExistente);

    }


    public void agregarSolicitudDeEliminacion(SolicitudDeEliminacionDTO solicitudDTO) {
        // Convierte utils.DTO a Entidad y guarda
        SolicitudDeEliminacion solicitud = new SolicitudDeEliminacion(solicitudDTO, hechoRepositorio);
        this.agregarSolicitudDeEliminacion(solicitud);
    }

    public boolean actualizarEstadoSolicitudEliminacion(String body, UUID id) {
        Optional<SolicitudDeEliminacion> resultadoBusqueda = this.buscarPorId(id);
        if (resultadoBusqueda.isPresent()) {
            SolicitudDeEliminacion solicitud = resultadoBusqueda.get();

            // ✅ DEBUG: Ver estado actual
            System.out.println("🔍 ESTADO ACTUAL - Padre: " + solicitud.getEstadoSolicitudEliminacion());

            EstadoSolicitudEliminacion estadoEnum = EstadoSolicitudEliminacion.valueOf(body.toUpperCase());

            if (estadoEnum == EstadoSolicitudEliminacion.ACEPTADA) {
                solicitud.aceptarSolicitud();
            } else if (estadoEnum == EstadoSolicitudEliminacion.RECHAZADA) {
                solicitud.rechazarSolicitud();
            } else {
                return false;
            }

            // ✅ DEBUG: Ver estado después de modificar
            System.out.println("🔍 ESTADO DESPUÉS - Padre: " + solicitud.getEstadoSolicitudEliminacion());

            // Persistencia del cambio
            EntityManager em = BDUtils.getEntityManager();
            try {
                BDUtils.comenzarTransaccion(em);
                em.merge(solicitud);
                BDUtils.commit(em);

                // ✅ DEBUG: Verificar que se guardó
                System.out.println("✅ Transacción completada - Estado guardado: " + solicitud.getEstadoSolicitudEliminacion());
                return true;
            } catch (Exception e) {
                BDUtils.rollback(em);
                e.printStackTrace();
                return false;
            } finally {
                em.close();
            }
        }
        return false;
    }

    public Optional<SolicitudDeEliminacion> buscarPorId(UUID id) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            System.out.println("🔍 Buscando solicitud con ID: " + id);

            SolicitudDeEliminacion solicitud = em.find(SolicitudDeEliminacion.class, id);

            if (solicitud != null) {
                System.out.println("✅ Solicitud encontrada: " + solicitud.getId());

                // ✅ INICIALIZAR TODAS LAS RELACIONES LAZY
                if (solicitud.getHechoAsociado() != null) {
                    Hecho hecho = solicitud.getHechoAsociado();

                    // Inicializar todas las colecciones lazy del Hecho
                    Hibernate.initialize(hecho.getEtiquetas());        // ← ESTE FALTABA
                    Hibernate.initialize(hecho.getContenidoMultimedia());
                    Hibernate.initialize(hecho.getUbicacion());

                    // Si el hecho tiene usuario contribuyente, inicializarlo también
                    if (hecho.getContribuyente() != null) {
                        Hibernate.initialize(hecho.getContribuyente());
                    }

                    // Si el hecho tiene fuente, inicializarla
                    if (hecho.getFuente() != null) {
                        Hibernate.initialize(hecho.getFuente());
                    }
                }

                // Inicializar el usuario de la solicitud
                if (solicitud.getUsuario() != null) {
                    Hibernate.initialize(solicitud.getUsuario());
                }
            } else {
                System.out.println("❌ Solicitud NO encontrada con ID: " + id);
            }

            return Optional.ofNullable(solicitud);
        } catch (Exception e) {
            System.err.println("❌ Error en buscarPorId: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        } finally {
            em.close();
        }
    }


}
