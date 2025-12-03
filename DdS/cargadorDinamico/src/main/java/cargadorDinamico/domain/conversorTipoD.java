package cargadorDinamico.domain;

import cargadorDinamico.domain.HechosYColeccionesD.*;
import cargadorDinamico.domain.Solicitudes.EstadoSolicitudEliminacion_D;
import cargadorDinamico.domain.Solicitudes.EstadoSolicitudModificacion_D;
import cargadorDinamico.domain.Solicitudes.SolicitudDeEliminacion_D;
import cargadorDinamico.domain.Solicitudes.SolicitudDeModificacion_D;
import cargadorDinamico.domain.Usuario.Usuario_D;
import cargadorDinamico.domain.DinamicaDto.HechoDTO;
import cargadorDinamico.domain.DinamicaDto.SolicitudDeEliminacionDTO;
import cargadorDinamico.domain.DinamicaDto.SolicitudDeModificacionDTO;
import cargadorDinamico.domain.HechosYColecciones.*;
import cargadorDinamico.domain.Solicitudes.EstadoSolicitudEliminacion;
import cargadorDinamico.domain.Solicitudes.EstadoSolicitudModificacion;
import cargadorDinamico.domain.Usuario.RolUsuario;
import cargadorDinamico.domain.Usuario.Usuario;
import cargadorDinamico.domain.fuente.Fuente;
import cargadorDinamico.domain.fuente.TipoDeFuente;

import java.util.List;
import java.util.ArrayList;

public interface conversorTipoD {

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

    private Fuente crearFuenteDinamica() {
            return new Fuente(TipoDeFuente.DINAMICA, "http://localhost:puerto-dinamico");
    }
}
