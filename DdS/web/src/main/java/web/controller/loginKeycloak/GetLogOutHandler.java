package web.controller.loginKeycloak;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GetLogOutHandler implements Handler {
    private String urlWeb;
    private String keycloakUrl;
    // Agrega el client_id si es necesario para tu versión de KC
    private String clientId = "componente-web";

    public GetLogOutHandler(String urlWeb, String keycloakUrl) {
        this.urlWeb = urlWeb;
        this.keycloakUrl = keycloakUrl;
    }

    @Override
    public void handle(@NotNull Context ctx) {
        // 1. Matamos la sesión local
        ctx.req().getSession().invalidate();

        // 2. Preparamos la URL de retorno
        String urlHome = urlWeb + "/home";

        // Es importante codificar la URL para que viaje bien como parámetro
        String urlHomeEncoded = URLEncoder.encode(urlHome, StandardCharsets.UTF_8);

        // 3. Construimos la URL de Logout
        // NOTA: Probamos con ambos parámetros por compatibilidad.
        // Keycloak moderno prefiere 'post_logout_redirect_uri' + 'client_id'
        String logoutUrl = keycloakUrl + "/protocol/openid-connect/logout?" +
                "post_logout_redirect_uri=" + urlHomeEncoded +
                "&client_id=" + clientId;

        /* Si tu Keycloak es muy antiguo (versión < 18), usa esta línea en su lugar:
           String logoutUrl = keycloakUrl + "/protocol/openid-connect/logout?redirect_uri=" + urlHomeEncoded;
        */

        ctx.redirect(logoutUrl);
    }
}