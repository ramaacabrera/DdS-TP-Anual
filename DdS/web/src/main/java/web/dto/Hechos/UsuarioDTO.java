package web.dto.Hechos;

import java.util.UUID;

public class UsuarioDTO {
    private UUID usuarioId;
    private String username;

    public UsuarioDTO() {}

    public UsuarioDTO(UUID usuarioId, String nombreUsuario) {
        this.usuarioId = usuarioId;
        this.username = nombreUsuario;
    }

    // Getters y Setters
    public void setId(UUID usuarioId) {this.usuarioId = usuarioId;}

    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }

    public String getUsername() { return username; }
    public void setUsername(String nombreUsuario) { this.username = nombreUsuario; }
}