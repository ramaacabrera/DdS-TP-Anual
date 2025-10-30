package ApiPublica.Presentacion;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import utils.Persistencia.UsuarioRepositorio;
import utils.Dominio.Usuario.Usuario;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class PostLoginHandler implements Handler {
    private UsuarioRepositorio usuarioRepositorio;
    private String urlWeb;
    private String servidorSSO;

    public PostLoginHandler(UsuarioRepositorio usuarioRepositorio, String urlWeb, String servidorSSO) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.urlWeb = urlWeb;
        this.servidorSSO = servidorSSO;
    }

    @Override
    public void handle(@NotNull Context ctx){
        String username = ctx.formParam("usuario");
        Usuario usuario = usuarioRepositorio.buscarPorUsername(username);
        if(usuario == null){
            //ctx.sessionAttribute("error", "Usuario no existe");
            //ctx.redirect("http://localhost:7070/login");
            System.out.println("Usuario no encontrado");
            ctx.status(401).json(Map.of("error", "El usuario no existe"));
            return;
        }
        String password = ctx.formParam("password");
        String body = "client_id=miapp-backend" +
                "&client_secret=AZAGxoNbaW0aRksB3YWPG7Qj05tKJhr5" +
                "&username=" + URLEncoder.encode(username, StandardCharsets.UTF_8) +
                "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8) +
                "&grant_type=password";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(servidorSSO + "/protocol/openid-connect/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200){
                JSONObject json = new JSONObject(response.body());
                String access_token = json.get("access_token").toString();
                System.out.println("Access_token: " + access_token);

                System.out.println(urlWeb+"/hechos");

                //ctx.sessionAttribute("access_token", access_token);
                //ctx.sessionAttribute("username", username);
                //ctx.redirect(urlWeb + "/hechos", HttpStatus.FOUND);
                //ctx.redirect("http://localhost:7070/hechos?username=" + username + "&access_token=" + access_token);
                ctx.json(Map.of("username", username, "access_token", access_token));
            } else{
                ctx.status(401).json(Map.of("error", "Password incorrecto"));
                return;
            }

        } catch (Exception e){
            System.err.println("Error al validar usuario " + e.getMessage());
        }
    }
}
