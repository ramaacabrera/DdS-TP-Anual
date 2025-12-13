package agregador.graphQl.dtoGraphQl;

import agregador.domain.HechosYColecciones.Hecho;

import java.util.List;

public class PageHechoDTO {
    private List<Hecho> content;
    private int page;
    private int size;
    private int totalPages;
    private int totalElements;

    public static PageHechoDTO from(List<Hecho> content, int page, int size, int totalElements) {

        PageHechoDTO dto = new PageHechoDTO();
        dto.content = content;
        dto.page = page;
        dto.size = size;
        dto.totalElements = totalElements;
        dto.totalPages = (int) Math.ceil((double) totalElements / size);

        return dto;
    }

    // Getters
    public List<Hecho> getContent() { return content; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public int getTotalPages() { return totalPages; }
    public int getTotalElements() { return totalElements; }
}
