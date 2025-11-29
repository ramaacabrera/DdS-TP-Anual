package cargadorEstatico.domain.criterios;

import cargadorEstatico.domain.hechosycolecciones.Hecho;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class CriterioDeTexto extends Criterio {

    private TipoDeTexto tipoDeTexto;

    @ElementCollection
    private List<String> palabras = new ArrayList<String>();

    public CriterioDeTexto(List<String> palabras, TipoDeTexto tipoDeTexto) {
        this.palabras = palabras;
        this.tipoDeTexto = tipoDeTexto;
    }

    public CriterioDeTexto() {}


    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        switch (tipoDeTexto) {
            case TITULO:
                for (String palabra : palabras) {
                    if(contienePalabraCompleta(hecho.getTitulo(), palabra)){
                        return true;
                    }
                }
                return false;
            case DESCRIPCION:
                for (String palabra : palabras) {
                    if(contienePalabraCompleta(hecho.getDescripcion(), palabra)){
                        return true;
                    }
                }
                return false;
            case CATEGORIA:
                for (String palabra : palabras) {
                    // Para categoría mantenemos contains (puede ser una sola palabra)
                    if(hecho.getCategoria().toLowerCase().contains(palabra.toLowerCase())){
                        return true;
                    }
                }
                return false;
            case BUSQUEDA:
                for (String palabra : palabras) {
                    if(contienePalabraCompleta(hecho.getTitulo(), palabra) ||
                            contienePalabraCompleta(hecho.getDescripcion(), palabra) ||
                            hecho.getCategoria().toLowerCase().contains(palabra.toLowerCase())){
                        return true;
                    }
                }
                return false;
            default:
                return palabras.stream().anyMatch(texto ->
                        contienePalabraCompleta(hecho.getTitulo(), texto) ||
                                contienePalabraCompleta(hecho.getDescripcion(), texto));
        }
    }

    // ← NUEVO METODO: Verifica si contiene la palabra completa
    private boolean contienePalabraCompleta(String texto, String palabra) {
        if (texto == null || palabra == null) return false;

        String textoLower = texto.toLowerCase();
        String palabraLower = palabra.toLowerCase();

        // Verificar si la palabra está al inicio, en medio o al final
        return textoLower.equals(palabraLower) ||                    // Exacto
                textoLower.startsWith(palabraLower + " ") ||          // Al inicio
                textoLower.contains(" " + palabraLower + " ") ||      // En medio
                textoLower.endsWith(" " + palabraLower);              // Al final
    }

    public TipoDeTexto getTipoDeTexto() {
        return tipoDeTexto;
    }

    public void setTipoDeTexto(TipoDeTexto tipoDeTexto) {
        this.tipoDeTexto = tipoDeTexto;
    }

    public List<String> getPalabras() {
        return palabras;
    }

    public void setPalabras(List<String> palabras) {
        this.palabras = palabras;
    }

    @Override
    public String getQueryCondition() {
        StringBuilder retorno = new StringBuilder();
        List<String> conditions = new ArrayList<>();

        switch (tipoDeTexto) {
            case TITULO:
                for (int i = 0; i < palabras.size(); i++) {
                    // ← CAMBIO: Buscar palabra completa con espacios alrededor
                    conditions.add("(LOWER(h.titulo) LIKE LOWER(:titulo_start_" + i + ") OR " +
                            "LOWER(h.titulo) LIKE LOWER(:titulo_middle_" + i + ") OR " +
                            "LOWER(h.titulo) LIKE LOWER(:titulo_end_" + i + "))");
                }
                break;
            case DESCRIPCION:
                for (int i = 0; i < palabras.size(); i++) {
                    // ← CAMBIO: Buscar palabra completa con espacios alrededor
                    conditions.add("(LOWER(h.descripcion) LIKE LOWER(:descripcion_start_" + i + ") OR " +
                            "LOWER(h.descripcion) LIKE LOWER(:descripcion_middle_" + i + ") OR " +
                            "LOWER(h.descripcion) LIKE LOWER(:descripcion_end_" + i + "))");
                }
                break;
            case CATEGORIA:
                for (int i = 0; i < palabras.size(); i++) {
                    // ← CAMBIO: Para categoría puede ser exacta o contener (depende de tu uso)
                    conditions.add("LOWER(h.categoria) LIKE LOWER(:categoria_" + i + ")");
                }
                break;
            case BUSQUEDA:
                for (int i = 0; i < palabras.size(); i++) {
                    // ← CAMBIO: Buscar palabra completa en todos los campos
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
    @Transient
    public Map<String, Object> getQueryParameters() {
        Map<String, Object> params = new HashMap<>();

        switch (tipoDeTexto) {
            case TITULO:
                for (int i = 0; i < palabras.size(); i++) {
                    String palabra = palabras.get(i);
                    // ← CAMBIO: Tres patrones para cubrir todos los casos
                    params.put("titulo_start_" + i, palabra + " %");    // Palabra al inicio
                    params.put("titulo_middle_" + i, "% " + palabra + " %"); // Palabra en medio
                    params.put("titulo_end_" + i, "% " + palabra);      // Palabra al final
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
                    // Para categoría mantenemos búsqueda parcial (puede ser una sola palabra)
                    params.put("categoria_" + i, "%" + palabras.get(i) + "%");
                }
                break;
            case BUSQUEDA:
                for (int i = 0; i < palabras.size(); i++) {
                    String palabra = palabras.get(i);
                    // Título - palabra completa
                    params.put("busqueda_titulo_start_" + i, palabra + " %");
                    params.put("busqueda_titulo_middle_" + i, "% " + palabra + " %");
                    params.put("busqueda_titulo_end_" + i, "% " + palabra);

                    // Descripción - palabra completa
                    params.put("busqueda_descripcion_start_" + i, palabra + " %");
                    params.put("busqueda_descripcion_middle_" + i, "% " + palabra + " %");
                    params.put("busqueda_descripcion_end_" + i, "% " + palabra);

                    // Categoría - búsqueda parcial
                    params.put("busqueda_categoria_" + i, "%" + palabra + "%");
                }
                break;
        }

        return params;
    }
}
//toLowerCase() compara letras sin importar si es mayus o minus
