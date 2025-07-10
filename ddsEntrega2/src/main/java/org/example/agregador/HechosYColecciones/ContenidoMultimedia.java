package org.example.agregador.HechosYColecciones;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ContenidoMultimedia {
    private TipoContenidoMultimedia tipoContenido;
    private String contenido;

    @JsonCreator
    public ContenidoMultimedia(@JsonProperty("tipoContenido") TipoContenidoMultimedia tipo,
                               @JsonProperty("contenido") String contenido) {
        this.tipoContenido = tipo;
        this.contenido = contenido;
    }

    public ContenidoMultimedia(){}

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public TipoContenidoMultimedia getTipoContenido() {
        return tipoContenido;
    }
}