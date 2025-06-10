package org.example;

import java.util.ArrayList;
import java.util.List;

public class CriterioDeTexto extends Criterio {

    private List<String> palabras = new ArrayList<>();

    public CriterioDeTexto(List<String> palabras) {
        this.palabras = palabras;
    }

    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        // Devuelve true si el hecho contiene al menos una de las palabras buscadas
        return palabras.stream().anyMatch(texto ->
                hecho.getTitulo().toLowerCase().contains(texto.toLowerCase()) ||
                        hecho.getDescripcion().toLowerCase().contains(texto.toLowerCase()) ||
                        hecho.getCategoria().toLowerCase().contains(texto.toLowerCase())
                        );
    }
}
//toLowerCase() compara letras sin importar si es mayus o minus
