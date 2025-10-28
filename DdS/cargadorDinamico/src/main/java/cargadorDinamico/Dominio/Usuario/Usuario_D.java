package cargadorDinamico.Dominio.Usuario;

import cargadorDinamico.Dominio.HechosYColeccionesD.Hecho_D;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Usuario_D {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type = "uuid-char")
    @Column(name = "id_usuario", length =36 , updatable = false, nullable = false)
    private UUID id_usuario;

    @OneToMany(mappedBy = "contribuyente")
    @JsonIgnore
    private List<Hecho_D> hechosSubidos = new ArrayList<Hecho_D>();

    @JsonProperty
    private Integer edad;
    @JsonProperty
    private String nombre;
    @JsonProperty
    private String apellido;
    public void Contribuyente() {}

    @Enumerated(EnumType.STRING)
    private RolUsuario rol;

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
