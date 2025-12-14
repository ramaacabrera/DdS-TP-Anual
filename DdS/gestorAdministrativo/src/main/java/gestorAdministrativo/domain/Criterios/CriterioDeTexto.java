package gestorAdministrativo.domain.Criterios;

import gestorAdministrativo.domain.HechosYColecciones.Hecho;

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
                    if (contieneTexto(hecho.getTitulo(), palabra)) {
                        return true;
                    }
                }
                return false;

            case DESCRIPCION:
                for (String palabra : palabras) {
                    if (contieneTexto(hecho.getDescripcion(), palabra)) {
                        return true;
                    }
                }
                return false;

            case CATEGORIA:
                for (String palabra : palabras) {
                    if (contieneTexto(hecho.getCategoria(), palabra)) {
                        return true;
                    }
                }
                return false;

            case BUSQUEDA:
                for (String palabra : palabras) {
                    if (contieneTexto(hecho.getTitulo(), palabra) ||
                            contieneTexto(hecho.getDescripcion(), palabra) ||
                            contieneTexto(hecho.getCategoria(), palabra)) {
                        return true;
                    }
                }
                return false;

            default:
                return palabras.stream().anyMatch(texto ->
                        contieneTexto(hecho.getTitulo(), texto) ||
                                contieneTexto(hecho.getDescripcion(), texto));
        }
    }

    private boolean contieneTexto(String textoBase, String textoBusqueda) {
        if (textoBase == null || textoBusqueda == null) return false;

        return textoBase.toLowerCase().contains(textoBusqueda.toLowerCase());
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
                    conditions.add("LOWER(h.titulo) LIKE LOWER(CONCAT('%', :titulo_" + i + ", '%'))");
                }
                break;

            case DESCRIPCION:
                for (int i = 0; i < palabras.size(); i++) {
                    conditions.add("LOWER(h.descripcion) LIKE LOWER(CONCAT('%', :descripcion_" + i + ", '%'))");
                }
                break;

            case CATEGORIA:
                for (int i = 0; i < palabras.size(); i++) {
                    conditions.add("LOWER(h.categoria) LIKE LOWER(CONCAT('%', :categoria_" + i + ", '%'))");
                }
                break;

            case BUSQUEDA:
                for (int i = 0; i < palabras.size(); i++) {
                    conditions.add("(" +
                            "LOWER(h.titulo) LIKE LOWER(CONCAT('%', :busqueda_" + i + ", '%')) OR " +
                            "LOWER(h.descripcion) LIKE LOWER(CONCAT('%', :busqueda_" + i + ", '%')) OR " +
                            "LOWER(h.categoria) LIKE LOWER(CONCAT('%', :busqueda_" + i + ", '%'))" +
                            ")");
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
                    params.put("titulo_" + i, palabras.get(i));
                }
                break;

            case DESCRIPCION:
                for (int i = 0; i < palabras.size(); i++) {
                    params.put("descripcion_" + i, palabras.get(i));
                }
                break;

            case CATEGORIA:
                for (int i = 0; i < palabras.size(); i++) {
                    params.put("categoria_" + i, palabras.get(i));
                }
                break;

            case BUSQUEDA:
                for (int i = 0; i < palabras.size(); i++) {
                    params.put("busqueda_" + i, palabras.get(i));
                }
                break;
        }

        return params;
    }
}