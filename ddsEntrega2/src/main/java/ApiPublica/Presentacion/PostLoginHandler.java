package ApiPublica.Presentacion;

import Agregador.Persistencia.UsuarioRepositorio;
import Agregador.Usuario.Usuario;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import utils.BDUtils;
import utils.KeyCloak.TokenValidator;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PostLoginHandler implements Handler {
    private UsuarioRepositorio usuarioRepositorio;

    public PostLoginHandler(UsuarioRepositorio usuarioRepositorio){this.usuarioRepositorio = usuarioRepositorio;}

    @Override
    public void handle(@NotNull Context ctx){
        String username = ctx.formParam("usuario");
        Usuario usuario = usuarioRepositorio.buscarPorUsername(username);
        if(usuario == null){
            ctx.sessionAttribute("error", "Usuario no existe");
            ctx.redirect("/api/login");
            return;
        }
        String password = ctx.formParam("password");
        String body = "client_id=miapp-backend" +
        "&client_secret=AZAGxoNbaW0aRksB3YWPG7Qj05tKJhr5" +
        "&username=" + URLEncoder.encode(username, StandardCharsets.UTF_8) +
        "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8) +
        "&grant_type=password";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/realms/tpDDSI/protocol/openid-connect/token"))
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

                ctx.sessionAttribute("access_token", access_token);
                ctx.redirect("/api/home");
            } else{
                Map<String, Object> model = new HashMap<>();
                ctx.sessionAttribute("error", "Password incorrecto");
                ctx.redirect("/api/login");
            }

        } catch (Exception e){
            System.err.println("Error al validar usuario " + e.getMessage());
        }
    }
}
