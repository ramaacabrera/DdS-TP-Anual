package gestorAdministrativo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gestorAdministrativo.dto.Coleccion.ColeccionDTO;
import gestorAdministrativo.dto.Coleccion.TipoAlgoritmoConsensoDTO;
import gestorAdministrativo.dto.Criterios.*;
import gestorAdministrativo.dto.Hechos.EtiquetaDTO;
import gestorAdministrativo.dto.Hechos.FuenteDTO;
import gestorAdministrativo.dto.Hechos.UbicacionDTO;
import gestorAdministrativo.domain.Criterios.*;
import gestorAdministrativo.domain.HechosYColecciones.*;
import gestorAdministrativo.domain.fuente.Fuente;
import gestorAdministrativo.domain.fuente.TipoDeFuente;
import gestorAdministrativo.dto.Hechos.TipoContenidoMultimediaDTO;
import gestorAdministrativo.repository.ColeccionRepositorio;
import gestorAdministrativo.repository.HechoRepositorio;
import gestorAdministrativo.repository.FuenteRepositorio;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ColeccionService {
    private final ColeccionRepositorio coleccionRepositorio;
    private final HechoRepositorio hechoRepositorio;
    private final FuenteRepositorio fuenteRepositorio;

    public ColeccionService(ColeccionRepositorio coleccionRepositorio,
                            HechoRepositorio hechoRepositorio,
                            FuenteRepositorio fuenteRepositorio) {
        this.coleccionRepositorio = coleccionRepositorio;
        this.hechoRepositorio = hechoRepositorio;
        this.fuenteRepositorio = fuenteRepositorio;
    }

    public List<FuenteDTO> obtenerTodasLasFuentes() {
        List<Fuente> fuentes = fuenteRepositorio.obtenerTodas();
        return mapFuentesToDTO(fuentes);
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
        for(Criterio c : coleccion.getCriteriosDePertenencia()){
            if(c instanceof CriterioUbicacion){
                System.out.println("Ubicacion de la coleccion: " + ((CriterioUbicacion)c).getDescripcion());
            }
        }

        List<Fuente> fuentesReales = new ArrayList<>();

        if (dto.getFuentes() != null && !dto.getFuentes().isEmpty()) {
            for (FuenteDTO fDto : dto.getFuentes()) {
                if (fDto.getFuenteId() != null) {
                    Fuente fuenteReal = fuenteRepositorio.buscarPorId(fDto.getFuenteId());
                    if (fuenteReal != null) {
                        fuentesReales.add(fuenteReal);
                    } else {
                        System.err.println("Advertencia: Se intentó asociar una fuente inexistente ID: " + fDto.getFuenteId());
                    }
                }
            }

            coleccion.setFuente(fuentesReales);


            try {
                System.out.println("Fuentes reales: " + new ObjectMapper().writeValueAsString(fuentesReales));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            List<Hecho> hechosQueCumplen = hechoRepositorio.buscarHechos(coleccion.getCriteriosDePertenencia(), fuentesReales);
            coleccion.setHechos(hechosQueCumplen);

        } else {
            coleccion.setHechos(hechoRepositorio.buscarHechos(coleccion.getCriteriosDePertenencia(), null));
        }
        coleccionRepositorio.guardar(coleccion);

        return convertirADTO(coleccion);
    }

    public ColeccionDTO actualizarColeccion(UUID id, ColeccionDTO dto) {
        Coleccion coleccionExistente = coleccionRepositorio.buscarPorHandle(id.toString());

        System.out.println("Coleccion encontrada");

        for(Fuente fuente : coleccionExistente.getFuente()){
            System.out.println("Fuente encontrada: " + fuente.getId() + " " + fuente.getDescriptor() + " " + fuente.getTipoDeFuente());
        }

        if (coleccionExistente == null) {
            throw new IllegalArgumentException("Colección no encontrada con ID: " + id);
        }

        boolean criteriosCambiaron = false;

        if (dto.getTitulo() != null) coleccionExistente.setTitulo(dto.getTitulo());

        System.out.println("Coleccion service 1");

        if (dto.getDescripcion() != null) coleccionExistente.setDescripcion(dto.getDescripcion());

        System.out.println("Coleccion service 2");

        if (dto.getCriteriosDePertenencia() != null) {
            List<Criterio> nuevosCriterios = mapCriteriosToEntity(dto.getCriteriosDePertenencia());
            coleccionExistente.setCriteriosDePertenencia(nuevosCriterios);
            criteriosCambiaron = true;
        }

        if(dto.getFuentes() != null &&  !dto.getFuentes().isEmpty()) {

            List<Fuente> fuentes = new ArrayList<>();
            for (FuenteDTO fDto : dto.getFuentes()) {
                fuentes.add(fuenteRepositorio.buscarPorId(fDto.getFuenteId()));
            }

            coleccionExistente.setFuente(fuentes);
            criteriosCambiaron = true;
        }
        try {
            System.out.println("Coleccion fuentes actualizadas: " + new ObjectMapper().writeValueAsString(coleccionExistente.getFuente()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Coleccion service 3");

        if (criteriosCambiaron) {
            List<Hecho> nuevosHechos = hechoRepositorio.buscarHechos(coleccionExistente.getCriteriosDePertenencia(), coleccionExistente.getFuente());
            coleccionExistente.setHechos(nuevosHechos);
        }
        System.out.println("Coleccion service 4");

        coleccionExistente.setAlgoritmoDeConsenso(TipoAlgoritmoConsenso.valueOf(dto.getAlgoritmoDeConsenso().toString()));

        coleccionRepositorio.guardar(coleccionExistente);

        System.out.println("Coleccion service 5");
        return convertirADTO(coleccionExistente);
    }

    public void eliminarColeccion(UUID id) {
        Coleccion coleccion = coleccionRepositorio.buscarPorHandle(id.toString());
        if (coleccion == null) {
            System.out.println("Coleccion no encontrada");
            throw new IllegalArgumentException("Colección no encontrada");
        }
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

    private ColeccionDTO convertirADTO(Coleccion coleccion) {
        ColeccionDTO dto = new ColeccionDTO();

        System.out.println("ConversorDTO 1");

        dto.setColeccionId(coleccion.getHandle());
        System.out.println("ConversorDTO 2");
        dto.setTitulo(coleccion.getTitulo());
        System.out.println("ConversorDTO 3");
        dto.setDescripcion(coleccion.getDescripcion());
        System.out.println("ConversorDTO 4");
        dto.setFuentes(mapFuentesToDTO(coleccion.getFuente()));
        System.out.println("ConversorDTO 5");
        dto.setCriteriosDePertenencia(mapCriteriosToDTO(coleccion.getCriteriosDePertenencia()));
        System.out.println("ConversorDTO 6");
        if (coleccion.getAlgoritmoDeConsenso() != null) {
            dto.setAlgoritmoDeConsenso(TipoAlgoritmoConsensoDTO.valueOf(coleccion.getAlgoritmoDeConsenso().name()));
        }
        System.out.println("ConversorDTO 7");

        return dto;
    }

    private List<FuenteDTO> mapFuentesToDTO(List<Fuente> entidades) {
        System.out.println("Se estan mapeando");

        if (entidades == null) {
            System.out.println("No hay fuentes (es null)");
            return new ArrayList<>();
        }

        System.out.println("Mapeando fuentes: " + entidades.size());

        for(Fuente fuente : entidades){
            String nombreTipo = (fuente.getTipoDeFuente() != null) ? fuente.getTipoDeFuente().name() : "NULO";
            System.out.println("Fuente: " + nombreTipo + " - " + fuente.getDescriptor() + " - " + fuente.getId());
        }

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
                fuente.setTipoDeFuente(TipoDeFuente.valueOf(dto.getTipoFuente()));
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
                entidad = new CriterioUbicacion(ubicacionDTO.getDescripcion());
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
            else if(dto instanceof CriterioContribuyenteDTO){
                CriterioContribuyenteDTO contrDTO = (CriterioContribuyenteDTO) dto;
                entidad = new CriterioContribuyente(contrDTO.getNombreContribuyente());
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
                dto = new CriterioUbicacionDTO(e.getId(), e.getDescripcion());
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

    private TipoDeTexto convertirTipoTextoEnum(TipoDeTextoDTO dtoEnum) {
        return dtoEnum != null ? TipoDeTexto.valueOf(dtoEnum.name()) : null;
    }
    private TipoDeTextoDTO convertirTipoTextoDTO(TipoDeTexto domainEnum) {
        return domainEnum != null ? TipoDeTextoDTO.valueOf(domainEnum.name()) : null;
    }

    private TipoDeFuente convertirTipoFuenteEnum(TipoDeFuenteDTO dtoEnum) {
        return dtoEnum != null ? TipoDeFuente.valueOf(dtoEnum.name()) : null;
    }
    private TipoDeFuenteDTO convertirTipoFuenteDTO(TipoDeFuente domainEnum) {
        return domainEnum != null ? TipoDeFuenteDTO.valueOf(domainEnum.name()) : null;
    }

    private TipoContenidoMultimedia convertirTipoMultimediaEnum(TipoContenidoMultimediaDTO dtoEnum) {
        return dtoEnum != null ? TipoContenidoMultimedia.valueOf(dtoEnum.name()) : null;
    }
    private TipoContenidoMultimediaDTO convertirTipoMultimediaDTO(TipoContenidoMultimedia domainEnum) {
        return domainEnum != null ? TipoContenidoMultimediaDTO.valueOf(domainEnum.name()) : null;
    }
    private TipoAlgoritmoConsenso convertirAlgoritmo(TipoAlgoritmoConsensoDTO dtoEnum) {
        if (dtoEnum == null) return null;
        return TipoAlgoritmoConsenso.valueOf(dtoEnum.name());
    }
}