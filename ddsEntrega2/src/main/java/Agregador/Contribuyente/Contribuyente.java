package Agregador.Contribuyente;

import Agregador.HechosYColecciones.Hecho;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Contribuyente {
    @JsonIgnore
    private List<Hecho> hechosSubidos = new ArrayList<Hecho>();
    @JsonProperty
    private Integer edad;
    @JsonProperty
    private String nombre;
    @JsonProperty
    private String apellido;
    public void Contribuyente() {}

    public void VerificarMayoriaDeEdad() {}

    public void hechoSubido(Hecho hecho){
        hechosSubidos.add(hecho);
    }

    public Integer getEdad() { return edad; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }

    public void setEdad(Integer edad) { this.edad = edad; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
}
