package gestorAdministrativo.service;

import DominioGestorAdministrativo.Criterios.*;
import DominioGestorAdministrativo.DTO.Coleccion.ColeccionDTO;
import DominioGestorAdministrativo.DTO.Coleccion.TipoAlgoritmoConsensoDTO;
import DominioGestorAdministrativo.DTO.Criterios.*;
import DominioGestorAdministrativo.DTO.Hechos.EtiquetaDTO;
import DominioGestorAdministrativo.DTO.Hechos.FuenteDTO;
import DominioGestorAdministrativo.DTO.Hechos.UbicacionDTO;
import DominioGestorAdministrativo.HechosYColecciones.*;
import DominioGestorAdministrativo.fuente.Fuente;
import DominioGestorAdministrativo.fuente.TipoDeFuente;
import gestorAdministrativo.repository.ColeccionRepositorio;
import gestorAdministrativo.repository.HechoRepositorio;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ColeccionService {
    private final ColeccionRepositorio coleccionRepositorio;
    private final HechoRepositorio hechoRepositorio;

    public ColeccionService(ColeccionRepositorio coleccionRepositorio, HechoRepositorio hechoRepositorio) {
        this.coleccionRepositorio = coleccionRepositorio;
        this.hechoRepositorio = hechoRepositorio;
    }

    public ColeccionDTO crearColeccion(ColeccionDTO dto) {
        System.out.println("Iniciando creación de colección...");

        Coleccion coleccion = new Coleccion();

        coleccion.setTitulo(dto.getTitulo());
        coleccion.setDescripcion(dto.getDescripcion());

        if (dto.getAlgoritmoDeConsenso() != null) {
            coleccion.setAlgoritmoDeConsenso(convertirAlgoritmo(dto.getAlgoritmoDeConsenso()));
        }

        if (dto.getCriteriosDePertenencia() != null) {
            coleccion.setCriteriosDePertenencia(mapCriteriosToEntity(dto.getCriteriosDePertenencia()));
        }
        if (dto.getFuentes() != null) {
            coleccion.setFuente(mapFuentesToEntity(dto.getFuentes()));
        }

        if (coleccion.getCriteriosDePertenencia() != null && !coleccion.getCriteriosDePertenencia().isEmpty()) {
            List<Hecho> hechosQueCumplen = hechoRepositorio.buscarHechos(coleccion.getCriteriosDePertenencia());
            coleccion.setHechos(hechosQueCumplen);
        }

        coleccionRepositorio.guardar(coleccion);

        return convertirADTO(coleccion);
    }

    public ColeccionDTO actualizarColeccion(UUID id, ColeccionDTO dto) {
        Coleccion coleccionExistente = coleccionRepositorio.buscarPorHandle(id.toString());

        if (coleccionExistente == null) {
            throw new IllegalArgumentException("Colección no encontrada con ID: " + id);
        }

        boolean criteriosCambiaron = false;

        if (dto.getTitulo() != null) coleccionExistente.setTitulo(dto.getTitulo());
        if (dto.getDescripcion() != null) coleccionExistente.setDescripcion(dto.getDescripcion());

        if (dto.getCriteriosDePertenencia() != null) {
            List<Criterio> nuevosCriterios = mapCriteriosToEntity(dto.getCriteriosDePertenencia());
            coleccionExistente.setCriteriosDePertenencia(nuevosCriterios);
            criteriosCambiaron = true;
        }

        if (criteriosCambiaron) {
            List<Hecho> nuevosHechos = hechoRepositorio.buscarHechos(coleccionExistente.getCriteriosDePertenencia());
            coleccionExistente.setHechos(nuevosHechos);
        }

        coleccionRepositorio.guardar(coleccionExistente);
        return convertirADTO(coleccionExistente);
    }

    public void eliminarColeccion(UUID id) {
        Coleccion coleccion = coleccionRepositorio.buscarPorHandle(id.toString());
        if (coleccion == null) throw new IllegalArgumentException("Colección no encontrada");
        coleccionRepositorio.eliminar(coleccion);
    }

    public ColeccionDTO agregarFuente(UUID coleccionId, FuenteDTO fuenteDto) {
        Fuente fuente = mapFuenteToEntity(fuenteDto);

        Coleccion coleccion = coleccionRepositorio.buscarPorHandle(coleccionId.toString());
        if (coleccion == null) throw new IllegalArgumentException("Colección no encontrada");

        if(fuente != null) {
            coleccion.agregarFuente(fuente);
            coleccionRepositorio.guardar(coleccion);
        }
        return convertirADTO(coleccion);
    }

    public ColeccionDTO eliminarFuente(UUID coleccionId, Fuente fuente) {
        Coleccion coleccion = coleccionRepositorio.buscarPorHandle(coleccionId.toString());
        if (coleccion == null) throw new IllegalArgumentException("Colección no encontrada");

        coleccion.eliminarFuente(fuente);
        coleccionRepositorio.guardar(coleccion);

        return convertirADTO(coleccion);
    }

    public ColeccionDTO actualizarAlgoritmoConsenso(UUID coleccionId, TipoAlgoritmoConsenso algoritmo) {
        Coleccion coleccion = coleccionRepositorio.buscarPorHandle(coleccionId.toString());
        if (coleccion == null) throw new IllegalArgumentException("Colección no encontrada");

        coleccion.setAlgoritmoDeConsenso(algoritmo);
        coleccionRepositorio.guardar(coleccion);

        return convertirADTO(coleccion);
    }

    public List<ColeccionDTO> obtenerTodasLasColecciones() {
        return coleccionRepositorio.obtenerTodas().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public ColeccionDTO obtenerColeccionPorId(UUID id) {
        Coleccion coleccion = coleccionRepositorio.buscarPorHandle(id.toString());
        if (coleccion == null) throw new IllegalArgumentException("Colección no encontrada");
        return convertirADTO(coleccion);
    }

    private ColeccionDTO convertirADTO(Coleccion coleccion) {
        ColeccionDTO dto = new ColeccionDTO();

        dto.setColeccionId(coleccion.getHandle());
        dto.setTitulo(coleccion.getTitulo());
        dto.setDescripcion(coleccion.getDescripcion());

        dto.setFuentes(mapFuentesToDTO(coleccion.getFuente()));
        dto.setCriteriosDePertenencia(mapCriteriosToDTO(coleccion.getCriteriosDePertenencia()));

        if (coleccion.getAlgoritmoDeConsenso() != null) {
            dto.setAlgoritmoDeConsenso(TipoAlgoritmoConsensoDTO.valueOf(coleccion.getAlgoritmoDeConsenso().name()));
        }

        return dto;
    }

    private List<Fuente> mapFuentesToEntity(List<FuenteDTO> dtos) {
        if (dtos == null) return new ArrayList<>();

        return dtos.stream().map(dto -> {
            Fuente fuente = new Fuente();
            if (dto.getFuenteId() != null) {
                fuente.setId(dto.getFuenteId());
            }

            fuente.setDescriptor(dto.getDescriptor());

            if (dto.getTipoFuente() != null) {
                try {
                    fuente.setTipoDeFuente(TipoDeFuente.valueOf(dto.getTipoFuente()));
                } catch (IllegalArgumentException e) {
                    System.err.println("Tipo de fuente inválido: " + dto.getTipoFuente());
                }
            }
            return fuente;
        }).collect(Collectors.toList());
    }

    private List<FuenteDTO> mapFuentesToDTO(List<Fuente> entidades) {
        if (entidades == null) return new ArrayList<>();

        return entidades.stream().map(f -> {
            FuenteDTO dto = new FuenteDTO();
            dto.setFuenteId(f.getId());
            dto.setDescriptor(f.getDescriptor());
            if (f.getTipoDeFuente() != null) {
                dto.setTipoFuente(f.getTipoDeFuente().name());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    private Fuente mapFuenteToEntity(FuenteDTO dto) {
        if (dto == null) return null;
        Fuente fuente = new Fuente();
        if (dto.getFuenteId() != null) {
            fuente.setId(dto.getFuenteId());
        }

        fuente.setDescriptor(dto.getDescriptor());
        if (dto.getTipoFuente() != null) {
            try {
                fuente.setTipoDeFuente(DominioGestorAdministrativo.fuente.TipoDeFuente.valueOf(dto.getTipoFuente()));
            } catch (IllegalArgumentException e) {
                System.err.println("❌ Error: El tipo de fuente '" + dto.getTipoFuente() + "' no existe en el Enum.");
            }
        }

        return fuente;
    }

    private List<Criterio> mapCriteriosToEntity(List<CriterioDTO> dtos) {
        if (dtos == null) return new ArrayList<>();
        List<Criterio> listaEntidades = new ArrayList<>();

        for (CriterioDTO dto : dtos) {
            Criterio entidad = null;

            if (dto instanceof CriterioDeTextoDTO) {
                CriterioDeTextoDTO textoDTO = (CriterioDeTextoDTO) dto;
                entidad = new CriterioDeTexto(
                        textoDTO.getPalabras(),
                        convertirTipoTextoEnum(textoDTO.getTipoDeTexto())
                );
            }
            else if (dto instanceof CriterioFechaDTO) {
                CriterioFechaDTO fechaDTO = (CriterioFechaDTO) dto;
                entidad = new CriterioFecha(
                        fechaDTO.getFechaInicio(),
                        fechaDTO.getFechaFin(),
                        fechaDTO.getTipoFecha()
                );
            }
            else if (dto instanceof CriterioEtiquetasDTO) {
                CriterioEtiquetasDTO etiquetasDTO = (CriterioEtiquetasDTO) dto;
                List<Etiqueta> etiquetasEntidad = mapEtiquetasToEntity(etiquetasDTO.getEtiquetas());
                entidad = new CriterioEtiquetas(etiquetasEntidad);
            }
            else if (dto instanceof CriterioUbicacionDTO) {
                CriterioUbicacionDTO ubicacionDTO = (CriterioUbicacionDTO) dto;
                Ubicacion ubicacionEntidad = mapUbicacionToEntity(ubicacionDTO.getUbicacion());
                entidad = new CriterioUbicacion(ubicacionEntidad);
            }
            else if (dto instanceof CriterioTipoFuenteDTO) {
                CriterioTipoFuenteDTO fuenteDTO = (CriterioTipoFuenteDTO) dto;
                entidad = new CriterioTipoFuente(
                        convertirTipoFuenteEnum(fuenteDTO.getFuente())
                );
            }
            else if (dto instanceof CriterioTipoMultimediaDTO) {
                CriterioTipoMultimediaDTO multiDTO = (CriterioTipoMultimediaDTO) dto;
                entidad = new CriterioTipoMultimedia(
                        convertirTipoMultimediaEnum(multiDTO.getTipoContenidoMultimedia())
                );
            }

            if (entidad != null) {
                listaEntidades.add(entidad);
            }
        }
        return listaEntidades;
    }

    private List<CriterioDTO> mapCriteriosToDTO(List<Criterio> entidades) {
        if (entidades == null) return new ArrayList<>();
        List<CriterioDTO> listaDTOs = new ArrayList<>();

        for (Criterio entidad : entidades) {
            CriterioDTO dto = null;

            if (entidad instanceof CriterioDeTexto) {
                CriterioDeTexto e = (CriterioDeTexto) entidad;
                dto = new CriterioDeTextoDTO(e.getId(), e.getPalabras(), convertirTipoTextoDTO(e.getTipoDeTexto()));
            }
            else if (entidad instanceof CriterioFecha) {
                CriterioFecha e = (CriterioFecha) entidad;
                dto = new CriterioFechaDTO(e.getId(), e.getFechaInicio(), e.getFechaFin(), e.getTipoFecha());
            }
            else if (entidad instanceof CriterioEtiquetas) {
                CriterioEtiquetas e = (CriterioEtiquetas) entidad;
                dto = new CriterioEtiquetasDTO(e.getId(), mapEtiquetasToDTO(e.getEtiquetas()));
            }
            else if (entidad instanceof CriterioUbicacion) {
                CriterioUbicacion e = (CriterioUbicacion) entidad;
                dto = new CriterioUbicacionDTO(e.getId(), mapUbicacionToDTO(e.getUbicacion()));
            }
            else if (entidad instanceof CriterioTipoFuente) {
                CriterioTipoFuente e = (CriterioTipoFuente) entidad;
                dto = new CriterioTipoFuenteDTO(e.getId(), convertirTipoFuenteDTO(e.getFuente()));
            }
            else if (entidad instanceof CriterioTipoMultimedia) {
                CriterioTipoMultimedia e = (CriterioTipoMultimedia) entidad;
                dto = new CriterioTipoMultimediaDTO(e.getId(), convertirTipoMultimediaDTO(e.getTipoContenidoMultimedia()));
            }
            else if (entidad instanceof CriterioContribuyente) {
                CriterioContribuyente e = (CriterioContribuyente) entidad;
                dto = new CriterioContribuyenteDTO(e.getId(), e.getNombreContribuyente());
            }

            if (dto != null) {
                listaDTOs.add(dto);
            }
        }
        return listaDTOs;
    }

    private List<Etiqueta> mapEtiquetasToEntity(List<EtiquetaDTO> dtos) {
        if (dtos == null) return new ArrayList<>();
        return dtos.stream().map(d -> {
            Etiqueta e = new Etiqueta();
            if(d.getId() != null) e.setId(d.getId());
            e.setNombre(d.getNombre());
            return e;
        }).collect(Collectors.toList());
    }

    private List<EtiquetaDTO> mapEtiquetasToDTO(List<Etiqueta> entidades) {
        if (entidades == null) return new ArrayList<>();
        return entidades.stream().map(e -> {
            EtiquetaDTO d = new EtiquetaDTO();
            d.setId(e.getId());
            d.setNombre(e.getNombre());
            return d;
        }).collect(Collectors.toList());
    }

    private Ubicacion mapUbicacionToEntity(UbicacionDTO dto) {
        if (dto == null) return null;
        Ubicacion u = new Ubicacion();
        if (dto.getUbicacionId() != null) {
            u.setId_ubicacion(dto.getUbicacionId());
        }
        u.setLatitud(dto.getLatitud());
        u.setLongitud(dto.getLongitud());
        return u;
    }

    private UbicacionDTO mapUbicacionToDTO(Ubicacion entidad) {
        if (entidad == null) return null;
        UbicacionDTO dto = new UbicacionDTO();
        dto.setUbicacionId(entidad.getId_ubicacion());
        dto.setLatitud(entidad.getLatitud());
        dto.setLongitud(entidad.getLongitud());
        return dto;
    }

    private DominioGestorAdministrativo.Criterios.TipoDeTexto convertirTipoTextoEnum(DominioGestorAdministrativo.DTO.Criterios.TipoDeTextoDTO dtoEnum) {
        return dtoEnum != null ? DominioGestorAdministrativo.Criterios.TipoDeTexto.valueOf(dtoEnum.name()) : null;
    }
    private DominioGestorAdministrativo.DTO.Criterios.TipoDeTextoDTO convertirTipoTextoDTO(DominioGestorAdministrativo.Criterios.TipoDeTexto domainEnum) {
        return domainEnum != null ? DominioGestorAdministrativo.DTO.Criterios.TipoDeTextoDTO.valueOf(domainEnum.name()) : null;
    }

    private DominioGestorAdministrativo.fuente.TipoDeFuente convertirTipoFuenteEnum(DominioGestorAdministrativo.DTO.Criterios.TipoDeFuenteDTO dtoEnum) {
        return dtoEnum != null ? DominioGestorAdministrativo.fuente.TipoDeFuente.valueOf(dtoEnum.name()) : null;
    }
    private DominioGestorAdministrativo.DTO.Criterios.TipoDeFuenteDTO convertirTipoFuenteDTO(DominioGestorAdministrativo.fuente.TipoDeFuente domainEnum) {
        return domainEnum != null ? DominioGestorAdministrativo.DTO.Criterios.TipoDeFuenteDTO.valueOf(domainEnum.name()) : null;
    }

    private DominioGestorAdministrativo.HechosYColecciones.TipoContenidoMultimedia convertirTipoMultimediaEnum(DominioGestorAdministrativo.DTO.Hechos.TipoContenidoMultimediaDTO dtoEnum) {
        return dtoEnum != null ? DominioGestorAdministrativo.HechosYColecciones.TipoContenidoMultimedia.valueOf(dtoEnum.name()) : null;
    }
    private DominioGestorAdministrativo.DTO.Hechos.TipoContenidoMultimediaDTO convertirTipoMultimediaDTO(DominioGestorAdministrativo.HechosYColecciones.TipoContenidoMultimedia domainEnum) {
        return domainEnum != null ? DominioGestorAdministrativo.DTO.Hechos.TipoContenidoMultimediaDTO.valueOf(domainEnum.name()) : null;
    }
    private DominioGestorAdministrativo.HechosYColecciones.TipoAlgoritmoConsenso convertirAlgoritmo(DominioGestorAdministrativo.DTO.Coleccion.TipoAlgoritmoConsensoDTO dtoEnum) {
        if (dtoEnum == null) return null;
        return DominioGestorAdministrativo.HechosYColecciones.TipoAlgoritmoConsenso.valueOf(dtoEnum.name());
    }
}