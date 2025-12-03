package cargadorDinamico.repository;

import cargadorDinamico.domain.BDUtilsDinamico;
import cargadorDinamico.domain.DinamicaDto.Hecho_D_DTO;
import cargadorDinamico.domain.HechosYColeccionesD.*;
import cargadorDinamico.domain.Solicitudes.EstadoSolicitudEliminacion_D;
import cargadorDinamico.domain.Solicitudes.EstadoSolicitudModificacion_D;
import org.hibernate.Hibernate;
import cargadorDinamico.domain.Usuario.RolUsuario;
import cargadorDinamico.domain.Usuario.Usuario_D;
import cargadorDinamico.domain.Solicitudes.EstadoSolicitudEliminacion;
import cargadorDinamico.domain.Solicitudes.EstadoSolicitudModificacion;
import cargadorDinamico.domain.fuente.Fuente;
import cargadorDinamico.domain.fuente.TipoDeFuente;
import cargadorDinamico.domain.HechosYColecciones.EstadoHecho;
import cargadorDinamico.domain.HechosYColecciones.*;


import cargadorDinamico.domain.Solicitudes.SolicitudDeEliminacion_D;
import cargadorDinamico.domain.Solicitudes.SolicitudDeModificacion_D;

import cargadorDinamico.domain.DinamicaDto.HechoDTO;
import cargadorDinamico.domain.DinamicaDto.SolicitudDeModificacionDTO;
import cargadorDinamico.domain.DinamicaDto.SolicitudDeEliminacionDTO;
import cargadorDinamico.domain.Usuario.*;

import javax.persistence.EntityManager;
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

    private Usuario convertirUsuario(Usuario_D usuarioD) {
        if (usuarioD == null) return null;

        Usuario usuario = new Usuario();
        usuario.setEdad(usuarioD.getEdad());
        usuario.setNombre(usuarioD.getNombre());
        usuario.setApellido(usuarioD.getApellido());
        usuario.setRol(RolUsuario.CONTRIBUYENTE);
        usuario.setUsername(usuarioD.getUsername());

        return usuario;
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

        // Convertir el enum del dinámico al enum del agregador
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

        // Mapeo manual entre los enums
        switch (tipoD) {
            case IMAGEN: return TipoContenidoMultimedia.IMAGEN;
            case VIDEO: return TipoContenidoMultimedia.VIDEO;
            case AUDIO: return TipoContenidoMultimedia.AUDIO;
        }
    return null;
    }

    /*private ContenidoMultimedia convertirContenidoMultimedia(ContenidoMultimedia_D contenidoD) {
        if (contenidoD == null) return null;

        ContenidoMultimedia contenido = new ContenidoMultimedia(
                contenidoD.getTipoContenido(), // Asumiendo que tienen el mismo enum
                contenidoD.getContenido()
        );

        return contenido;
    }*/


    public void resetearHechos() {
        EntityManager em = BDUtilsDinamico.getEntityManager();
        try {
            BDUtilsDinamico.comenzarTransaccion(em);

            //em.createQuery("DELETE FROM HechoDXEtiquetaD").executeUpdate();
            em.createQuery("DELETE FROM Hecho_D").executeUpdate();
            em.createQuery("DELETE FROM Etiqueta_D").executeUpdate();
            em.createQuery("DELETE FROM ContenidoMultimedia_D").executeUpdate();
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

    public void guardarSolicitudModificacion(SolicitudDeModificacion_D solicitud){
        EntityManager em = BDUtilsDinamico.getEntityManager();
        try {
            BDUtilsDinamico.comenzarTransaccion(em);
            em.merge(solicitud); // Usamos persist para insertar nuevo
            BDUtilsDinamico.commit(em);
            System.out.println("SolicitudDeModificacion_D guardado: " + solicitud.getJustificacion());
        } catch (Exception e) {
            BDUtilsDinamico.rollback(em);
            System.err.println("ERROR al guardar SolicitudDeModificacion_D: " + e.getMessage());
            e.printStackTrace();
        }finally {
            em.close();
        }
    }

    public void guardarSolicitudEliminacion(SolicitudDeEliminacion_D solicitud) {
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
            em.createQuery("DELETE FROM Usuario_D").executeUpdate();
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
            //em.createQuery("DELETE FROM Usuario_D").executeUpdate();
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
            return query.getResultList();
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
                .filter(Objects::nonNull) // Filtrar conversiones nulas
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
        dto.setHechoModificado(convertirHechoEntidadAHechoModificado(entidad.getHechoModificado()));
        dto.setEstadoSolicitudModificacion(convertirEstadoSolicitudModificacion(entidad.getEstadoSolicitudModificacion()));
        return dto;
    }

    private EstadoSolicitudEliminacion convertirEstadoSolicitudEliminacion(EstadoSolicitudEliminacion_D estadoD) {
        if (estadoD == null) return EstadoSolicitudEliminacion.PENDIENTE;
        return EstadoSolicitudEliminacion.valueOf(estadoD.name());
    }

    private EstadoSolicitudModificacion convertirEstadoSolicitudModificacion(EstadoSolicitudModificacion_D estadoD) {
        if (estadoD == null) return EstadoSolicitudModificacion.PENDIENTE;
        return EstadoSolicitudModificacion.valueOf(estadoD.name());
    }

    private HechoDTO convertirHechoEntidadAHecho(Hecho_D entidad) {
        if (entidad == null) return null;

        HechoDTO hecho = new HechoDTO();
        hecho.setTitulo(entidad.getTitulo());
        hecho.setDescripcion(entidad.getDescripcion());
        hecho.setCategoria(entidad.getCategoria());
        hecho.setUbicacion(convertirUbicacion(entidad.getUbicacion()));
        hecho.setFechaDeAcontecimiento(entidad.getFechaDeAcontecimiento());
        hecho.setFuente(crearFuenteDinamica());
        hecho.setEstadoHecho(convertirEstado(entidad.getEstadoHecho()));
        hecho.setContribuyente(convertirUsuario(entidad.getContribuyente()));
        hecho.setEtiquetas(convertirEtiquetas(entidad.getEtiquetas()));
        hecho.setEsEditable(entidad.getEsEditable());
        hecho.setContenidoMultimedia(convertirMultimedia(entidad.getContenidoMultimedia()));

        return hecho;
    }

    private HechoModificado convertirHechoEntidadAHechoModificado(Hecho_D entidad) {
        if (entidad == null) return null;

        // Usar el constructor de HechoModificado
        HechoModificado hechoModificado = new HechoModificado(
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

        return hechoModificado;
    }
}
