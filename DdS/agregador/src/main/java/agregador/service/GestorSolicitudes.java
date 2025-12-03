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
            try {
                SolicitudDeEliminacion solicitud = new SolicitudDeEliminacion();

                solicitud.setJustificacion(dto.getJustificacion());

                if (dto.getHechoId() != null) {
                    System.out.println("Tenemos hecho con id");
                    Hecho hecho = hechoRepositorio.buscarPorId(dto.getHechoId());
                    System.out.println("Encontramos el hecho: ");
                    System.out.println(hecho);
                    solicitud.setHechoAsociado(hecho);
                    System.out.println("seteamos el hecho");
                }

                if (detectorDeSpam.esSpam(dto.getJustificacion())) {
                    solicitud.rechazarSolicitud();
                    solicitud.setEsSpam(true);
                    System.out.println("Spam detectado en solicitud eliminación.");
                }

                elimRepo.agregarSolicitudDeEliminacion(solicitud);
            } catch (Exception e) {
                System.err.println("Error procesando solicitud de eliminación: " + e.getMessage());
            }
        }
    }

    private void procesarModificaciones(List<SolicitudDeModificacionDTO> dtos) {
        if (dtos == null) return;

        for (SolicitudDeModificacionDTO dto : dtos) {
            try{
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

                if (detectorDeSpam.esSpam(dto.getJustificacion())) {
                    solicitud.rechazarSolicitud();
                    solicitud.setEsSpam(true);
                    System.out.println("Spam detectado en solicitud modificación.");
                }

                modRepo.agregarSolicitudDeModificacion(solicitud);
            } catch (Exception e) {
                System.err.println("Error procesando solicitud de modificación: " + e.getMessage());
            }
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