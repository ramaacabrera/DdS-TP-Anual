package ApiPublica.Presentacion;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import utils.Dominio.Usuario.RolUsuario;
import utils.Dominio.Usuario.Usuario;
import utils.Persistencia.UsuarioRepositorio;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;


public class PostLoginKeyCloakHandler implements Handler {
    private UsuarioRepositorio usuarioRepositorio;

    public PostLoginKeyCloakHandler(UsuarioRepositorio usuarioRepositorio) {this.usuarioRepositorio = usuarioRepositorio;}

    @Override
    public void handle(@NotNull Context ctx){
        String code = ctx.formParam("code");

        HttpClient client = HttpClient.newHttpClient();

        String tokenEndpoint = "http://localhost:8080/realms/tpDDSI/protocol/openid-connect/token";
        String body = "grant_type=authorization_code"
                + "&code=" + code
                + "&redirect_uri=http://localhost:7070/auth/callback"
                + "&client_id=miapp-backend"
                + "&client_secret=" + "AZAGxoNbaW0aRksB3YWPG7Qj05tKJhr5";

        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(tokenEndpoint))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 1️⃣ Parsear la respuesta JSON para obtener el id_token
            JSONObject json = new JSONObject(response.body());
            String idToken = json.getString("id_token");

            // 2️⃣ Decodificar el id_token (JWT)
            DecodedJWT decoded = JWT.decode(idToken);

            // 3️⃣ Obtener los datos del usuario
            String username = decoded.getClaim("preferred_username").asString();
            String nombre = decoded.getClaim("given_name").asString();
            String apellido = decoded.getClaim("family_name").asString();

            if(usuarioRepositorio.buscarPorUsername(username) == null){
                Usuario usuario = new Usuario();
                usuario.setUsername(username);
                usuario.setNombre(nombre);
                usuario.setApellido(apellido);
                usuario.setRol(RolUsuario.CONTRIBUYENTE);
                usuario.setEdad(18);
                usuarioRepositorio.guardar(usuario);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("username", username);
            result.put("access_token", json.getString("access_token"));

            ctx.status(200).json(result);

        } catch (Exception e) {
            System.err.println("Error al obtener tokens: " + e.getMessage());
            ctx.status(500);
        }

    }
}
