package gestorAdministrativo.service;

import gestorAdministrativo.domain.HechosYColecciones.ContenidoMultimedia;
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

        if (cambios.getUbicacion() != null) {
        if (original.getUbicacion() == null) {
            original.setUbicacion(cambios.getUbicacion());
        } else {
            original.getUbicacion().setLatitud(cambios.getUbicacion().getLatitud());
            original.getUbicacion().setLongitud(cambios.getUbicacion().getLongitud());
            original.getUbicacion().setDescripcion(cambios.getUbicacion().getDescripcion());
        }
        }

        if (cambios.getContenidoMultimedia() != null && !cambios.getContenidoMultimedia().isEmpty()) {
            original.getContenidoMultimedia().clear();
            original.getContenidoMultimedia().addAll(cambios.getContenidoMultimedia());

            for (ContenidoMultimedia media : original.getContenidoMultimedia()) {
                media.setHecho(original);
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

        // TODO: Mapear objetos complejos (Ubicacion, Fuente) si es necesario
        // entidad.setUbicacion(mapUbicacion(dto.getUbicacion()));

        return entidad;
    }

    private SolicitudDeModificacionDTO convertirADTO(SolicitudDeModificacion s) {
        SolicitudDeModificacionDTO dto = new SolicitudDeModificacionDTO();

        dto.setId(s.getId());

        dto.setJustificacion(s.getJustificacion());
        Hecho hechoOriginal = s.getHechoAsociado();

        if (s.getHechoModificado() != null && hechoOriginal != null) {

            // --- Generar una lista de cambios con valores originales ---
            List<Map<String, String>> cambiosMapeados = new ArrayList<>();

            HechoModificado cambios = s.getHechoModificado();

            // Título
            if (cambios.getTitulo() != null && !cambios.getTitulo().equals(hechoOriginal.getTitulo())) {
                cambiosMapeados.add(Map.of(
                        "campo", "titulo",
                        "anterior", hechoOriginal.getTitulo() != null ? hechoOriginal.getTitulo() : "",
                        "nuevo", cambios.getTitulo()
                ));
            }

            // Descripción
            if (cambios.getDescripcion() != null && !cambios.getDescripcion().equals(hechoOriginal.getDescripcion())) {
                cambiosMapeados.add(Map.of(
                        "campo", "descripcion",
                        "anterior", hechoOriginal.getDescripcion() != null ? hechoOriginal.getDescripcion() : "",
                        "nuevo", cambios.getDescripcion()
                ));
            }

            // Categoría
            if (cambios.getCategoria() != null && !cambios.getCategoria().equals(hechoOriginal.getCategoria())) {
                cambiosMapeados.add(Map.of(
                        "campo", "categoria",
                        "anterior", hechoOriginal.getCategoria() != null ? hechoOriginal.getCategoria() : "",
                        "nuevo", cambios.getCategoria()
                ));
            }

            // Fecha de Acontecimiento
            if (cambios.getFechaDeAcontecimiento() != null && !cambios.getFechaDeAcontecimiento().equals(hechoOriginal.getFechaDeAcontecimiento())) {
                // Es mejor comparar con el string de la fecha para evitar problemas de formato
                String fechaAnterior = hechoOriginal.getFechaDeAcontecimiento() != null ? hechoOriginal.getFechaDeAcontecimiento().toString() : "";
                String fechaNueva = cambios.getFechaDeAcontecimiento().toString();

                cambiosMapeados.add(Map.of(
                        "campo", "fechaDeAcontecimiento",
                        "anterior", fechaAnterior,
                        "nuevo", fechaNueva
                ));
            }
            dto.setCambios(cambiosMapeados);

            // También podemos agregar el título del hecho original para que el web component lo muestre fácilmente
            dto.setHechoTitulo(hechoOriginal.getTitulo());

            // Si el componente web sigue necesitando el objeto anidado:
            dto.setHechoModificado(convertirHechoModificadoADTO(s.getHechoModificado()));
        }

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

    public Integer obtenerCantidadPendientes() {
        Integer solis = solicitudRepositorio.obtenerCantidadPendientes();
        System.out.println("Total solicitudes modificacion: " + solis);
        return solis;
    }
}