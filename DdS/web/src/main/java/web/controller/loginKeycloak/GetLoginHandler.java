package web.controller.loginKeycloak;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class GetLoginHandler implements Handler {
    private final String keycloakUrl;
    private final String clientId;
    private final String redirectUrl;

    public GetLoginHandler(String keycloakUrl, String clientId, String redirectUrl) {
        this.keycloakUrl = keycloakUrl;
        this.clientId = clientId;
        this.redirectUrl = redirectUrl;
    }

    @Override
    public void handle(@NotNull Context ctx){
        String authUrl = keycloakUrl + "/protocol/openid-connect/auth" +
                "?client_id=" + clientId +
                "&response_type=code" +
                "&scope=openid profile email" +
                "&redirect_uri=" + redirectUrl;

        ctx.redirect(authUrl);
    }
}
