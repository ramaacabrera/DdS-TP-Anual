// gestorAdministrativo/service/SolicitudEliminacionService.java
package gestorAdministrativo.service;

import DominioGestorAdministrativo.DTO.Hechos.UsuarioDTO;
import DominioGestorAdministrativo.DTO.Solicitudes.SolicitudDeEliminacionDTO;
import DominioGestorAdministrativo.DTO.Solicitudes.SolicitudDTO;
import DominioGestorAdministrativo.HechosYColecciones.Hecho;
import DominioGestorAdministrativo.Usuario.Usuario;
import gestorAdministrativo.repository.HechoRepositorio;
import gestorAdministrativo.repository.SolicitudEliminacionRepositorio;
import DominioGestorAdministrativo.Solicitudes.EstadoSolicitudEliminacion;
import DominioGestorAdministrativo.Solicitudes.SolicitudDeEliminacion;
import gestorAdministrativo.repository.UsuarioRepositorio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class SolicitudEliminacionService {
    private final SolicitudEliminacionRepositorio solicitudRepositorio;
    private final HechoRepositorio hechoRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    public SolicitudEliminacionService(SolicitudEliminacionRepositorio solicitudRepositorio, HechoRepositorio hechoRepositorio, UsuarioRepositorio usuarioRepositorio) {
        this.solicitudRepositorio = solicitudRepositorio;
        this.hechoRepositorio = hechoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    public boolean procesarSolicitud(UUID solicitudId, String accion) {
        try {
            EstadoSolicitudEliminacion estado = EstadoSolicitudEliminacion.valueOf(accion.toUpperCase());

            boolean actualizado = solicitudRepositorio.actualizarEstadoSolicitudEliminacion(accion, solicitudId);

            if (actualizado && estado == EstadoSolicitudEliminacion.ACEPTADA) {
                var solicitud = solicitudRepositorio.buscarPorId(solicitudId);
                if (solicitud.isPresent() && solicitud.get().getHechoAsociado() != null) {
                    hechoRepositorio.eliminar(solicitud.get().getHechoAsociado());
                }
            }
            return actualizado;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Acción inválida: " + accion);
        }
    }

    public List<SolicitudDeEliminacionDTO> obtenerTodasLasSolicitudes() {
        List<SolicitudDeEliminacion> solicitudes = solicitudRepositorio.buscarTodas();

        return solicitudes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public Optional<SolicitudDeEliminacionDTO> obtenerSolicitudPorId(UUID id) {
        Optional<SolicitudDeEliminacion> solicitud = solicitudRepositorio.buscarPorId(id);
        return solicitud.map(this::convertirADTO);
    }

    // En SolicitudEliminacionService.java

    public void crearSolicitudEliminacion(SolicitudDTO dto) {
        Hecho hecho = hechoRepositorio.buscarPorId(dto.getHechoId());

        Usuario usuario = usuarioRepositorio.buscarPorId(dto.getUsuarioId());

        SolicitudDeEliminacion solicitud = new SolicitudDeEliminacion();
        solicitud.setHechoAsociado(hecho);
        solicitud.setUsuario(usuario);
        solicitud.setJustificacion(dto.getJustificacion());

        solicitudRepositorio.guardar(solicitud);
    }

    private SolicitudDeEliminacionDTO convertirADTO(SolicitudDeEliminacion solicitud) {
        SolicitudDeEliminacionDTO dto = new SolicitudDeEliminacionDTO();

        dto.setId(solicitud.getId());
        dto.setJustificacion(solicitud.getJustificacion());
        dto.setEstado(solicitud.getEstado()); // Enum directo

        if (solicitud.getHechoAsociado() != null) {
            dto.setHechoId(solicitud.getHechoAsociado().getHecho_id());
        }

        if (solicitud.getUsuario() != null) {
            UsuarioDTO uDto = new UsuarioDTO();
            dto.setUsuario(uDto);
        }

        return dto;
    }
}