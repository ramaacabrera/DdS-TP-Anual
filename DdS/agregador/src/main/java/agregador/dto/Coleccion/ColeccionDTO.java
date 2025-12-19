package agregador.dto.Coleccion;

import agregador.domain.HechosYColecciones.Coleccion;
import agregador.dto.Coleccion.TipoAlgoritmoConsensoDTO;
import agregador.dto.Criterios.CriterioDTO;
import agregador.dto.Hechos.FuenteDTO;
import agregador.dto.Hechos.HechoDTO;

import java.util.*;
import java.util.stream.Collectors;

public class ColeccionDTO {
    private UUID coleccionId;
    private String titulo;
    private String descripcion;
    private List<FuenteDTO> fuentes = new ArrayList<>();
    private List<CriterioDTO> criteriosDePertenencia = new ArrayList<>();
    private TipoAlgoritmoConsensoDTO algoritmoDeConsenso;
    private Set<HechoDTO> hechos = new HashSet<>();
    private Set<HechoDTO> hechosConsensuados = new HashSet<>();

    public ColeccionDTO() {}

    public ColeccionDTO(String titulo, String descripcion, List<FuenteDTO> fuentes,
                        List<CriterioDTO> criteriosDePertenencia, TipoAlgoritmoConsensoDTO algoritmoDeConsenso) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fuentes = fuentes;
        this.criteriosDePertenencia = criteriosDePertenencia;
        this.algoritmoDeConsenso = algoritmoDeConsenso;
    }

    public ColeccionDTO(Coleccion coleccion) {
        if (coleccion == null) return;

        this.coleccionId = coleccion.getHandle();
        this.titulo = coleccion.getTitulo();
        this.descripcion = coleccion.getDescripcion();

        // Mapeo de Algoritmo (Enum)
        if (coleccion.getAlgoritmoDeConsenso() != null) {
            try {
                this.algoritmoDeConsenso = TipoAlgoritmoConsensoDTO.valueOf(coleccion.getAlgoritmoDeConsenso().name());
            } catch (Exception e) {
                // Si no coincide el nombre, se deja null
            }
        }

        // Mapeo de Fuentes
        if (coleccion.getFuente() != null) {
            this.fuentes = coleccion.getFuente().stream().map(f -> {
                FuenteDTO dto = new FuenteDTO();
                dto.setFuenteId(f.getId());
                dto.setDescriptor(f.getDescriptor());
                if (f.getTipoDeFuente() != null) {
                    dto.setTipoDeFuente(f.getTipoDeFuente());
                }
                return dto;
            }).collect(Collectors.toList());
        }

        if (coleccion.getHechos() != null) {
            this.hechos = coleccion.getHechos().stream()
                    .map(HechoDTO::new)
                    .collect(Collectors.toSet());
        }

        if (coleccion.getHechosConsensuados() != null) {
            this.hechosConsensuados = coleccion.getHechosConsensuados().stream()
                    .map(HechoDTO::new)
                    .collect(Collectors.toSet());
        }

        this.criteriosDePertenencia = new ArrayList<>();
    }

    // Getters y Setters
    public UUID getColeccionId() { return coleccionId; }
    public void setColeccionId(UUID coleccionId) { this.coleccionId = coleccionId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public List<FuenteDTO> getFuentes() { return fuentes; }
    public void setFuentes(List<FuenteDTO> fuentes) { this.fuentes = fuentes; }

    public List<CriterioDTO> getCriteriosDePertenencia() { return criteriosDePertenencia; }
    public void setCriteriosDePertenencia(List<CriterioDTO> criteriosDePertenencia) {
        this.criteriosDePertenencia = criteriosDePertenencia;
    }

    public TipoAlgoritmoConsensoDTO getAlgoritmoDeConsenso() { return algoritmoDeConsenso; }
    public void setAlgoritmoDeConsenso(TipoAlgoritmoConsensoDTO algoritmoDeConsenso) {
        this.algoritmoDeConsenso = algoritmoDeConsenso;
    }

    public Set<HechoDTO> getHechos() {
        return hechos;
    }

    public Set<HechoDTO> getHechosConsensuados() {
        return hechosConsensuados;
    }

    public String getId() {
        return coleccionId != null ? coleccionId.toString() : null;
    }
}