package gestorPublico.service;

import gestorPublico.dto.LoginDTO;
import gestorPublico.dto.RegistroUsuarioDTO;
import gestorPublico.repository.UsuarioRepositorio;
import org.json.JSONObject;
import gestorPublico.domain.Usuario.RolUsuario;
import gestorPublico.domain.Usuario.Usuario;
import utils.Keycloak.UserCreator;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class UsuarioService {
    private final UsuarioRepositorio usuarioRepositorio;
    private final String servidorSSO;

    public UsuarioService(UsuarioRepositorio usuarioRepositorio, String servidorSSO) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.servidorSSO = servidorSSO;
    }

    public Usuario obtenerPorUsername(String username) {
        return usuarioRepositorio.buscarPorUsername(username);
    }

    public Map<String, Object> login(LoginDTO dto) {
        // 1. Validar existencia local
        Usuario usuario = usuarioRepositorio.buscarPorUsername(dto.usuario);
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no existe en la base de datos local");
        }

        try {
            String body = "client_id=miapp-backend" +
                    "&client_secret=AZAGxoNbaW0aRksB3YWPG7Qj05tKJhr5" +
                    "&username=" + URLEncoder.encode(dto.usuario, StandardCharsets.UTF_8) +
                    "&password=" + URLEncoder.encode(dto.password, StandardCharsets.UTF_8) +
                    "&grant_type=password";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(servidorSSO + "/protocol/openid-connect/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                String accessToken = json.getString("access_token");

                return Map.of(
                        "username", dto.usuario,
                        "access_token", accessToken,
                        "rol", usuario.getRol()
                );
            } else {
                throw new SecurityException("Credenciales inválidas en Keycloak");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error en autenticación: " + e.getMessage());
        }
    }

    public void registrarUsuario(RegistroUsuarioDTO dto) {
        if (usuarioRepositorio.buscarPorUsername(dto.usuario) != null) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        UserCreator creador = new UserCreator();
        int codigo = creador.crearUsuario(dto.usuario, dto.password, dto.nombre, dto.apellido, dto.email);

        if (codigo != 201 && codigo != 204) { // 201 Created es lo estándar, 204 a veces
            throw new RuntimeException("Error al crear usuario en Keycloak. Código: " + codigo);
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(dto.nombre);
        nuevoUsuario.setApellido(dto.apellido);
        nuevoUsuario.setEdad(dto.edad);
        nuevoUsuario.setUsername(dto.usuario);
        nuevoUsuario.setRol(RolUsuario.CONTRIBUYENTE); // Rol por defecto

        usuarioRepositorio.guardar(nuevoUsuario);
    }
}