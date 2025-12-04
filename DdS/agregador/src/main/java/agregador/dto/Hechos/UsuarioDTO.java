package agregador.dto.Hechos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class UsuarioDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id_usuario;
    private String username;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String rol;

    public UsuarioDTO() {}

    public UsuarioDTO(UUID usuarioId, String nombreUsuario, int edad, String nombre, String apellido, String rol) {
        this.id_usuario = usuarioId;
        this.username = nombreUsuario;
        this.rol = rol;
    }

    // Getters y Setters
    public void setId(UUID usuarioId) {this.id_usuario = usuarioId;}

    public UUID getUsuarioId() { return id_usuario; }
    public void setUsuarioId(UUID usuarioId) { this.id_usuario = usuarioId; }

    public String getUsername() { return username; }
    public void setUsername(String nombreUsuario) { this.username = nombreUsuario; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}