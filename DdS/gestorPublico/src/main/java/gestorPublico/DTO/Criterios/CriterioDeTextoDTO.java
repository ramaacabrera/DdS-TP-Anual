package gestorPublico.DTO.Criterios;

import java.util.*;

public class CriterioDeTextoDTO extends CriterioDTO {
    private TipoDeTextoDTO tipoDeTexto;
    private List<String> palabras = new ArrayList<>();

    public CriterioDeTextoDTO() {
        super(null, "CRITERIO_TEXTO");
    }

    public CriterioDeTextoDTO(UUID criterioId, List<String> palabras, TipoDeTextoDTO tipoDeTexto) {
        super(criterioId, "CRITERIO_TEXTO");
        this.palabras = palabras;
        this.tipoDeTexto = tipoDeTexto;
    }

    // Getters y Setters
    public TipoDeTextoDTO getTipoDeTexto() { return tipoDeTexto; }
    public void setTipoDeTexto(TipoDeTextoDTO tipoDeTexto) { this.tipoDeTexto = tipoDeTexto; }

    public List<String> getPalabras() { return palabras; }
    public void setPalabras(List<String> palabras) { this.palabras = palabras; }

    @Override
    public String getQueryCondition() {
        StringBuilder retorno = new StringBuilder();
        List<String> conditions = new ArrayList<>();

        switch (tipoDeTexto) {
            case TITULO:
                for (int i = 0; i < palabras.size(); i++) {
                    conditions.add("(LOWER(h.titulo) LIKE LOWER(:titulo_start_" + i + ") OR " +
                            "LOWER(h.titulo) LIKE LOWER(:titulo_middle_" + i + ") OR " +
                            "LOWER(h.titulo) LIKE LOWER(:titulo_end_" + i + "))");
                }
                break;
            case DESCRIPCION:
                for (int i = 0; i < palabras.size(); i++) {
                    conditions.add("(LOWER(h.descripcion) LIKE LOWER(:descripcion_start_" + i + ") OR " +
                            "LOWER(h.descripcion) LIKE LOWER(:descripcion_middle_" + i + ") OR " +
                            "LOWER(h.descripcion) LIKE LOWER(:descripcion_end_" + i + "))");
                }
                break;
            case CATEGORIA:
                for (int i = 0; i < palabras.size(); i++) {
                    conditions.add("LOWER(h.categoria) LIKE LOWER(:categoria_" + i + ")");
                }
                break;
            case BUSQUEDA:
                for (int i = 0; i < palabras.size(); i++) {
                    conditions.add("((LOWER(h.titulo) LIKE LOWER(:busqueda_titulo_start_" + i + ") OR " +
                            "LOWER(h.titulo) LIKE LOWER(:busqueda_titulo_middle_" + i + ") OR " +
                            "LOWER(h.titulo) LIKE LOWER(:busqueda_titulo_end_" + i + ")) OR " +

                            "(LOWER(h.descripcion) LIKE LOWER(:busqueda_descripcion_start_" + i + ") OR " +
                            "LOWER(h.descripcion) LIKE LOWER(:busqueda_descripcion_middle_" + i + ") OR " +
                            "LOWER(h.descripcion) LIKE LOWER(:busqueda_descripcion_end_" + i + ")) OR " +

                            "LOWER(h.categoria) LIKE LOWER(:busqueda_categoria_" + i + "))");
                }
                break;
            default:
                return "(1=1)";
        }

        if (!conditions.isEmpty()) {
            retorno.append("(").append(String.join(" OR ", conditions)).append(")");
        } else {
            retorno.append("(1=1)");
        }

        return retorno.toString();
    }

    @Override
    public Map<String, Object> getQueryParameters() {
        Map<String, Object> params = new HashMap<>();

        switch (tipoDeTexto) {
            case TITULO:
                for (int i = 0; i < palabras.size(); i++) {
                    String palabra = palabras.get(i);
                    params.put("titulo_start_" + i, palabra + " %");
                    params.put("titulo_middle_" + i, "% " + palabra + " %");
                    params.put("titulo_end_" + i, "% " + palabra);
                }
                break;
            case DESCRIPCION:
                for (int i = 0; i < palabras.size(); i++) {
                    String palabra = palabras.get(i);
                    params.put("descripcion_start_" + i, palabra + " %");
                    params.put("descripcion_middle_" + i, "% " + palabra + " %");
                    params.put("descripcion_end_" + i, "% " + palabra);
                }
                break;
            case CATEGORIA:
                for (int i = 0; i < palabras.size(); i++) {
                    params.put("categoria_" + i, "%" + palabras.get(i) + "%");
                }
                break;
            case BUSQUEDA:
                for (int i = 0; i < palabras.size(); i++) {
                    String palabra = palabras.get(i);
                    params.put("busqueda_titulo_start_" + i, palabra + " %");
                    params.put("busqueda_titulo_middle_" + i, "% " + palabra + " %");
                    params.put("busqueda_titulo_end_" + i, "% " + palabra);

                    params.put("busqueda_descripcion_start_" + i, palabra + " %");
                    params.put("busqueda_descripcion_middle_" + i, "% " + palabra + " %");
                    params.put("busqueda_descripcion_end_" + i, "% " + palabra);

                    params.put("busqueda_categoria_" + i, "%" + palabra + "%");
                }
                break;
        }

        return params;
    }
}