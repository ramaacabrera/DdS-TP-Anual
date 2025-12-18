package agregador.service;

import agregador.domain.HechosYColecciones.*;
import agregador.domain.Solicitudes.SolicitudDeEliminacion;
import agregador.domain.Solicitudes.SolicitudDeModificacion;
import agregador.domain.Usuario.Usuario;
import agregador.dto.Hechos.ContenidoMultimediaDTO;
import agregador.dto.Hechos.UbicacionDTO;
import agregador.dto.Solicitudes.HechoModificadoDTO;
import agregador.dto.Solicitudes.SolicitudDeEliminacionDTO;
import agregador.dto.Solicitudes.SolicitudDeModificacionDTO;
import agregador.repository.HechoRepositorio;
import agregador.repository.SolicitudEliminacionRepositorio;
import agregador.repository.SolicitudModificacionRepositorio;
import agregador.repository.UsuarioRepositorio;
import agregador.service.normalizacion.ServicioGeoref;
import agregador.utils.DetectorDeSpam;
import com.fasterxml.jackson.databind.ObjectMapper;
import agregador.service.normalizacion.GeolocalizadorOffline;

import java.util.ArrayList;
import java.util.List;

public class GestorSolicitudes {

    private final SolicitudEliminacionRepositorio elimRepo;
    private final SolicitudModificacionRepositorio modRepo;
    private final HechoRepositorio hechoRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final DetectorDeSpam detectorDeSpam;
    private final ServicioGeoref servicioGeoref;

    public GestorSolicitudes(SolicitudEliminacionRepositorio elimRepo, SolicitudModificacionRepositorio modRepo,
                             HechoRepositorio hechoRepositorio, UsuarioRepositorio usuarioRepositorioNuevo) {
        this.elimRepo = elimRepo;
        this.modRepo = modRepo;
        this.hechoRepositorio = hechoRepositorio;
        this.usuarioRepositorio = usuarioRepositorioNuevo;
        this.detectorDeSpam = new DetectorDeSpam();
        this.servicioGeoref = new ServicioGeoref(null,null);
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

                if(dto.getUsuario() != null){
                    Usuario usuario = usuarioRepositorio.buscarPorUsername(dto.getUsuario().getUsername());
                    solicitud.setUsuario(usuario);
                }

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

                ObjectMapper ob = new ObjectMapper();
                System.out.println("Solicitud a agregar: " + ob.writeValueAsString(solicitud));
                modRepo.agregarSolicitudDeModificacion(solicitud);
                System.out.println("Solicitud de modificacion guardada");
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

        List<ContenidoMultimedia> multimediaNuevo = new ArrayList<>();
        List<ContenidoMultimediaDTO> dtoMultimedia = dto.getContenidoMultimedia();
        for(ContenidoMultimediaDTO m: dtoMultimedia){
            multimediaNuevo.add(new ContenidoMultimedia(TipoContenidoMultimedia.valueOf(m.getTipoContenido().toString()), m.getContenido()));
        }

        hm.setContenidoMultimedia(multimediaNuevo);
        if (dto.getUbicacion() != null) {
            enriquecerUbicacionDTO(dto.getUbicacion());
            hm.setUbicacion(new Ubicacion(dto.getUbicacion().getLatitud(), dto.getUbicacion().getLongitud(), dto.getUbicacion().getDescripcion()));

        }
        return hm;
    }

    private void enriquecerUbicacionDTO(UbicacionDTO ubicacion) {
        if (ubicacion == null) return;

        boolean tieneCoordenadas = ubicacion.getLatitud() != 0 && ubicacion.getLongitud() != 0;

        boolean faltaDescripcion = ubicacion.getDescripcion() == null || ubicacion.getDescripcion().trim().isEmpty();

        if (tieneCoordenadas && faltaDescripcion) {
            System.out.println("Buscando descripción para coord: " + ubicacion.getLatitud() + ", " + ubicacion.getLongitud());

            String descripcionEncontrada = null;
            try {
                if (servicioGeoref != null) {
                    descripcionEncontrada = servicioGeoref.obtenerDescripcionPorCoordenadas(
                            ubicacion.getLatitud(),
                            ubicacion.getLongitud()
                    );
                }
            } catch (Exception e) {
                System.err.println("Error conectando con servicioGeoref: " + e.getMessage());
            }

            if (descripcionEncontrada != null && !descripcionEncontrada.isEmpty()) {
                ubicacion.setDescripcion(descripcionEncontrada);
                System.out.println("Ubicación actualizada (API): " + descripcionEncontrada);
            } else {
                System.out.println("API falló o no disponible, calculando ubicación aproximada offline...");

                try {
                    String descripcionOffline = GeolocalizadorOffline.obtenerUbicacionAproximada(
                            ubicacion.getLatitud(),
                            ubicacion.getLongitud()
                    );

                    if (descripcionOffline != null) {
                        ubicacion.setDescripcion(descripcionOffline);
                        System.out.println("Ubicación actualizada (Offline): " + descripcionOffline);
                    }
                } catch (Exception e) {
                    System.err.println("Error en geolocalizador offline: " + e.getMessage());
                    ubicacion.setDescripcion("Ubicación por coordenadas (" + ubicacion.getLatitud() + ", " + ubicacion.getLongitud() + ")");
                }
            }
        }
    }
}