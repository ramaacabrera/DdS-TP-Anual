package agregador.graphQl.dtoGraphQl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HechoFiltroDTO {

    private String categoria;
    private String titulo;
    private String ubicacion;

    private String fechaAcontecimientoDesde;
    private String fechaAcontecimientoHasta;

    private String fechaCargaDesde;
    private String fechaCargaHasta;

    private String contribuyente;
    private String tipoDeFuente;

    private List<String> etiquetas;

    public HechoFiltroDTO() {}

    @SuppressWarnings("unchecked")
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

        dto.setFechaCargaDesde((String) filtroMap.get("fechaCargaDesde"));
        dto.setFechaCargaHasta((String) filtroMap.get("fechaCargaHasta"));

        dto.setContribuyente((String) filtroMap.get("contribuyente"));
        dto.setTipoDeFuente((String) filtroMap.get("tipoDeFuente"));

        // 🏷️ Etiquetas (IDs)
        if (filtroMap.get("etiquetas") != null) {
            List<String> nombres = (List<String>) filtroMap.get("etiquetas");
            dto.setEtiquetas(nombres);
        }

        return dto;
    }

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

    public String getFechaCargaDesde() { return fechaCargaDesde; }
    public void setFechaCargaDesde(String fechaCargaDesde) {
        this.fechaCargaDesde = fechaCargaDesde;
    }

    public String getFechaCargaHasta() { return fechaCargaHasta; }
    public void setFechaCargaHasta(String fechaCargaHasta) {
        this.fechaCargaHasta = fechaCargaHasta;
    }

    public String getContribuyente() { return contribuyente; }
    public void setContribuyente(String contribuyente) {
        this.contribuyente = contribuyente;
    }

    public String getTipoDeFuente() { return tipoDeFuente; }
    public void setTipoDeFuente(String tipoDeFuente) {
        this.tipoDeFuente = tipoDeFuente;
    }

    public List<String> getEtiquetas() { return etiquetas; }
    public void setEtiquetas(List<String> etiquetas) {
        this.etiquetas = etiquetas;
    }
}
