package utils.DTO.HechosYColecciones;

import java.util.UUID;

public class Etiqueta {
    private UUID id_etiqueta;
    private String nombre;

    public Etiqueta() {}

    public Etiqueta(String nombre) {
        this.nombre = nombre;
    }

    public UUID getEtiqueta_id() { return id_etiqueta; }
    public void setEtiqueta_id(UUID nuevoEtiqueta_id) { this.id_etiqueta = nuevoEtiqueta_id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nuevoNombre) { this.nombre = nuevoNombre; }
}
