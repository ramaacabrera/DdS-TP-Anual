package web.controller;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.json.JSONObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostLoginHandler implements Handler {
    private String urlPublica;

    public PostLoginHandler(String urlPublica) {
        this.urlPublica = urlPublica;
    }

    @Override
    public void handle(@NotNull Context ctx) throws IOException, InterruptedException {
        String usuario = ctx.formParam("usuario");
        String password = ctx.formParam("password");

        JSONObject requestBody = new JSONObject();
        requestBody.put("usuario", usuario);
        requestBody.put("password", password);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8087/api/login"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .header("Content-Type", "application/json")
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());

            if (response.statusCode() == 200) {
                String accessToken = json.getString("access_token");
                DecodedJWT jwt = JWT.decode(accessToken);

                String usernameReal = "Usuario";
                if (!jwt.getClaim("preferred_username").isMissing()) {
                    usernameReal = jwt.getClaim("preferred_username").asString();
                } else if (!jwt.getClaim("username").isMissing()) {
                    usernameReal = jwt.getClaim("username").asString();
                }

                List<String> roles = null;
                if (!jwt.getClaim("realm_access").isMissing()) {
                    Map<String, Object> realmAccess = jwt.getClaim("realm_access").asMap();
                    roles = (List<String>) realmAccess.get("roles");
                }

                ctx.sessionAttribute("access_token", accessToken);
                ctx.sessionAttribute("username", usernameReal);

                String rolPrincipal = null;
                if (roles != null) {
                    if (roles.contains("administrador")) {
                        rolPrincipal = "ADMINISTRADOR";
                    } else if (roles.contains("contribuyente")) {
                        rolPrincipal = "CONTRIBUYENTE";
                    }
                }
                ctx.sessionAttribute("rolUsuario", rolPrincipal);
                System.out.println("PostLogin rolUsuario:" + rolPrincipal);

                // Respuesta exitosa JSON
                ctx.json(Map.of("status", "ok", "redirect", "/home"));

            } else {
                String errorMsg = json.has("error") ? json.getString("error") : "Credenciales inválidas";

                ctx.status(401).json(Map.of("error", errorMsg));
            }
    }
}