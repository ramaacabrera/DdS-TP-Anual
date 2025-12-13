package agregador.graphQl.dtoGraphQl;

import java.util.Map;

public class PageRequestDTO {
    private int page;
    private int size;

    // Valores por defecto
    public PageRequestDTO() {
        this.page = 1;
        this.size = 10;
    }

    public static PageRequestDTO fromMap(Map<String, Object> pageMap) {
        if (pageMap == null) {
            return new PageRequestDTO();
        }

        PageRequestDTO dto = new PageRequestDTO();

        if (pageMap.get("page") != null) {
            dto.setPage(((Number) pageMap.get("page")).intValue());
        }

        if (pageMap.get("size") != null) {
            dto.setSize(((Number) pageMap.get("size")).intValue());
        }

        return dto;
    }

    // Getters y setters
    public int getPage() { return page; }
    public void setPage(int page) {
        this.page = Math.max(1, page); // Asegurar que sea al menos 1
    }

    public int getSize() { return size; }
    public void setSize(int size) {
        this.size = Math.max(1, Math.min(size, 100)); // Limitar entre 1 y 100
    }
}
