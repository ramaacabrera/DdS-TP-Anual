package Presentacion;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class GetLoginHandler implements Handler {
    private String urlPublica;

    public GetLoginHandler(String urlPublica) {this.urlPublica = urlPublica;}

    @Override
    public void handle(@NotNull Context ctx){
        String keycloakBase = "http://localhost:8080/realms/tpDDSI/protocol/openid-connect/auth";
        String clientId = "miapp-backend";
        String redirectUri = "http://localhost:7070/auth/callback"; // o localhost:7000 en desarrollo
        String scope = "openid email profile";
        String responseType = "code";
        String authUrl = String.format(
                "%s?client_id=%s&redirect_uri=%s&response_type=%s&scope=%s",
                keycloakBase,
                clientId,
                URLEncoder.encode(redirectUri, StandardCharsets.UTF_8),
                responseType,
                scope
        );
        Map<String, Object> model = new HashMap<>();
        model.put("baseApiUrl", "http://localhost:8087/api");
        model.put("authUrl", authUrl);
        //model.put("baseAPIUrl", urlPublica);
        String error = ctx.sessionAttribute("error");
        if(error != null){
            model.put("error", error);
            ctx.sessionAttribute("error", null);
        }

        ctx.render("login.ftl", model);
    }
}
