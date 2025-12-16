package gestorAdministrativo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gestorAdministrativo.domain.HechosYColecciones.ContenidoMultimedia;
import gestorAdministrativo.domain.HechosYColecciones.TipoContenidoMultimedia;
import gestorAdministrativo.domain.Solicitudes.SolicitudDeEliminacion;
import gestorAdministrativo.dto.Hechos.ContenidoMultimediaDTO;
import gestorAdministrativo.dto.Hechos.UbicacionDTO;
import gestorAdministrativo.dto.Hechos.UsuarioDTO;
import gestorAdministrativo.dto.PageDTO;
import gestorAdministrativo.dto.Solicitudes.SolicitudDeEliminacionDTO;
import gestorAdministrativo.dto.Solicitudes.SolicitudDeModificacionDTO;
import gestorAdministrativo.dto.Solicitudes.HechoModificadoDTO;
import gestorAdministrativo.dto.Solicitudes.EstadoSolicitudModificacionDTO;
import gestorAdministrativo.domain.Solicitudes.EstadoSolicitudModificacion;
import gestorAdministrativo.domain.Solicitudes.SolicitudDeModificacion;
import gestorAdministrativo.domain.HechosYColecciones.Hecho;
import gestorAdministrativo.domain.HechosYColecciones.HechoModificado;
import gestorAdministrativo.domain.Usuario.Usuario;
import gestorAdministrativo.repository.HechoRepositorio;
import gestorAdministrativo.repository.SolicitudModificacionRepositorio;
import gestorAdministrativo.repository.UsuarioRepositorio;

