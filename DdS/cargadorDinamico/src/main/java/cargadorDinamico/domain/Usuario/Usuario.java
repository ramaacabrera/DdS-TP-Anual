package cargadorDinamico.domain.Usuario;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import cargadorDinamico.domain.HechosYColecciones.Hecho;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type="uuid-char")
    @Column(name = "usuarioId", length = 36, updatable = false, nullable = false)
    private UUID usuarioId;

    @OneToMany(mappedBy = "contribuyente")
    @JsonIgnore
    private List<Hecho> hechosSubidos = new ArrayList<Hecho>();

    private String username;

    @JsonProperty
    private Integer edad;
    @JsonProperty
    private String nombre;
    @JsonProperty
    private String apellido;
    public void Contribuyente() {}

    @Enumerated(EnumType.STRING)
    private cargadorDinamico.domain.Usuario.RolUsuario rol;

    //public void VerificarMayoriaDeEdad() {}

    public void hechoSubido(Hecho hecho){
        hechosSubidos.add(hecho);
    }

    public Integer getEdad() { return edad; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public UUID getUsuarioId() {return usuarioId;}
    public cargadorDinamico.domain.Usuario.RolUsuario getRol() {return rol;}
    public String getUsername() {return username;}

    public void setEdad(Integer edad) { this.edad = edad; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setRol(RolUsuario rol) { this.rol = rol; }
    public void setUsuarioId(UUID id_usuario) {this.usuarioId = id_usuario; }
    public void setUsername(String username) { this.username = username; }
}
