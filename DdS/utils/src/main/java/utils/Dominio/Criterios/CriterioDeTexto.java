package utils.Dominio.Criterios;

import utils.Dominio.HechosYColecciones.Hecho;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                case BUSQUEDA:
                    for (String palabra : palabras) {
                        if(hecho.getTitulo().contains(palabra) || hecho.getDescripcion().contains(palabra) || hecho.getCategoria().contains(palabra)){
                            return true;
                        }
                    }
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
            case BUSQUEDA:
                for(String palabra : palabras){
                    retorno.append(" or h.titulo like '%").append(palabra).append("%'").append(" or h.descripcion like '%").append(palabra).append("%'")
                            .append(" or h.categoria like '%").append(palabra).append("%'");
                }

            default:
                    retorno = new StringBuilder("(1=1");
        }

        return retorno.toString() + ")";
    }

    @Override
    @Transient
    public Map<String, Object> getQueryParameters() {
        return Map.of();
    }
}
//toLowerCase() compara letras sin importar si es mayus o minus
