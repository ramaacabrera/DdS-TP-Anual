// gestorAdministrativo/cargadorDinamico.service/SolicitudEliminacionService.java
package gestorAdministrativo.service;

import gestorAdministrativo.domain.HechosYColecciones.EstadoHecho;
import gestorAdministrativo.dto.Hechos.UsuarioDTO;
import gestorAdministrativo.dto.Solicitudes.SolicitudDeEliminacionDTO;
import gestorAdministrativo.dto.Solicitudes.SolicitudDTO;
import gestorAdministrativo.domain.HechosYColecciones.Hecho;
import gestorAdministrativo.domain.Usuario.Usuario;
import gestorAdministrativo.repository.HechoRepositorio;
import gestorAdministrativo.repository.SolicitudEliminacionRepositorio;
import gestorAdministrativo.domain.Solicitudes.EstadoSolicitudEliminacion;
import gestorAdministrativo.domain.Solicitudes.SolicitudDeEliminacion;
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

            if (actualizado) {
                System.out.println("✅ Estado de solicitud actualizado a: " + accion);

                // Obtener la solicitud completa
                Optional<SolicitudDeEliminacion> optSolicitud = solicitudRepositorio.buscarPorId(solicitudId);

                if (optSolicitud.isPresent()) {
                    SolicitudDeEliminacion solicitud = optSolicitud.get();
                    System.out.println("Solicitud encontrada: " + solicitud.getId());

                    // CORRECCIÓN: getHechoAsociado() devuelve Hecho, no UUID
                    Hecho hecho = solicitud.getHechoAsociado();

                    if (hecho != null) {
                        UUID hechoId = hecho.getHecho_id();
                        System.out.println("Hecho asociado ID: " + hechoId);
                        System.out.println("Hecho asociado: " + hecho.getTitulo());
                        System.out.println("Estado actual del hecho: " + hecho.getEstadoHecho());

                        if (estado == EstadoSolicitudEliminacion.ACEPTADA) {
                            // Cambiar estado del hecho a OCULTO
                            hecho.setEstadoHecho(EstadoHecho.OCULTO);
                            hechoRepositorio.guardar(hecho);
                            System.out.println("✅ Hecho " + hechoId + " cambiado a estado: OCULTO");

                        } else if (estado == EstadoSolicitudEliminacion.RECHAZADA) {
                            // Asegurar que el hecho esté ACTIVO
                            hecho.setEstadoHecho(EstadoHecho.ACTIVO);
                            hechoRepositorio.guardar(hecho);
                            System.out.println("✅ Hecho " + hechoId + " mantenido como ACTIVO");
                        }

                        // Verificación después de guardar
                        Hecho hechoVerificado = hechoRepositorio.buscarPorId(hechoId);
                        System.out.println("Estado verificado después de guardar: " +
                                (hechoVerificado != null ? hechoVerificado.getEstadoHecho() : "null"));
                    } else {
                        System.err.println("⚠️ Solicitud sin hecho asociado");
                    }
                } else {
                    System.err.println("⚠️ Solicitud no encontrada después de actualizar");
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
        Usuario usuario = solicitud.getUsuario();
        if (usuario != null) {
            UsuarioDTO uDto = new UsuarioDTO(usuario.getId_usuario(), usuario.getUsername());
            dto.setUsuario(uDto);
        }

        return dto;
    }
}