package web.controller.loginKeycloak;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import web.service.UsuarioService;

import java.util.Map;

public class AuthCallbackHandler implements Handler {

    private UsuarioService usuarioService;
    private String clientId;
    private String redirectUri;
    private String clientSecret;
    private String keycloakUrl;

    public AuthCallbackHandler(UsuarioService usuarioService, String clientSecret, String keycloakUrl, String clientId, String redirectUri) {
        this.usuarioService = usuarioService;
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.clientSecret = clientSecret;
        this.keycloakUrl = keycloakUrl;

    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String code = ctx.queryParam("code");
        if (code == null) {
            ctx.status(400).result("Error: No se recibió código");
            return;
        }

        Map<String, Object> dataUsuario = usuarioService.authCallback(code ,clientId, clientSecret, redirectUri, keycloakUrl);

        // D. Establecer sesión y redirigir
        ctx.sessionAttribute("username", dataUsuario.get("username"));
        ctx.sessionAttribute("accessToken", dataUsuario.get("accessToken"));
        ctx.sessionAttribute("rolUsuario", dataUsuario.get("rolUsuario"));
        ctx.redirect("/home");
    }
}
