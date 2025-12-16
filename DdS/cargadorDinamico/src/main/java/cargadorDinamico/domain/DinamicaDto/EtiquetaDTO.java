package cargadorDinamico.domain.DinamicaDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EtiquetaDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID etiquetaId;

    private String nombre;

    public EtiquetaDTO() {}

    public EtiquetaDTO(UUID etiquetaId, String nombre) {
        this.etiquetaId = etiquetaId;
        this.nombre = nombre;
    }

    // Getters y Setters
    public UUID getId() { return etiquetaId; }
    public void setId(UUID etiquetaId) { this.etiquetaId = etiquetaId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}