package controller;

import io.javalin.http.Context;
import io.javalin.http.Handler;


import org.json.JSONObject;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;

public class PostLoginHandler implements Handler {
    private String urlPublica;
    public PostLoginHandler(String urlPublica) {
        this.urlPublica = urlPublica;
    }

    @Override
    public void handle(@NotNull Context ctx) {
        String usuario = ctx.formParam("usuario");
        String password = ctx.formParam("password");
        String redirectUrl = ctx.formParam("redirectUrl");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8087/api/login"))
                .POST(HttpRequest.BodyPublishers.ofString("usuario=" + usuario +"&password=" + password))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());

            JSONObject json = new JSONObject(response.body());

            if (response.statusCode() == 200) {

                String accessToken = json.getString("access_token");
                String username = json.getString("username");

                ctx.sessionAttribute("access_token", accessToken);
                ctx.sessionAttribute("username", username);

                ctx.redirect("/home");
            } else {
                Map<String, Object> model = new HashMap<>();
                model.put("error", json.getString("error"));
                ctx.render("login.ftl", model);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
