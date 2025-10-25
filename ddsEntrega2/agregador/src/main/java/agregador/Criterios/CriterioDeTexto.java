package agregador.Criterios;

import agregador.HechosYColecciones.Hecho;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CriterioDeTexto extends Criterio {

    private TipoDeTexto tipoDeTexto;

    @ElementCollection
    private List<String> palabras = new ArrayList<String>();

    public CriterioDeTexto(List<String> palabras) {
        this.palabras = palabras;
    }

    public CriterioDeTexto() {}

    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        // Devuelve true si el hecho contiene al menos una de las palabras buscadas
        switch (tipoDeTexto) {
            case TITULO:
                for (String palabra : palabras) {
                    if(hecho.getTitulo().contains(palabra)){
                        return true;
                    }
                }
                return false;
            case DESCRIPCION:
                for (String palabra : palabras) {
                    if(hecho.getDescripcion().contains(palabra)){
                        return true;
                    }
                }
                return false;
            case CATEGORIA:
                for (String palabra : palabras) {
                    if(hecho.getCategoria().contains(palabra)){
                        return true;
                    }
                }
                return false;
            default:
                return palabras.stream().anyMatch(texto ->
                        hecho.getTitulo().toLowerCase().contains(texto.toLowerCase()) ||
                                hecho.getDescripcion().toLowerCase().contains(texto.toLowerCase()));
        }

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
        StringBuilder retorno = new StringBuilder("( 1=1");
        switch (tipoDeTexto) {
            case TITULO:
                for (String palabra : palabras) {
                    retorno.append(" or h.titulo like '%").append(palabra).append("%'");
                }
            case DESCRIPCION:
                for (String palabra : palabras) {
                    retorno.append(" or h.descripcion like '%").append(palabra).append("%'");
                }
            case CATEGORIA:
                for (String palabra : palabras) {
                    retorno.append(" or h.categoria like '%").append(palabra).append("%'");
                }
                default:
                    retorno = new StringBuilder("(1=1");
        }

        return retorno.toString() + ")";
    }
}
//toLowerCase() compara letras sin importar si es mayus o minus