import java.util.*;
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

        Usuario usuario = usuarioRepositorio.buscarPorId(dto.getUsuarioId().getUsuarioId());
        if (usuario == null) {
            throw new IllegalArgumentException("El Usuario no existe: " + dto.getUsuarioId().getUsuarioId());
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
        System.out.println("Termine de buscar por Id");
        if (optSolicitud.isEmpty()) return false;

        SolicitudDeModificacion solicitud = optSolicitud.get();

        try {
            EstadoSolicitudModificacion nuevoEstado = EstadoSolicitudModificacion.valueOf(accion.toUpperCase());

            if (nuevoEstado == EstadoSolicitudModificacion.ACEPTADA) {
                solicitud.aceptarSolicitud();

                aplicarCambiosAlHechoOriginal(solicitud.getHechoAsociado(), solicitud.getHechoModificado());

                hechoRepositorio.guardar(solicitud.getHechoAsociado());
                System.out.println("Termine de guardar");

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

    public PageDTO<SolicitudDeModificacionDTO> obtenerTodasLasSolicitudes(int pagina, int limite, String estado) {
        if (pagina < 1) pagina = 1;
        if (limite < 1) limite = 10;

        long totalRegistros = solicitudRepositorio.contarTodas(estado);

        System.out.println("Total registros: " + totalRegistros);

        int totalPages = (int) Math.ceil(totalRegistros / (double) limite);

        if (pagina > totalPages && totalRegistros > 0) {
            return new PageDTO<>(new ArrayList<>(), pagina, limite, totalPages, (int) totalRegistros);
        }

        List<SolicitudDeModificacion> solicitudesPaginadas = solicitudRepositorio.obtenerPaginadas(pagina, limite, estado);

        System.out.println("Total solicitudes recuperadas: " + solicitudesPaginadas.size());

        ObjectMapper mapper = new ObjectMapper();

        System.out.println("Lista de solicitudes: ");

        for(SolicitudDeModificacion sol :  solicitudesPaginadas) {
            try {
                System.out.println("Solicitud: " + mapper.writeValueAsString(sol));
            } catch (Exception e){
                System.err.println("Erro al serializar solicitud: " + e.getMessage());
            }
        }

        List<SolicitudDeModificacionDTO> dtos = solicitudesPaginadas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());

        System.out.println("Devolviendo solicitudes..");

        return new PageDTO<>(dtos, pagina, limite, totalPages, (int) totalRegistros);
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

        if (cambios.getUbicacion() != null) {
            if (original.getUbicacion() == null) {
                original.setUbicacion(new gestorAdministrativo.domain.HechosYColecciones.Ubicacion());
            }

            original.getUbicacion().setLatitud(cambios.getUbicacion().getLatitud());
            original.getUbicacion().setLongitud(cambios.getUbicacion().getLongitud());
            original.getUbicacion().setDescripcion(cambios.getUbicacion().getDescripcion());
        }

        if (cambios.getContenidoMultimedia() != null && !cambios.getContenidoMultimedia().isEmpty()) {

            original.getContenidoMultimedia().clear();

            for (ContenidoMultimedia mediaCambio : cambios.getContenidoMultimedia()) {

                ContenidoMultimedia mediaNueva = new ContenidoMultimedia();

                mediaNueva.setContenido(mediaCambio.getContenido());

                mediaNueva.setHecho(original);
                mediaNueva.setTipoContenido(mediaCambio.getTipoContenido());

                original.getContenidoMultimedia().add(mediaNueva);
            }
        }

        System.out.println("✅ Hecho actualizado correctamente.");
    }

    private HechoModificado mapHechoModificadoToEntity(HechoModificadoDTO dto) {
        if (dto == null) return null;

        HechoModificado entidad = new HechoModificado();

        entidad.setTitulo(dto.getTitulo());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setCategoria(dto.getCategoria());
        entidad.setFechaDeAcontecimiento(dto.getFechaDeAcontecimiento());

        if (dto.getUbicacion() != null) {
            gestorAdministrativo.domain.HechosYColecciones.Ubicacion ubi = new gestorAdministrativo.domain.HechosYColecciones.Ubicacion();
            ubi.setLatitud(dto.getUbicacion().getLatitud());
            ubi.setLongitud(dto.getUbicacion().getLongitud());
            ubi.setDescripcion(dto.getUbicacion().getDescripcion());
            entidad.setUbicacion(ubi);
        }

        if (dto.getContenidoMultimedia() != null) {
            List<ContenidoMultimedia> listaMedia = new ArrayList<>();
            for (ContenidoMultimediaDTO mediaDto : dto.getContenidoMultimedia()) {
                ContenidoMultimedia media = new ContenidoMultimedia();

                media.setContenido(mediaDto.getContenido());

                if (mediaDto.getTipoContenido() != null) {
                    try {
                        String nombreTipo = mediaDto.getTipoContenido().name();

                        media.setTipoContenido(TipoContenidoMultimedia.valueOf(nombreTipo));

                    } catch (IllegalArgumentException e) {
                        System.err.println("Tipo de contenido desconocido: " + mediaDto.getTipoContenido());
                        media.setTipoContenido(TipoContenidoMultimedia.IMAGEN);
                    }
                }

                listaMedia.add(media);
            }
            entidad.setContenidoMultimedia(listaMedia);
        }

        return entidad;
    }

    private SolicitudDeModificacionDTO convertirADTO(SolicitudDeModificacion entidad) {
        if (entidad == null) return null;

        SolicitudDeModificacionDTO dto = new SolicitudDeModificacionDTO();

        System.out.println("Convertir DTO 1");

        // 1. Datos básicos
        dto.setId(entidad.getId());
        dto.setJustificacion(entidad.getJustificacion());

        System.out.println("Convertir DTO 2");

        // 2. ID del Hecho Original
        if (entidad.getHechoAsociado() != null) {
            dto.setHechoId(entidad.getHechoAsociado().getHecho_id());
        }

        System.out.println("Convertir DTO 3");

        if (entidad.getUsuario() != null) {
            UsuarioDTO uDto = new UsuarioDTO();
            uDto.setUsuarioId(entidad.getUsuario().getId_usuario());
            uDto.setUsername(entidad.getUsuario().getUsername());
            dto.setUsuarioId(uDto);
        }

        System.out.println("Convertir DTO 4");

        if (entidad.getEstado() != null) {
            try {
                String nombreEstado = entidad.getEstado().name();
                dto.setEstado(EstadoSolicitudModificacionDTO.valueOf(nombreEstado));
            } catch (IllegalArgumentException e) {
                dto.setEstado(EstadoSolicitudModificacionDTO.PENDIENTE);
            }
        }

        System.out.println("Convertir DTO 5");

        if (entidad.getHechoModificado() != null) {
            dto.setHechoModificado(convertirHechoModificadoADTO(entidad.getHechoModificado()));
        }

        System.out.println("Convertir DTO 6");

        return dto;
    }


    private HechoModificadoDTO convertirHechoModificadoADTO(HechoModificado entidad) {
        if (entidad == null) return null;

        HechoModificadoDTO dto = new HechoModificadoDTO();

        dto.setHechoModificadoId(entidad.getHecho_modificado_id()); // o getId()
        dto.setTitulo(entidad.getTitulo());
        dto.setDescripcion(entidad.getDescripcion());
        dto.setCategoria(entidad.getCategoria());
        dto.setFechaDeAcontecimiento(entidad.getFechaDeAcontecimiento());


        if (entidad.getUbicacion() != null) {
            UbicacionDTO uDto = new UbicacionDTO();
            uDto.setLatitud(entidad.getUbicacion().getLatitud());
            uDto.setLongitud(entidad.getUbicacion().getLongitud());
            uDto.setDescripcion(entidad.getUbicacion().getDescripcion());
            dto.setUbicacion(uDto);
        }

        if (entidad.getContenidoMultimedia() != null) {
            List<ContenidoMultimediaDTO> mediaListDTO = new ArrayList<>();
            for (var mediaEntidad : entidad.getContenidoMultimedia()) {
                ContenidoMultimediaDTO mediaDTO = new ContenidoMultimediaDTO();
                mediaDTO.setContenido(mediaEntidad.getContenido());
                mediaListDTO.add(mediaDTO);
            }
            dto.setContenidoMultimedia(mediaListDTO);
        }

        return dto;
    }

    public Integer obtenerCantidad(String estado) {
        Integer solis = solicitudRepositorio.obtenerCantidad(estado);
        System.out.println("Total solicitudes modificacion: " + solis);
        return solis;
    }
}