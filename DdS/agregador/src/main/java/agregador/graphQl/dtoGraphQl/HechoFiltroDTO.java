package agregador.graphQl.dtoGraphQl;

import java.util.Map;

public class HechoFiltroDTO {
    private String categoria;
    private String titulo;
    private String ubicacion;
    private String fechaAcontecimientoDesde;
    private String fechaAcontecimientoHasta;

    // Constructor vacío
    public HechoFiltroDTO() {}

    // Constructor desde Map (GraphQL arguments)
    public static HechoFiltroDTO fromMap(Map<String, Object> filtroMap) {
        if (filtroMap == null) {
            return new HechoFiltroDTO();
        }

        HechoFiltroDTO dto = new HechoFiltroDTO();
        dto.setCategoria((String) filtroMap.get("categoria"));
        dto.setTitulo((String) filtroMap.get("titulo"));
        dto.setUbicacion((String) filtroMap.get("ubicacion"));
        dto.setFechaAcontecimientoDesde((String) filtroMap.get("fechaAcontecimientoDesde"));
        dto.setFechaAcontecimientoHasta((String) filtroMap.get("fechaAcontecimientoHasta"));
        return dto;
    }

    // Getters y setters
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getFechaAcontecimientoDesde() { return fechaAcontecimientoDesde; }
    public void setFechaAcontecimientoDesde(String fechaAcontecimientoDesde) {
        this.fechaAcontecimientoDesde = fechaAcontecimientoDesde;
    }

    public String getFechaAcontecimientoHasta() { return fechaAcontecimientoHasta; }
    public void setFechaAcontecimientoHasta(String fechaAcontecimientoHasta) {
        this.fechaAcontecimientoHasta = fechaAcontecimientoHasta;
    }
}
