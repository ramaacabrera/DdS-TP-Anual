package gestorPublico.dto.Coleccion;

import gestorPublico.domain.Criterios.*;
import gestorPublico.domain.HechosYColecciones.Coleccion; // Importar la entidad
import gestorPublico.domain.HechosYColecciones.Hecho;
import gestorPublico.domain.HechosYColecciones.TipoAlgoritmoConsenso;
import gestorPublico.dto.Criterios.*;
import gestorPublico.dto.Hechos.FuenteDTO;
import gestorPublico.dto.Hechos.HechoDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ColeccionDTO {
    private UUID handle;
    private String titulo;
    private String descripcion;
    private List<FuenteDTO> fuente = new ArrayList<>();
    private List<CriterioDTO> criteriosDePertenencia = new ArrayList<>();
    private TipoAlgoritmoConsensoDTO algoritmoDeConsenso;
    private List<HechoDTO> hechos = new ArrayList<>();

    public ColeccionDTO() {}

    public ColeccionDTO(String titulo, String descripcion, List<FuenteDTO> fuentes,
                        List<CriterioDTO> criteriosDePertenencia, TipoAlgoritmoConsensoDTO algoritmoDeConsenso) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fuente = fuentes;
        this.criteriosDePertenencia = criteriosDePertenencia;
        this.algoritmoDeConsenso = algoritmoDeConsenso;
    }

    public ColeccionDTO(Coleccion coleccion) {
        if (coleccion == null) return;

        this.handle = coleccion.getHandle();
        this.titulo = coleccion.getTitulo();
        this.descripcion = coleccion.getDescripcion();

        // Mapeo de Algoritmo (Enum)
        if (coleccion.getAlgoritmoDeConsenso() != null) {
            try {
                this.algoritmoDeConsenso = this.convertirAlgoritmo(coleccion.getAlgoritmoDeConsenso());
            } catch (Exception e) {
                // Si no coincide el nombre, se deja null
            }
        }

        // Mapeo de Fuentes
        if (coleccion.getFuente() != null) {
            this.fuente = coleccion.getFuente().stream().map(f -> {
                FuenteDTO dto = new FuenteDTO();
                dto.setId(f.getId());
                dto.setDescriptor(f.getDescriptor());
                if (f.getTipoDeFuente() != null) {
                    dto.setTipoDeFuente(f.getTipoDeFuente().name());
                }
                return dto;
            }).collect(Collectors.toList());
        }

        this.hechos = convertirHechos(coleccion.getHechos());

        this.criteriosDePertenencia = convertirDTO(coleccion.getCriteriosDePertenencia());
    }

    private List<HechoDTO> convertirHechos(List<Hecho> hechos){
        List<HechoDTO> dto = new ArrayList<>();
        for(Hecho h : hechos){
            HechoDTO hdto = new HechoDTO(h);
            dto.add(hdto);
        }
        return dto;
    }

    private TipoAlgoritmoConsensoDTO convertirAlgoritmo(TipoAlgoritmoConsenso t){
        switch (t){
            case ABSOLUTA:
                return TipoAlgoritmoConsensoDTO.ABSOLUTA;
            case MAYORIA_SIMPLE:
                return TipoAlgoritmoConsensoDTO.MAYORIA_SIMPLE;
            case MULTIPLES_MENCIONES:
                return TipoAlgoritmoConsensoDTO.MULTIPLES_MENCIONES;
            default:
                return null;
        }
    }

    private List<CriterioDTO> convertirDTO(List<Criterio> criterios) {
        return criterios.stream()
                .map(this::convertirUno)
                .collect(Collectors.toList());
    }


    public CriterioDTO convertirUno(Criterio criterio){
        if (criterio instanceof CriterioDeTexto)
            return new CriterioDeTextoDTO((CriterioDeTexto) criterio);


        if (criterio instanceof CriterioContribuyente)
            return new CriterioContribuyenteDTO((CriterioContribuyente) criterio);


        if (criterio instanceof CriterioEtiquetas)
            return new CriterioEtiquetasDTO((CriterioEtiquetas) criterio);


        if (criterio instanceof CriterioFecha)
            return new CriterioFechaDTO((CriterioFecha) criterio);


        if (criterio instanceof CriterioTipoFuente)
            return new CriterioTipoFuenteDTO((CriterioTipoFuente) criterio);


        if (criterio instanceof CriterioTipoMultimedia)
            return new CriterioTipoMultimediaDTO((CriterioTipoMultimedia) criterio);


        if (criterio instanceof CriterioUbicacion)
            return new CriterioUbicacionDTO((CriterioUbicacion) criterio);


        throw new IllegalArgumentException("Tipo de criterio desconocido: " + criterio.getClass());
    }


    // Getters y Setters
    public UUID getHandle() { return handle; }
    public void setHandle(UUID coleccionId) { this.handle = coleccionId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public List<FuenteDTO> getFuente() { return fuente; }
    public void setFuente(List<FuenteDTO> fuentes) { this.fuente = fuentes; }

    public List<HechoDTO> getHechos(){return hechos;}
    public void setHechos(List<HechoDTO> hechos){this.hechos = hechos;}

    public List<CriterioDTO> getCriteriosDePertenencia() { return criteriosDePertenencia; }
    public void setCriteriosDePertenencia(List<CriterioDTO> criteriosDePertenencia) {
        this.criteriosDePertenencia = criteriosDePertenencia;
    }

    public TipoAlgoritmoConsensoDTO getAlgoritmoDeConsenso() { return algoritmoDeConsenso; }
    public void setAlgoritmoDeConsenso(TipoAlgoritmoConsensoDTO algoritmoDeConsenso) {
        this.algoritmoDeConsenso = algoritmoDeConsenso;
    }
}