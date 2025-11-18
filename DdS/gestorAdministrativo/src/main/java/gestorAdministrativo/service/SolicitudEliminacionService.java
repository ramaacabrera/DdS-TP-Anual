// gestorAdministrativo/service/SolicitudEliminacionService.java
package gestorAdministrativo.service;

import DominioGestorAdministrativo.DTO.Solicitudes.SolicitudDeEliminacionDTO;
import DominioGestorAdministrativo.DTO.Solicitudes.SolicitudDTO;
import gestorAdministrativo.repository.SolicitudEliminacionRepositorio;
import DominioGestorAdministrativo.Solicitudes.EstadoSolicitudEliminacion;
import DominioGestorAdministrativo.Solicitudes.SolicitudDeEliminacion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class SolicitudEliminacionService {
    private final SolicitudEliminacionRepositorio solicitudRepositorio;

    public SolicitudEliminacionService(SolicitudEliminacionRepositorio solicitudRepositorio) {
        this.solicitudRepositorio = solicitudRepositorio;
    }

    public boolean procesarSolicitud(UUID solicitudId, String accion) {
        try {
            EstadoSolicitudEliminacion estado = EstadoSolicitudEliminacion.valueOf(accion.toUpperCase());
            return solicitudRepositorio.actualizarEstadoSolicitudEliminacion(accion, solicitudId);
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

    public void crearSolicitudEliminacion(SolicitudDTO solicitudDTO) {
        // Convertir SolicitudDTO a SolicitudDeEliminacionDTO si es necesario
        SolicitudDeEliminacionDTO eliminacionDTO = new SolicitudDeEliminacionDTO();
        eliminacionDTO.setID_HechoAsociado(solicitudDTO.getID_HechoAsociado());
        eliminacionDTO.setJustificacion(solicitudDTO.getJustificacion());
        eliminacionDTO.setusuario(solicitudDTO.getUsuario());
        eliminacionDTO.setEstado(EstadoSolicitudEliminacion.PENDIENTE); // Estado por defecto

        solicitudRepositorio.agregarSolicitudDeEliminacion(eliminacionDTO);
    }

    private SolicitudDeEliminacionDTO convertirADTO(SolicitudDeEliminacion solicitud) {
        SolicitudDeEliminacionDTO dto = new SolicitudDeEliminacionDTO();
        dto.setID_HechoAsociado(solicitud.getHechoAsociado() != null ? solicitud.getHechoAsociado().getHecho_id() : null);
        dto.setJustificacion(solicitud.getJustificacion());
        dto.setusuario(solicitud.getUsuario());
        dto.setEstado(solicitud.getEstadoSolicitudEliminacion());

        return dto;
    }
}