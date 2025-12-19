package agregador.graphQl.dtoGraphQl;

import agregador.domain.HechosYColecciones.Coleccion;
import agregador.dto.Coleccion.TipoAlgoritmoConsensoDTO;
import agregador.dto.Criterios.CriterioDTO;
import agregador.dto.Hechos.FuenteDTO;
import agregador.dto.Hechos.HechoDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ColeccionGraphDTO {
    private UUID coleccionId;
    private String titulo;
    private String descripcion;
    private List<FuenteDTO> fuentes = new ArrayList<>();
    private List<CriterioDTO> criteriosDePertenencia = new ArrayList<>();
    private TipoAlgoritmoConsensoDTO algoritmoDeConsenso;
    private List<HechoDTO> hechos = new ArrayList<>();
    private List<HechoDTO> hechosConsensuados = new ArrayList<>();

    public ColeccionGraphDTO() {}

    public ColeccionGraphDTO(String titulo, String descripcion, List<FuenteDTO> fuentes,
                        List<CriterioDTO> criteriosDePertenencia, TipoAlgoritmoConsensoDTO algoritmoDeConsenso) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fuentes = fuentes;
        this.criteriosDePertenencia = criteriosDePertenencia;
        this.algoritmoDeConsenso = algoritmoDeConsenso;
    }

    public ColeccionGraphDTO(Coleccion coleccion) {
        if (coleccion == null) return;

        this.coleccionId = coleccion.getHandle();
        this.titulo = coleccion.getTitulo();
        this.descripcion = coleccion.getDescripcion();

        // Mapeo de Algoritmo (Enum)
        if (coleccion.getAlgoritmoDeConsenso() != null) {
            try {
                this.algoritmoDeConsenso = TipoAlgoritmoConsensoDTO.valueOf(coleccion.getAlgoritmoDeConsenso().name());
            } catch (Exception e) {

            }
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

    public List<HechoDTO> getHechos() {
        return hechos;
    }

    public List<HechoDTO> getHechosConsensuados() {
        return hechosConsensuados;
    }

    public String getId() {
        return coleccionId != null ? coleccionId.toString() : null;
    }
}