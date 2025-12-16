package cargadorDinamico.repository;

import cargadorDinamico.domain.BDUtilsDinamico;
import cargadorDinamico.domain.DinamicaDto.*;
import cargadorDinamico.domain.HechosYColeccionesD.*;
import cargadorDinamico.domain.Solicitudes.EstadoSolicitudEliminacion_D;
import cargadorDinamico.domain.Solicitudes.EstadoSolicitudModificacion_D;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Hibernate;
import cargadorDinamico.domain.Usuario.Usuario;
import cargadorDinamico.domain.Usuario.Usuario_D;
import cargadorDinamico.domain.Solicitudes.EstadoSolicitudEliminacion;
import cargadorDinamico.domain.Solicitudes.EstadoSolicitudModificacion;
import cargadorDinamico.domain.fuente.Fuente;
import cargadorDinamico.domain.fuente.TipoDeFuente;
import cargadorDinamico.domain.HechosYColecciones.EstadoHecho;
import cargadorDinamico.domain.HechosYColecciones.*;


import cargadorDinamico.domain.Solicitudes.SolicitudDeEliminacion_D;
import cargadorDinamico.domain.Solicitudes.SolicitudDeModificacion_D;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class DinamicoRepositorio {
    public DinamicoRepositorio() {}

    public void guardarHecho(Hecho_D hecho) {

        EntityManager em = BDUtilsDinamico.getEntityManager();
        try {
            System.out.println("Guardo hecho en dinamica: " + new ObjectMapper().writeValueAsString(hecho));
            BDUtilsDinamico.comenzarTransaccion(em);
            em.merge(hecho); // Usamos persist para insertar nuevo
            BDUtilsDinamico.commit(em);
            System.out.println("Hecho_D guardado: " + hecho.getTitulo());
        } catch (Exception e) {
            BDUtilsDinamico.rollback(em);
            System.err.println("ERROR al guardar Hecho_D: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void guardarHecho(Hecho_D_DTO hecho) {
        Hecho_D hecho_D = new Hecho_D(hecho);
        guardarHecho(hecho_D);
    }

    public List<Hecho_D> buscarHechosEntidades() {
        EntityManager em = BDUtilsDinamico.getEntityManager();
        try {
            TypedQuery<Hecho_D> query = em.createQuery("SELECT h FROM Hecho_D h", Hecho_D.class);
            List<Hecho_D> hechos = query.getResultList();

            for (Hecho_D hecho : hechos) {
                if (hecho.getUbicacion() != null && !Hibernate.isInitialized(hecho.getUbicacion())) {
                    Hibernate.initialize(hecho.getUbicacion());}
                if (hecho.getContribuyente() != null && !Hibernate.isInitialized(hecho.getContribuyente())) {
                    Hibernate.initialize(hecho.getContribuyente());}
                if (hecho.getEtiquetas() != null && !Hibernate.isInitialized(hecho.getEtiquetas())) {
                    Hibernate.initialize(hecho.getEtiquetas());}
                if (hecho.getContenidoMultimedia() != null && !Hibernate.isInitialized(hecho.getContenidoMultimedia())) {
                    Hibernate.initialize(hecho.getContenidoMultimedia());}
            }

            return hechos;
        } catch (Exception e) {
            System.err.println("Error al obtener hechos: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public List<HechoDTO> buscarHechos() {
        List<Hecho_D> entidades = this.buscarHechosEntidades();
        List<HechoDTO> dtos = new ArrayList<>();

        for (Hecho_D entidad : entidades) {
            HechoDTO dto = convertirEntidadADTO(entidad);
            dtos.add(dto);
        }
        return dtos;
    }

    private HechoDTO convertirEntidadADTO(Hecho_D entidad) {
        return new HechoDTO(
                entidad.getTitulo(),
                entidad.getDescripcion(),
                entidad.getCategoria(),
                convertirUbicacion(entidad.getUbicacion()),
                entidad.getFechaDeAcontecimiento(),
                entidad.getFechaDeCarga(),
                crearFuenteDinamica(),
                convertirEstado(entidad.getEstadoHecho()),
                convertirUsuario(entidad.getContribuyente()),
                convertirEtiquetas(entidad.getEtiquetas()),
                entidad.getEsEditable(),
                convertirMultimedia(entidad.getContenidoMultimedia())
        );
    }

    private Ubicacion convertirUbicacion(Ubicacion_D ubicacionD) {
        if (ubicacionD == null) return null;

        // Si tiene latitud y longitud válidas, usar constructor con coordenadas
        if (ubicacionD.getLatitud() != -999.0 && ubicacionD.getLongitud() != -999.0) {
            return new Ubicacion(
                    ubicacionD.getLatitud(),
                    ubicacionD.getLongitud(),
                    ubicacionD.getDescripcion()
            );
        }
        return null;
    }

    private EstadoHecho convertirEstado(EstadoHecho_D estadoD) {
        return EstadoHecho.valueOf(estadoD.name());
    }

    private Fuente crearFuenteDinamica() {
        return new Fuente(TipoDeFuente.DINAMICA, "DINAMICA");
    }

    private List<Etiqueta> convertirEtiquetas(List<Etiqueta_D> etiquetasD) {
        if (etiquetasD == null) return new ArrayList<>();

        List<Etiqueta> etiquetas = new ArrayList<>();
        for (Etiqueta_D etiquetaD : etiquetasD) {
            Etiqueta etiqueta = convertirEtiqueta(etiquetaD);
            if (etiqueta != null) {
                etiquetas.add(etiqueta);
            }
        }
        return etiquetas;
    }

    private Etiqueta convertirEtiqueta(Etiqueta_D etiquetaD) {
        if (etiquetaD == null) return null;

        Etiqueta etiqueta = new Etiqueta();
        etiqueta.setNombre(etiquetaD.getNombre());

        return etiqueta;
    }

    private List<ContenidoMultimedia> convertirMultimedia(List<ContenidoMultimedia_D> multimediaD) {
        if (multimediaD == null) return new ArrayList<>();

        List<ContenidoMultimedia> multimedia = new ArrayList<>();
        for (ContenidoMultimedia_D contenidoD : multimediaD) {
            ContenidoMultimedia contenido = convertirContenidoMultimedia(contenidoD);
            if (contenido != null) {
                multimedia.add(contenido);
            }
        }
        return multimedia;
    }

    private ContenidoMultimedia convertirContenidoMultimedia(ContenidoMultimedia_D contenidoD) {
        if (contenidoD == null) return null;

        TipoContenidoMultimedia tipoConvertido =
                convertirTipoContenido(contenidoD.getTipoContenido());

        ContenidoMultimedia contenido = new ContenidoMultimedia(
                tipoConvertido,
                contenidoD.getContenido()
        );

        return contenido;
    }

    private TipoContenidoMultimedia convertirTipoContenido(
            TipoContenidoMultimedia_D tipoD) {
        if(tipoD == null) return TipoContenidoMultimedia.IMAGEN;
        switch (tipoD) {
            case IMAGEN: return TipoContenidoMultimedia.IMAGEN;
            case VIDEO: return TipoContenidoMultimedia.VIDEO;
            case AUDIO: return TipoContenidoMultimedia.AUDIO;
        }
        return TipoContenidoMultimedia.IMAGEN;
    }


    public void resetearHechos() {
        EntityManager em = BDUtilsDinamico.getEntityManager();
        try {
            BDUtilsDinamico.comenzarTransaccion(em);

            //em.createQuery("DELETE FROM HechoDXEtiquetaD").executeUpdate();
            em.createQuery("DELETE FROM ContenidoMultimedia_D").executeUpdate();
            em.createQuery("DELETE FROM Hecho_D").executeUpdate();
            em.createQuery("DELETE FROM Etiqueta_D").executeUpdate();
            //em.createQuery("DELETE FROM Usuario_D").executeUpdate();
            em.createQuery("DELETE FROM Ubicacion_D").executeUpdate();

            BDUtilsDinamico.commit(em);
            System.out.println("Todos los hechos han sido reseteados");
        } catch (Exception e) {
            BDUtilsDinamico.rollback(em);
            System.err.println("ERROR al resetear hechos: " + e.getMessage());
        }finally {
            em.close();
        }
    }

    public Hecho_D buscarHechoPorId(UUID id) {
        EntityManager em = BDUtilsDinamico.getEntityManager();
        try {
            return em.find(Hecho_D.class, id);
        } catch (Exception e) {
            System.err.println("Error al buscar hecho por ID: " + e.getMessage());
            return null;
        }finally {
            em.close();
        }
    }

    //-------------------------SOLICITUDES---------------------------------------

    public void guardarSolicitudModificacion(SolicitudDeModificacion_D solicitud) {
        EntityManager em = BDUtilsDinamico.getEntityManager();
        try {
            BDUtilsDinamico.comenzarTransaccion(em);

            // 1. Extraer datos del usuario
            Usuario_D usuarioSolicitud = solicitud.getUsuario();
            UUID usuarioId = usuarioSolicitud.getId_usuario();
            String username = usuarioSolicitud.getUsername();

            // 2. Verificar si usuario existe en BD dinámica
            Usuario_D usuarioDinamico = em.find(Usuario_D.class, usuarioId);

            if (usuarioDinamico == null) {
                // 3. Crear consulta SQL nativa para insertar con ID específico
                String sql = "INSERT INTO Usuario_D (id_usuario, username) VALUES (?, ?)";

                Query query = em.createNativeQuery(sql);
                query.setParameter(1, usuarioId.toString());
                query.setParameter(2, username);
                query.executeUpdate();

                // 4. Refrescar el contexto de persistencia
                em.flush();
                em.clear();

                // 5. Obtener la entidad recién insertada
                usuarioDinamico = em.find(Usuario_D.class, usuarioId);

                System.out.println("Usuario insertado con ID específico: " + usuarioId);
            }

            // 6. Asignar usuario a la solicitud
            solicitud.setUsuario(usuarioDinamico);

            // 7. Guardar solicitud
            // Como HechoModificado_D tiene CascadeType.ALL en la entidad Solicitud, se guarda solo.
            em.persist(solicitud);

            BDUtilsDinamico.commit(em);
            System.out.println("Solicitud guardada exitosamente");

        } catch (Exception e) {
            BDUtilsDinamico.rollback(em);
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al guardar solicitud", e);
        } finally {
            em.close();
        }
    }

    public Usuario_D buscarUsuarioPorId(EntityManager em, String id) {
        try {
            UUID uuid = UUID.fromString(id);

            TypedQuery<Usuario_D> query = em.createQuery(
                    "SELECT u FROM Usuario_D u WHERE u.id_usuario = :id_usuario",
                    Usuario_D.class
            );
            query.setParameter("id_usuario", uuid);

            return query.getResultStream().findFirst().orElse(null);

        } catch (IllegalArgumentException e) {
            System.err.println("Formato de UUID inválido: " + id);
            return null;
        } catch (Exception e) {
            System.err.println("Error buscando usuario por id: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void guardarSolicitudEliminacion(SolicitudDeEliminacion_D solicitud) {
        System.out.println("Llega a Dinamico Repositorio");

        EntityManager em = BDUtilsDinamico.getEntityManager();
        try {
            BDUtilsDinamico.comenzarTransaccion(em);
            em.merge(solicitud); // Usamos persist para insertar nuevo
            BDUtilsDinamico.commit(em);
            System.out.println("SolicitudDeEliminacion_D guardado: " + solicitud.getJustificacion());
        } catch (Exception e) {
            BDUtilsDinamico.rollback(em);
            System.err.println("ERROR al guardar SolicitudDeEliminacion_D: " + e.getMessage());
            e.printStackTrace();
        }finally {
            em.close();
        }
    }

    public void resetearSolicitudesModificacion() {
        EntityManager em = BDUtilsDinamico.getEntityManager();
        try {
            BDUtilsDinamico.comenzarTransaccion(em);
            em.createQuery("DELETE FROM SolicitudDeModificacion_D").executeUpdate();
            // Borramos también los hechos modificados huérfanos si es necesario, aunque el cascade suele encargarse al borrar la solicitud
            em.createQuery("DELETE FROM HechoModificado_D").executeUpdate();
            // em.createQuery("DELETE FROM Usuario_D").executeUpdate(); // Opcional, depende de tu lógica
            BDUtilsDinamico.commit(em);
            System.out.println("Todos las agregador.Solicitudes De Modificacion han sido reseteados");
        } catch (Exception e) {
            BDUtilsDinamico.rollback(em);
            System.err.println("ERROR al resetear agregador.Solicitudes De Modificacion: " + e.getMessage());
        }finally {
            em.close();
        }
    }

    public void resetearSolicitudesEliminacion() {
        EntityManager em = BDUtilsDinamico.getEntityManager();
        try {
            BDUtilsDinamico.comenzarTransaccion(em);
            em.createQuery("DELETE FROM SolicitudDeEliminacion_D").executeUpdate();
            BDUtilsDinamico.commit(em);
            System.out.println("Todos las agregador.Solicitudes De Eliminacion han sido reseteados");
        } catch (Exception e) {
            BDUtilsDinamico.rollback(em);
            System.err.println("ERROR al resetear agregador.Solicitudes De Eliminacion: " + e.getMessage());
        }finally {
            em.close();
        }
    }


    public List<SolicitudDeModificacionDTO> buscarSolicitudesModificacion() {
        List<SolicitudDeModificacion_D> solModificaciones = this.buscarSolModificacionEntidades();
        List<SolicitudDeModificacionDTO> dtos = new ArrayList<>();

        for (SolicitudDeModificacion_D sol : solModificaciones) {
            SolicitudDeModificacionDTO dto = convertirEntidadSModificacionADTO(sol);
            dtos.add(dto);
        }
        return dtos;
    }

    public List<SolicitudDeModificacion_D> buscarSolModificacionEntidades() {
        EntityManager em = BDUtilsDinamico.getEntityManager();
        try {
            TypedQuery<SolicitudDeModificacion_D> query = em.createQuery("SELECT sm FROM SolicitudDeModificacion_D sm", SolicitudDeModificacion_D.class);
            List<SolicitudDeModificacion_D> resultados = query.getResultList();

            for(SolicitudDeModificacion_D sol : resultados) {
                if(sol.getHechoModificado() != null) {
                    Hibernate.initialize(sol.getHechoModificado());
                    if(sol.getHechoModificado().getContenidoMultimedia() != null) {
                        Hibernate.initialize(sol.getHechoModificado().getContenidoMultimedia());
                    }
                }
            }
            return resultados;
        } catch (Exception e) {
            System.err.println("Error al obtener sol de modificación: " + e.getMessage());
            return new ArrayList<>();
        }finally {
            em.close();
        }
    }

    public List<SolicitudDeEliminacionDTO> buscarSolicitudesEliminacion() {
        return this.buscarSolEliminacionEntidades().stream()
                .map(this::convertirEntidadSEliminacionADTO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<SolicitudDeEliminacion_D> buscarSolEliminacionEntidades() {
        EntityManager em = BDUtilsDinamico.getEntityManager();
        try {
            TypedQuery<SolicitudDeEliminacion_D> query = em.createQuery("SELECT se FROM SolicitudDeEliminacion_D se", SolicitudDeEliminacion_D.class);
            List<SolicitudDeEliminacion_D> resultados = query.getResultList();
            for (SolicitudDeEliminacion_D sol : resultados) {
                if (sol.getUsuario() != null) {
                    Hibernate.initialize(sol.getUsuario());
                }
            }
            return resultados;
        } catch (Exception e) {
            System.err.println("Error al obtener sol de eliminacion: " + e.getMessage());
            return new ArrayList<>();
        }finally {
            em.close();
        }
    }

    private SolicitudDeEliminacionDTO convertirEntidadSEliminacionADTO(SolicitudDeEliminacion_D entidad) {
        SolicitudDeEliminacionDTO dto = new SolicitudDeEliminacionDTO();
        dto.setJustificacion(entidad.getJustificacion());
        dto.setID_HechoAsociado(entidad.getHecho_id());
        dto.setusuario(convertirUsuario(entidad.getUsuario()));
        dto.setEstado(convertirEstadoSolicitudEliminacion(entidad.getEstadoSolicitudEliminacion()));
        return dto;
    }

    private SolicitudDeModificacionDTO convertirEntidadSModificacionADTO(SolicitudDeModificacion_D entidad) {
        SolicitudDeModificacionDTO dto = new SolicitudDeModificacionDTO();

        dto.setJustificacion(entidad.getJustificacion());
        dto.setID_HechoAsociado(entidad.getID_HechoAsociado());

        dto.setusuario(convertirUsuario(entidad.getUsuario()));

        dto.setHechoModificado(convertirHechoModificadoEntidadADTO(entidad.getHechoModificado()));
        dto.setEstadoSolicitudModificacion(convertirEstadoSolicitudModificacion(entidad.getEstadoSolicitudModificacion()));

        return dto;
    }

    private HechoModificadoDTO convertirHechoModificadoEntidadADTO(HechoModificado_D entidad) {
        if (entidad == null) return null;

        HechoModificadoDTO dto = new HechoModificadoDTO();
        dto.setTitulo(entidad.getTitulo());
        dto.setDescripcion(entidad.getDescripcion());
        dto.setCategoria(entidad.getCategoria());
        dto.setFechaDeAcontecimiento(entidad.getFechaDeAcontecimiento());

        if(entidad.getUbicacion() != null) {
            dto.setUbicacion(convertirUbicacionADTO(entidad.getUbicacion()));
        }

        dto.setContenidoMultimedia(convertirMultimediaADTO(entidad.getContenidoMultimedia()));

        return dto;
    }

    // --- MÉTODOS AUXILIARES NUEVOS ---

    private UbicacionDTO convertirUbicacionADTO(Ubicacion_D ubicacionD) {
        if (ubicacionD == null) return null;

        // Usamos el constructor vacío y luego asignamos valores
        UbicacionDTO dto = new UbicacionDTO();
        dto.setLatitud(ubicacionD.getLatitud());
        dto.setLongitud(ubicacionD.getLongitud());
        dto.setDescripcion(ubicacionD.getDescripcion());

        return dto;
    }

    private List<ContenidoMultimediaDTO> convertirMultimediaADTO(List<ContenidoMultimedia_D> multimediaD) {
        if (multimediaD == null) return new ArrayList<>();
        List<ContenidoMultimediaDTO> lista = new ArrayList<>();

        for (ContenidoMultimedia_D cmD : multimediaD) {
            TipoContenidoMultimediaDTO tipo = convertirTipoContenidoParaDTO(cmD.getTipoContenido());

            ContenidoMultimediaDTO dto = new ContenidoMultimediaDTO();
            dto.setTipoContenido(tipo);
            dto.setContenido(cmD.getContenido());

            lista.add(dto);
        }
        return lista;
    }

    private TipoContenidoMultimediaDTO convertirTipoContenidoParaDTO(TipoContenidoMultimedia_D tipoD) {
        if(tipoD == null) return TipoContenidoMultimediaDTO.IMAGEN;
        switch (tipoD) {
            case IMAGEN: return TipoContenidoMultimediaDTO.IMAGEN;
            case VIDEO: return TipoContenidoMultimediaDTO.VIDEO;
            case AUDIO: return TipoContenidoMultimediaDTO.AUDIO;
        }
        return TipoContenidoMultimediaDTO.IMAGEN;
    }

    private EstadoSolicitudEliminacion convertirEstadoSolicitudEliminacion(EstadoSolicitudEliminacion_D estadoD) {
        if (estadoD == null) return EstadoSolicitudEliminacion.PENDIENTE;
        return EstadoSolicitudEliminacion.valueOf(estadoD.name());
    }

    private EstadoSolicitudModificacion convertirEstadoSolicitudModificacion(EstadoSolicitudModificacion_D estadoD) {
        if (estadoD == null) return EstadoSolicitudModificacion.PENDIENTE;
        return EstadoSolicitudModificacion.valueOf(estadoD.name());
    }

    private Usuario convertirUsuario(Usuario_D usuarioD) {
        if (usuarioD == null) return null;
        Usuario usuario = new Usuario();
        usuario.setUsername(usuarioD.getUsername());
        return usuario;
    }

    // Los siguientes métodos se mantienen si los usas para otras cosas,
    // pero ya no se usan para la solicitud de modificación.


    private HechoModificado convertirHechoEntidadAHechoModificado(Hecho_D entidad) {
         // YA NO SE USA PORQUE AHORA USAMOS HechoModificado_D
         return null;
    }
}