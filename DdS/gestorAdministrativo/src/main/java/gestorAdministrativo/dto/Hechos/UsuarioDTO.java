package gestorAdministrativo.dto.Hechos;

import java.util.UUID;

public class UsuarioDTO {
    private UUID usuarioId;
    private String username;
    private String rol;

    public UsuarioDTO() {}

    public UsuarioDTO(UUID usuarioId, String nombreUsuario, int edad, String nombre, String apellido, String rol) {
        this.usuarioId = usuarioId;
        this.username = nombreUsuario;
        this.rol = rol;
    }

    // Getters y Setters
    public void setId(UUID usuarioId) {this.usuarioId = usuarioId;}

    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }

    public String getUsername() { return username; }
    public void setUsername(String nombreUsuario) { this.username = nombreUsuario; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}