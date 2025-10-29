package utils.KeyCloak;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;

public class TokenValidator {

    private static final String ISSUER = "http://localhost:8080/realms/tpDDSI";

    public static void validar(String token) {

        try {
            //Algorithm algorithm = Algorithm.RSA256(KeycloakKeyProvider.getKey(), null);
            Algorithm algorithm = Algorithm.RSA256(KeycloakKeyProvider.getKey(token), null);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();

            DecodedJWT verificarExp = JWT.decode(token); // decodifica sin validar la firma
            System.out.println("Expiración: " + verificarExp.getExpiresAt());
            System.out.println("Emisión: " + verificarExp.getIssuedAt());
            System.out.println("Issuer: " + verificarExp.getIssuer());

            DecodedJWT jwt = verifier.verify(token);

            // Guardás info útil del usuario para tus endpoints
            /*
            ctx.attribute("usuario_id", jwt.getSubject());
            ctx.attribute("username", jwt.getClaim("preferred_username").asString());
             */

        } catch (Exception e) {
            throw new UnauthorizedResponse("Token inválido o expirado: " + e.getMessage());
        }
    }
}