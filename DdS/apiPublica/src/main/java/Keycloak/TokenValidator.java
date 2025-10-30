package Keycloak;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import io.javalin.http.UnauthorizedResponse;
import utils.LecturaConfig;

import java.util.Properties;

public class TokenValidator {

    private final String ISSUER;

    public TokenValidator(){
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        ISSUER = config.getProperty("urlServidorSSO");
    }

    public void validar(String token) {

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

        } catch (Exception e) {
            throw new UnauthorizedResponse("Token inválido o expirado: " + e.getMessage());
        }
    }
}
