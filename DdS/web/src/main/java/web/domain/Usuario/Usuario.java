package web.domain.Usuario;

import web.domain.HechosYColecciones.Hecho;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

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
    @Column(name = "id_usuario", length = 36, updatable = false, nullable = false)
    private UUID usuarioId;

    @OneToMany(mappedBy = "contribuyente")
    @JsonIgnore
    private List<Hecho> hechosSubidos = new ArrayList<Hecho>();

    private String username;

    public void Contribuyente() {}

    @Enumerated(EnumType.STRING)
    private RolUsuario rol;

    //public void VerificarMayoriaDeEdad() {}

    public void hechoSubido(Hecho hecho){
        hechosSubidos.add(hecho);
    }

    public UUID getUsuarioId() {return usuarioId;}
    public RolUsuario getRol() {return rol;}
    public String getUsername() {return username;}


    public void setRol(RolUsuario rol) { this.rol = rol; }
    public void setUsuarioId(UUID id_usuario) {this.usuarioId = id_usuario; }
    public void setUsername(String username) { this.username = username; }
}
