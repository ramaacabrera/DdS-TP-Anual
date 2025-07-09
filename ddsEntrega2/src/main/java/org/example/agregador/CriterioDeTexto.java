package org.example.agregador;

import java.util.ArrayList;
import java.util.List;

public class CriterioDeTexto extends Criterio {

    private TipoDeTexto tipoDeTexto;

    private final List<String> palabras;

    public CriterioDeTexto(List<String> palabras) {
        this.palabras = palabras;
    }

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
                return false;
        }
        /*return palabras.stream().anyMatch(texto ->
                hecho.getTitulo().toLowerCase().contains(texto.toLowerCase()) ||
                        hecho.getDescripcion().toLowerCase().contains(texto.toLowerCase())
                        );*/
    }
}
//toLowerCase() compara letras sin importar si es mayus o minus
