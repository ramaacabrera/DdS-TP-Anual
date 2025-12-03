package gestorPublico.dto.Coleccion;

import gestorPublico.domain.HechosYColecciones.Coleccion; // Importar la entidad
import gestorPublico.dto.Criterios.CriterioDTO;
import gestorPublico.dto.Hechos.FuenteDTO;

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
                this.algoritmoDeConsenso = TipoAlgoritmoConsensoDTO.valueOf(coleccion.getAlgoritmoDeConsenso().name());
            } catch (Exception e) {
                // Si no coincide el nombre, se deja null
            }
        }

        // Mapeo de Fuentes
        if (coleccion.getFuente() != null) {
            this.fuente = coleccion.getFuente().stream().map(f -> {
                FuenteDTO dto = new FuenteDTO();
                dto.setFuenteId(f.getId());
                dto.setDescriptor(f.getDescriptor());
                if (f.getTipoDeFuente() != null) {
                    dto.setTipoDeFuente(f.getTipoDeFuente().name());
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
    public UUID getHandle() { return handle; }
    public void setHandle(UUID coleccionId) { this.handle = coleccionId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public List<FuenteDTO> getFuente() { return fuente; }
    public void setFuente(List<FuenteDTO> fuentes) { this.fuente = fuentes; }

    public List<CriterioDTO> getCriteriosDePertenencia() { return criteriosDePertenencia; }
    public void setCriteriosDePertenencia(List<CriterioDTO> criteriosDePertenencia) {
        this.criteriosDePertenencia = criteriosDePertenencia;
    }

    public TipoAlgoritmoConsensoDTO getAlgoritmoDeConsenso() { return algoritmoDeConsenso; }
    public void setAlgoritmoDeConsenso(TipoAlgoritmoConsensoDTO algoritmoDeConsenso) {
        this.algoritmoDeConsenso = algoritmoDeConsenso;
    }
}