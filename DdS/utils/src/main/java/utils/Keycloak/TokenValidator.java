package utils.Keycloak;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import io.javalin.http.UnauthorizedResponse;
import utils.LecturaConfig;

import java.security.interfaces.RSAPublicKey;
import java.util.Properties;

public class TokenValidator {

    private final String ISSUER;

    public TokenValidator(){
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        ISSUER = "http://localhost:8080/realms/tpDDSI";
    }

    public void validar(String token) {

        try {
            System.out.println("Token validator: " + ISSUER + ", " +  token);
            //Algorithm algorithm = Algorithm.RSA256(KeycloakKeyProvider.getKey(), null);
            System.out.println("Token recibido: " + token);
            RSAPublicKey key = KeycloakKeyProvider.getKey(token);
            System.out.println("Clave obtenida: " + key);
            Algorithm algorithm = Algorithm.RSA256(key, null);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();

            DecodedJWT verificarExp = JWT.decode(token); // decodifica sin validar la firma
            System.out.println("Expiración: " + verificarExp.getExpiresAt());
            System.out.println("Emisión: " + verificarExp.getIssuedAt());
            System.out.println("Issuer: " + verificarExp.getIssuer());

            DecodedJWT jwt = verifier.verify(token);

            System.out.println("Termine de validar");

        } catch (Exception e) {
            throw new UnauthorizedResponse("Token inválido o expirado: " + e.getMessage());
        }
    }
}
