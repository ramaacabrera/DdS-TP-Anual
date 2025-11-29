package gestorAdministrativo.DTO.Coleccion;

import gestorAdministrativo.DTO.Criterios.CriterioDTO;
import gestorAdministrativo.DTO.Hechos.FuenteDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ColeccionDTO {
    private UUID coleccionId; // Se mapea al handle
    private String titulo;
    private String descripcion;
    private List<FuenteDTO> fuentes = new ArrayList<>();
    private List<CriterioDTO> criteriosDePertenencia = new ArrayList<>();
    private TipoAlgoritmoConsensoDTO algoritmoDeConsenso;

    public ColeccionDTO() {}

    public ColeccionDTO(String titulo, String descripcion, List<FuenteDTO> fuentes,
                        List<CriterioDTO> criteriosDePertenencia, TipoAlgoritmoConsensoDTO algoritmoDeConsenso) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fuentes = fuentes;
        this.criteriosDePertenencia = criteriosDePertenencia;
        this.algoritmoDeConsenso = algoritmoDeConsenso;
    }

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
}