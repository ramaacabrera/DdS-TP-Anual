package agregador.graphQl.dtoGraphQl;

import agregador.domain.HechosYColecciones.Hecho;
import agregador.dto.Hechos.HechoDTO;

import java.util.List;

public class PageHechoDTO {
    private List<HechoDTO> content;
    private int page;
    private int size;
    private int totalPages;
    private int totalElements;

    public PageHechoDTO(List<HechoDTO> content,
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
    public List<HechoDTO> getContent() { return content; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public int getTotalPages() { return totalPages; }
    public int getTotalElements() { return totalElements; }
}
