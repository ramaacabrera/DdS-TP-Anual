package gestorPublico.dto;

public class LoginDTO {
    public String usuario;
    public String password;

    public LoginDTO() {}

    public LoginDTO(String usuario, String password) {
        this.usuario = usuario;
        this.password = password;
    }
}