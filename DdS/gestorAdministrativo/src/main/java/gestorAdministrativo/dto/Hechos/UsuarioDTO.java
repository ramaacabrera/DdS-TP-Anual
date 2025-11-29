package gestorAdministrativo.dto.Hechos;

import java.util.UUID;

public class UsuarioDTO {
    private UUID usuarioId;
    private String nombreUsuario;
    private int edad;
    private String nombre;
    private String apellido;
    private String rol;

    public UsuarioDTO() {}

    public UsuarioDTO(UUID usuarioId, String nombreUsuario, int edad, String nombre, String apellido, String rol) {
        this.usuarioId = usuarioId;
        this.nombreUsuario = nombreUsuario;
        this.edad = edad;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
    }

    // Getters y Setters
    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}