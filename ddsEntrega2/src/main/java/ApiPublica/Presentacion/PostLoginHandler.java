package ApiPublica.Presentacion;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import utils.KeyCloak.TokenValidator;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class PostLoginHandler implements Handler {
    public PostLoginHandler(){}

    @Override
    public void handle(@NotNull Context ctx){
        String usuario = ctx.formParam("usuario");
        String password = ctx.formParam("password");
        String body = "client_id=miapp-backend" +
        "&client_secret=AZAGxoNbaW0aRksB3YWPG7Qj05tKJhr5" +
        "&username=" + URLEncoder.encode(usuario, StandardCharsets.UTF_8) +
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
                System.out.println("Access_token: " + json.get("access_token"));
                String jsonBody = """
                        {
                            "nombre": "nombre",
                            "apellido": "apellido"
                        }
                        """;

                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8087/api/prueba-validacion"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + json.getString("access_token"))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();
                HttpClient cliente = HttpClient.newHttpClient();

                HttpResponse<String> res = cliente.send(req, HttpResponse.BodyHandlers.ofString());

                if(res.statusCode() == 200){
                    System.out.println("Validacion exitosa");
                } else{
                    System.out.println("Usuario no valido");
                    System.err.println(res.statusCode());
                }
            } else{
                ctx.status(response.statusCode()).result(response.body());
            }

        } catch (Exception e){
            System.err.println("Error al validar usuario " + e.getMessage());
        }
    }
}
