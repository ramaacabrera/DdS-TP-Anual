package gestorPublico.dto;

public class RegistroUsuarioDTO {
    public String usuario;
    public String password;
    public String confirmPassword; // Añade este campo
    public String nombre;
    public String apellido;
    public String email;
    public int edad;

    public RegistroUsuarioDTO() {}

    public RegistroUsuarioDTO (String usuario, String password, String confirmPassword, String nombre, String apellido, String email, int edad) {
        this.usuario = usuario;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.edad = edad;
    }
}