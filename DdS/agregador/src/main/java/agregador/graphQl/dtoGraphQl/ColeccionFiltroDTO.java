package agregador.graphQl.dtoGraphQl;

import java.util.Map;
public class ColeccionFiltroDTO {

    private String titulo;
    private String descripcion;
    private String algoritmoDeConsenso;
    private Boolean tieneAlgoritmo;
    private Integer minHechos;
    private Integer maxHechos;

    public ColeccionFiltroDTO() {}

    public static ColeccionFiltroDTO fromMap(Map<String, Object> map) {
        if (map == null) return new ColeccionFiltroDTO();

        ColeccionFiltroDTO dto = new ColeccionFiltroDTO();
        dto.setTitulo((String) map.get("titulo"));
        dto.setDescripcion((String) map.get("descripcion"));
        dto.setAlgoritmoDeConsenso((String) map.get("algoritmoDeConsenso"));
        dto.setTieneAlgoritmo((Boolean) map.get("tieneAlgoritmo"));

        if (map.get("minHechos") != null)
            dto.setMinHechos(((Number) map.get("minHechos")).intValue());

        if (map.get("maxHechos") != null)
            dto.setMaxHechos(((Number) map.get("maxHechos")).intValue());

        return dto;
    }

    // getters & setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getAlgoritmoDeConsenso() { return algoritmoDeConsenso; }
    public void setAlgoritmoDeConsenso(String algoritmoDeConsenso) {
        this.algoritmoDeConsenso = algoritmoDeConsenso;
    }

    public Boolean getTieneAlgoritmo() { return tieneAlgoritmo; }
    public void setTieneAlgoritmo(Boolean tieneAlgoritmo) {
        this.tieneAlgoritmo = tieneAlgoritmo;
    }

    public Integer getMinHechos() { return minHechos; }
    public void setMinHechos(Integer minHechos) { this.minHechos = minHechos; }

    public Integer getMaxHechos() { return maxHechos; }
    public void setMaxHechos(Integer maxHechos) { this.maxHechos = maxHechos; }
}