package gestorPublico.dto;

import java.util.List;

public class PageDTO<T> {
    public List<T> content;
    public int page, size, totalPages;
    public long totalElements;

    public PageDTO() {};

    public PageDTO(List<T> content, int page, int size, int totalPages, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
