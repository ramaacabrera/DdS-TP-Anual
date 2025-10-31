package utils.Keycloak;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.LecturaConfig;

import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Properties;

public class KeycloakKeyProvider {

    private static RSAPublicKey cachedKey;

    public static RSAPublicKey getKey(String token) throws Exception {
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();

        String url = "http://localhost:8080/realms/tpDDSI";

        if (cachedKey != null) return cachedKey;
        DecodedJWT jwt = JWT.decode(token);
        String tokenKid = jwt.getKeyId();

        String jwksUrl = url + "/protocol/openid-connect/certs";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(jwksUrl)).build();
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject jwks = new JSONObject(response.body());
        JSONArray keys = jwks.getJSONArray("keys");

        JSONObject key = null;
        for (int i = 0; i < keys.length(); i++) {
            if (keys.getJSONObject(i).getString("kid").equals(tokenKid)) {
                key = keys.getJSONObject(i);
                break;
            }
        }

        if (key == null) throw new Exception("No se encontró la clave correspondiente al token");

        BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(key.getString("n")));
        BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(key.getString("e")));

        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
    }
}