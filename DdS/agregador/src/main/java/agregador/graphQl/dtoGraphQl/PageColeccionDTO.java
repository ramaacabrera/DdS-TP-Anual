package agregador.graphQl.dtoGraphQl;

import agregador.dto.Coleccion.ColeccionDTO;

import java.util.List;

public class PageColeccionDTO {

    private List<ColeccionDTO> content;
    private int page;
    private int size;
    private int totalPages;
    private int totalElements;

    public PageColeccionDTO(List<ColeccionDTO> content,
                            int page,
                            int size,
                            int totalPages,
                            int totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    // Getters
    public List<ColeccionDTO> getContent() { return content; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public int getTotalPages() { return totalPages; }
    public int getTotalElements() { return totalElements; }
}
