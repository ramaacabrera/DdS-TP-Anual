package org.example.agregador.HechosYColecciones;

public class ContenidoMultimedia {
    private final TipoContenidoMultimedia tipoContenido;
    private String contenido;

    public ContenidoMultimedia(TipoContenidoMultimedia tipo, String contenido) {
        this.tipoContenido = tipo;
        this.contenido = contenido;
    }

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