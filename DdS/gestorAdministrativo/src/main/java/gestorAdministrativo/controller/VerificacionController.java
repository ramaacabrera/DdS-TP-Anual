package gestorAdministrativo.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.javalin.http.Handler;
import utils.Keycloak.TokenValidator;

import java.util.List;
import java.util.Map;

public class VerificacionController {

    public Handler verificarAdministrador = ctx -> {

        System.out.println("Iniciando verificacion de administrativo");
        if (ctx.method().name().equals("OPTIONS")) {
            System.out.println("Rechazado por OPTIONS");
            return;
        }
        String accessToken = ctx.header("accessToken");

        System.out.println("Token: " + accessToken);

        if (accessToken == null) {
            System.out.println("No hay token");
            throw new io.javalin.http.UnauthorizedResponse("Faltan credenciales");
        }

        DecodedJWT jwt = JWT.decode(accessToken);
        String username = jwt.getClaim("preferred_username").asString();
        String rolUsuario = "VISITANTE";

        Claim realmAccessClaim = jwt.getClaim("realm_access");

        if (!realmAccessClaim.isNull()) {
            // 'realm_access' es un objeto complejo, lo convertimos a Map
            Map<String, Object> realmAccessMap = realmAccessClaim.asMap();

            if (realmAccessMap != null && realmAccessMap.containsKey("roles")) {
                // Keycloak devuelve los roles como una lista de objetos (Strings)
                List<String> roles = (List<String>) realmAccessMap.get("roles");

                if (roles.contains("administrador")) {
                    rolUsuario = "ADMINISTRADOR";
                }else if (roles.contains("contribuyente")) {
                    rolUsuario = "CONTRIBUYENTE";
                }
                System.out.println("Rol: " + rolUsuario);
            }
        }

        try {
            TokenValidator validador = new TokenValidator();
            validador.validar(accessToken);
        } catch (Exception e) {
            System.err.println("Token inválido: " + e.getMessage());
            throw new io.javalin.http.UnauthorizedResponse("Token inválido");
        }

        if (!rolUsuario.equals("ADMINISTRADOR")) {
            System.err.println("Usuario no es admin: " + username);
            throw new io.javalin.http.ForbiddenResponse("No tienes permisos de administrador");
        }

        System.out.println("✅ Acceso autorizado para: " + username);
    };
}
