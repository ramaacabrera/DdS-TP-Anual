package Agregador.Usuario;

import Agregador.HechosYColecciones.Hecho;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.*;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id_usuario", updatable = false, nullable = false)
    private UUID id_usuario;

    @OneToMany(mappedBy = "contribuyente")
    @JsonIgnore
    private List<Hecho> hechosSubidos = new ArrayList<Hecho>();

    @JsonProperty
    private Integer edad;
    @JsonProperty
    private String nombre;
    @JsonProperty
    private String apellido;
    public void Contribuyente() {}

    @Enumerated(EnumType.STRING)
    private RolUsuario rol;

    //public void VerificarMayoriaDeEdad() {}

    public void hechoSubido(Hecho hecho){
        hechosSubidos.add(hecho);
    }

    public Integer getEdad() { return edad; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public UUID getId_usuario() {return id_usuario;}
    public RolUsuario getRol() {return rol;}

    public void setEdad(Integer edad) { this.edad = edad; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setRol(RolUsuario rol) { this.rol = rol; }
    public void setId_usuario(UUID id_usuario) {this.id_usuario = id_usuario; }
}
