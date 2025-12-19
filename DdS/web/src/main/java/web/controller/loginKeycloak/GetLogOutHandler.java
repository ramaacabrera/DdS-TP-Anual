package web.controller.loginKeycloak;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GetLogOutHandler implements Handler {
    private String urlWeb;
    private String keycloakUrl;
    private String clientId = "componente-web";

    public GetLogOutHandler(String urlWeb, String keycloakUrl) {
        this.urlWeb = urlWeb;
        this.keycloakUrl = keycloakUrl;
    }

    @Override
    public void handle(@NotNull Context ctx) {
        ctx.req().getSession().invalidate();

        String urlHome = urlWeb + "/home";

        String urlHomeEncoded = URLEncoder.encode(urlHome, StandardCharsets.UTF_8);

        String logoutUrl = keycloakUrl + "/protocol/openid-connect/logout?" +
                "post_logout_redirect_uri=" + urlHomeEncoded +
                "&client_id=" + clientId;

        ctx.redirect(logoutUrl);
    }
}