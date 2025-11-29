package gestorAdministrativo.service;

import gestorAdministrativo.DTO.Solicitudes.SolicitudDeModificacionDTO;
import gestorAdministrativo.DTO.Solicitudes.HechoModificadoDTO;
import gestorAdministrativo.DTO.Solicitudes.EstadoSolicitudModificacionDTO;
import DominioGestorAdministrativo.Solicitudes.EstadoSolicitudModificacion;
import DominioGestorAdministrativo.Solicitudes.SolicitudDeModificacion;
import DominioGestorAdministrativo.HechosYColecciones.Hecho;
import DominioGestorAdministrativo.HechosYColecciones.HechoModificado;
import DominioGestorAdministrativo.Usuario.Usuario;
import gestorAdministrativo.repository.HechoRepositorio;
import gestorAdministrativo.repository.SolicitudModificacionRepositorio;
import gestorAdministrativo.repository.UsuarioRepositorio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class SolicitudModificacionService {
    private final SolicitudModificacionRepositorio solicitudRepositorio;
    private final HechoRepositorio hechoRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    public SolicitudModificacionService(SolicitudModificacionRepositorio solicitudRepositorio,
                                        HechoRepositorio hechoRepositorio,
                                        UsuarioRepositorio usuarioRepositorio) {
        this.solicitudRepositorio = solicitudRepositorio;
        this.hechoRepositorio = hechoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    public void crearSolicitud(SolicitudDeModificacionDTO dto) {
        Hecho hechoOriginal = hechoRepositorio.buscarPorId(dto.getHechoId());
        if (hechoOriginal == null) {
            throw new IllegalArgumentException("El Hecho original no existe: " + dto.getHechoId());
        }

        Usuario usuario = usuarioRepositorio.buscarPorId(dto.getUsuarioId());
        if (usuario == null) {
            throw new IllegalArgumentException("El Usuario no existe: " + dto.getUsuarioId());
        }

        SolicitudDeModificacion solicitud = new SolicitudDeModificacion();
        solicitud.setHechoAsociado(hechoOriginal);
        solicitud.setUsuario(usuario);
        solicitud.setJustificacion(dto.getJustificacion());

        solicitud.setEstado(EstadoSolicitudModificacion.PENDIENTE);

        if (dto.getHechoModificado() != null) {
            HechoModificado cambios = mapHechoModificadoToEntity(dto.getHechoModificado());
            solicitud.setHechoModificado(cambios);
        }

        solicitudRepositorio.guardar(solicitud);
    }

    public boolean procesarSolicitud(UUID solicitudId, String accion) {
        Optional<SolicitudDeModificacion> optSolicitud = solicitudRepositorio.buscarPorId(solicitudId);
        if (optSolicitud.isEmpty()) return false;

        SolicitudDeModificacion solicitud = optSolicitud.get();

        try {
            EstadoSolicitudModificacion nuevoEstado = EstadoSolicitudModificacion.valueOf(accion.toUpperCase());

            if (nuevoEstado == EstadoSolicitudModificacion.ACEPTADA) {
                solicitud.aceptarSolicitud();

                aplicarCambiosAlHechoOriginal(solicitud.getHechoAsociado(), solicitud.getHechoModificado());

                hechoRepositorio.guardar(solicitud.getHechoAsociado());

            } else if (nuevoEstado == EstadoSolicitudModificacion.RECHAZADA) {
                solicitud.rechazarSolicitud();
            } else {
                throw new IllegalArgumentException("Acción no soportada para procesar: " + accion);
            }

            solicitudRepositorio.guardar(solicitud);
            return true;

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado inválido: " + accion);
        }
    }

    public List<SolicitudDeModificacionDTO> obtenerTodasLasSolicitudes() {
        return solicitudRepositorio.buscarTodas().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public Optional<SolicitudDeModificacionDTO> obtenerSolicitudPorId(UUID id) {
        return solicitudRepositorio.buscarPorId(id).map(this::convertirADTO);
    }

    private void aplicarCambiosAlHechoOriginal(Hecho original, HechoModificado cambios) {
        if (original == null || cambios == null) return;

        if (cambios.getTitulo() != null) original.setTitulo(cambios.getTitulo());
        if (cambios.getDescripcion() != null) original.setDescripcion(cambios.getDescripcion());
        if (cambios.getCategoria() != null) original.setCategoria(cambios.getCategoria());

        if (cambios.getFechaDeAcontecimiento() != null) original.setFechaDeAcontecimiento(cambios.getFechaDeAcontecimiento());

        if (cambios.getUbicacion() != null) original.setUbicacion(cambios.getUbicacion());

        System.out.println("✅ Hecho actualizado con los cambios de la solicitud.");
    }

    private HechoModificado mapHechoModificadoToEntity(HechoModificadoDTO dto) {
        if (dto == null) return null;

        HechoModificado entidad = new HechoModificado();
        entidad.setTitulo(dto.getTitulo());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setCategoria(dto.getCategoria());
        entidad.setFechaDeAcontecimiento(dto.getFechaDeAcontecimiento());

        // TODO: Mapear objetos complejos (Ubicacion, Fuente) si es necesario
        // entidad.setUbicacion(mapUbicacion(dto.getUbicacion()));

        return entidad;
    }

    private SolicitudDeModificacionDTO convertirADTO(SolicitudDeModificacion s) {
        SolicitudDeModificacionDTO dto = new SolicitudDeModificacionDTO();

        // Asumiendo que agregaste el campo 'id' en el DTO hijo o padre
        dto.setId(s.getId());
        // Si te da error en .setId, es porque falta agregar 'private UUID id;' en SolicitudDeModificacionDTO

        dto.setJustificacion(s.getJustificacion());

        if (s.getHechoAsociado() != null) {
            dto.setHechoId(s.getHechoAsociado().getHecho_id());
        }

        if (s.getUsuario() != null) {
            dto.setUsuarioId(s.getUsuario().getId_usuario());
        }

        if (s.getEstado() != null) {
            try {
                dto.setEstado(EstadoSolicitudModificacionDTO.valueOf(s.getEstado().name()));
            } catch (Exception e) {
                dto.setEstado(EstadoSolicitudModificacionDTO.PENDIENTE);
            }
        }

        if (s.getHechoModificado() != null) {
            dto.setHechoModificado(convertirHechoModificadoADTO(s.getHechoModificado()));
        }

        return dto;
    }

    private HechoModificadoDTO convertirHechoModificadoADTO(HechoModificado hm) {
        if (hm == null) return null;
        HechoModificadoDTO dto = new HechoModificadoDTO();
        dto.setTitulo(hm.getTitulo());
        dto.setDescripcion(hm.getDescripcion());
        dto.setCategoria(hm.getCategoria());
        dto.setFechaDeAcontecimiento(hm.getFechaDeAcontecimiento());
        return dto;
    }
}