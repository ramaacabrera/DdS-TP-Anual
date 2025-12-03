package agregador.dto.Coleccion;

import agregador.domain.HechosYColecciones.Coleccion;
import agregador.dto.Coleccion.TipoAlgoritmoConsensoDTO;
import agregador.dto.Criterios.CriterioDTO;
import agregador.dto.Hechos.FuenteDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ColeccionDTO {
    private UUID coleccionId;
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

        // NOTA: El mapeo de Criterios (Lista polimórfica) es complejo para hacerlo dentro del constructor del DTO
        // sin acoplar demasiadas clases. Por ahora inicializamos la lista vacía.
        // Si necesitas mostrar los criterios en el listado público, deberías usar un "Mapper" externo
        // o inyectar la lógica de conversión, pero para listados generales suele dejarse vacío por rendimiento.
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
}