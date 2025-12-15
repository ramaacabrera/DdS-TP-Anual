package agregador.service.normalizacion;

import agregador.dto.Hechos.*; // Importa los DTOs anidados (FuenteDTO, etc)
import agregador.domain.HechosYColecciones.*;
import agregador.domain.Usuario.Usuario;
import agregador.domain.fuente.Fuente;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class ServicioNormalizacion {

    private final MockNormalizador normalizador;

    public ServicioNormalizacion(MockNormalizador normalizador) {
        this.normalizador = normalizador;
    }

    public Hecho normalizar(HechoDTO hechoDTO) {
        Hecho hechoSinNormalizar = convertirAEntidad(hechoDTO);

        return normalizador.normalizar(hechoSinNormalizar);
    }

    private Hecho convertirAEntidad(HechoDTO dto) {
        if (dto == null) return null;

        Hecho entidad = new Hecho();

        if (dto.getHechoId() != null) {
            entidad.setHecho_id(dto.getHechoId());
        }
        entidad.setTitulo(dto.getTitulo());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setCategoria(dto.getCategoria());
        entidad.setFechaDeAcontecimiento(dto.getFechaDeAcontecimiento());
        entidad.setFechaDeCarga(dto.getFechaDeCarga());
        entidad.setEsEditable(dto.isEsEditable());

        if (dto.getEstadoHecho() != null) {
            try {
                entidad.setEstadoHecho(EstadoHecho.valueOf(dto.getEstadoHecho().name()));
            } catch (Exception e) {
                entidad.setEstadoHecho(EstadoHecho.ACTIVO);
            }
        }

        if (dto.getUbicacion() != null) {
            UbicacionDTO uDto = dto.getUbicacion();
            Ubicacion u = new Ubicacion(uDto.getLatitud(), uDto.getLongitud(),uDto.getDescripcion());
            if (uDto.getUbicacionId() != null) {
                u.setId_ubicacion(uDto.getUbicacionId());
            }
            entidad.setUbicacion(u);
        }

        if (dto.getFuente() != null) {
            FuenteDTO fDto = dto.getFuente();
            Fuente f = new Fuente();
            if (fDto.getFuenteId() != null) f.setId(fDto.getFuenteId());
            f.setDescriptor(fDto.getDescriptor());

            if (fDto.getTipoDeFuente() != null) {
                try {
                    f.setTipoDeFuente(fDto.getTipoDeFuente());
                } catch (Exception ignored) {}
            }
            entidad.setFuente(f);
        }

        if (dto.getContribuyente() != null) {
            UsuarioDTO uDto = dto.getContribuyente();
            Usuario u = new Usuario();
            if (uDto.getUsuarioId() != null) u.setId_usuario(uDto.getUsuarioId());
            u.setUsername(uDto.getUsername());
            entidad.setContribuyente(u);
        }

        if (dto.getEtiquetas() != null) {
            entidad.setEtiquetas(dto.getEtiquetas().stream().map(eDto -> {
                Etiqueta e = new Etiqueta();
                if (eDto.getId() != null) e.setId(eDto.getId());
                e.setNombre(eDto.getNombre());
                return e;
            }).collect(Collectors.toSet()));
        } else {
            entidad.setEtiquetas(new HashSet<>());
        }

        if (dto.getContenidoMultimedia() != null) {
            entidad.setContenidoMultimedia(dto.getContenidoMultimedia().stream().map(mDto -> {
                ContenidoMultimedia m = new ContenidoMultimedia();
                m.setContenido(mDto.getContenido());
                m.setTipoContenido(TipoContenidoMultimedia.valueOf(mDto.getTipoContenido().toString()));
                return m;
            }).collect(Collectors.toList()));
        } else {
            entidad.setContenidoMultimedia(new ArrayList<>());
        }

        return entidad;
    }
}