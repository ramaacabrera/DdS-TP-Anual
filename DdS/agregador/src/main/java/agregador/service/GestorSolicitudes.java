package agregador.service;

import agregador.domain.HechosYColecciones.Hecho;
import agregador.domain.HechosYColecciones.HechoModificado;
import agregador.domain.Solicitudes.SolicitudDeEliminacion;
import agregador.domain.Solicitudes.SolicitudDeModificacion;
import agregador.domain.Usuario.Usuario;
import agregador.dto.Solicitudes.HechoModificadoDTO;
import agregador.dto.Solicitudes.SolicitudDeEliminacionDTO;
import agregador.dto.Solicitudes.SolicitudDeModificacionDTO;
import agregador.repository.HechoRepositorio;
import agregador.repository.SolicitudEliminacionRepositorio;
import agregador.repository.SolicitudModificacionRepositorio;
import agregador.repository.UsuarioRepositorio;
import agregador.utils.DetectorDeSpam;

import java.util.List;

public class GestorSolicitudes {

    private final SolicitudEliminacionRepositorio elimRepo;
    private final SolicitudModificacionRepositorio modRepo;
    private final HechoRepositorio hechoRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final DetectorDeSpam detectorDeSpam;

    // Necesitas un repo de usuarios o estrategia para asignarlos si vienen solo con ID en el DTO
    // Por ahora, asumiré que el DTO trae el ID y buscamos el hecho.
    // Para el usuario, si el DTO no trae el objeto completo, quizás debas buscarlo o crearlo.

    public GestorSolicitudes(SolicitudEliminacionRepositorio elimRepo, SolicitudModificacionRepositorio modRepo,
                             HechoRepositorio hechoRepositorio, UsuarioRepositorio usuarioRepositorioNuevo) {
        this.elimRepo = elimRepo;
        this.modRepo = modRepo;
        this.hechoRepositorio = hechoRepositorio;
        this.usuarioRepositorio = usuarioRepositorioNuevo;
        this.detectorDeSpam = new DetectorDeSpam();
    }

    public void procesarSolicitudes(List<SolicitudDeModificacionDTO> mods, List<SolicitudDeEliminacionDTO> elims) {
        procesarEliminaciones(elims);
        procesarModificaciones(mods);
    }

    private void procesarEliminaciones(List<SolicitudDeEliminacionDTO> dtos) {
        if (dtos == null) return;

        for (SolicitudDeEliminacionDTO dto : dtos) {
            // 1. Crear instancia vacía
            SolicitudDeEliminacion solicitud = new SolicitudDeEliminacion();

            // 2. Mapear datos básicos
            solicitud.setJustificacion(dto.getJustificacion());

            // 3. Buscar y setear el Hecho real
            if (dto.getHechoId() != null) {
                System.out.println("Tenemos hecho con id");
                Hecho hecho = hechoRepositorio.buscarPorId(dto.getHechoId());
                System.out.println("Encontramos el hecho: ");
                System.out.println(hecho);
                solicitud.setHechoAsociado(hecho);
                System.out.println("seteamos el hecho");
            }

            /*if (dto.getUsuario().getUsuarioId() != null) {
                Usuario u = usuarioRepositorio.buscarPorId(dto.getUsuario().getUsuarioId());
                if (u != null) {
                    solicitud.setUsuario(u);
                } else {
                    System.out.println("⚠️ Usuario no encontrado para solicitud: " + dto.getUsuario().getUsuarioId());
                }
            }*/

            // 5. Verificar Spam
            if (detectorDeSpam.esSpam(dto.getJustificacion())) {
                solicitud.rechazarSolicitud();
                solicitud.setEsSpam(true);
                System.out.println("Spam detectado en solicitud eliminación.");
            }

            // 6. Guardar
            elimRepo.agregarSolicitudDeEliminacion(solicitud);
        }
    }

    private void procesarModificaciones(List<SolicitudDeModificacionDTO> dtos) {
        if (dtos == null) return;

        for (SolicitudDeModificacionDTO dto : dtos) {
            SolicitudDeModificacion solicitud = new SolicitudDeModificacion();

            solicitud.setJustificacion(dto.getJustificacion());

            if (dto.getHechoId() != null) {
                Hecho hecho = hechoRepositorio.buscarPorId(dto.getHechoId());
                solicitud.setHechoAsociado(hecho);
            }

            if (dto.getUsuarioId() != null) {
                Usuario u = new Usuario();
                u.setId_usuario(dto.getUsuarioId());
                solicitud.setUsuario(u);
            }

            if (dto.getHechoModificado() != null) {
                HechoModificado cambios = mapHechoModificado(dto.getHechoModificado());
                solicitud.setHechoModificado(cambios);
            }

            // 6. Verificar Spam
            if (detectorDeSpam.esSpam(dto.getJustificacion())) {
                solicitud.rechazarSolicitud();
                solicitud.setEsSpam(true);
                System.out.println("Spam detectado en solicitud modificación.");
            }

            // 7. Guardar
            modRepo.agregarSolicitudDeModificacion(solicitud);
        }
    }

    private HechoModificado mapHechoModificado(HechoModificadoDTO dto) {
        HechoModificado hm = new HechoModificado();
        hm.setTitulo(dto.getTitulo());
        hm.setDescripcion(dto.getDescripcion());
        hm.setCategoria(dto.getCategoria());
        hm.setFechaDeAcontecimiento(dto.getFechaDeAcontecimiento());
        // ... setear el resto de campos necesarios
        return hm;
    }
}