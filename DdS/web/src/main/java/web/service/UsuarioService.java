package web.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.json.JSONObject;
import okhttp3.Request;
import okhttp3.Response;
import web.domain.Usuario.Usuario;
import web.domain.Usuario.RolUsuario;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UsuarioService {
    private final String urlPublica;
    private final ObjectMapper mapper = new ObjectMapper();

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    public UsuarioService(String urlPublica){
        this.urlPublica = urlPublica;
    }

    public RolUsuario obtenerRol(String username){
        RolUsuario rol = null;
        try {
            HttpClient cli =  HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI(urlPublica + "/usuario/" + username))
                    .GET()
                    .build();
            HttpResponse<String> res = cli.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) {
                return rol;
            }

            System.out.println("Body: " + res.body());
            Usuario user = mapper.readValue(res.body(), new TypeReference<Usuario>() {});
            System.out.println("Usuario: " + user.getUsuarioId());
            System.out.println("Rol: " + user.getRol());
            rol = user.getRol();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return rol;
    }

    public Map<String, Object> authCallback(String code,String clientId, String clientSecret, String redirectUri, String keycloakUrl) {
        // A. Petición POST a Keycloak para obtener el Token
        // Formateamos los datos como x-www-form-urlencoded
        Map<String, String> tokenParams = Map.of(
                "grant_type", "authorization_code",
                "client_id", clientId,
                "client_secret", clientSecret,
                "code", code,
                "redirect_uri", redirectUri
        );

        HttpRequest tokenRequest = HttpRequest.newBuilder()
                .uri(URI.create(keycloakUrl + "/protocol/openid-connect/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData(tokenParams)))
                .build();

        HttpResponse<String> tokenResponse = null;
        try {
            tokenResponse = httpClient.send(tokenRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (tokenResponse.statusCode() != 200) {
            throw new RuntimeException("Error al conectar con Keycloak: " + tokenResponse.body());
        }

        // Parsear token
        JSONObject tokenJson = new JSONObject(tokenResponse.body());
        String accessToken = tokenJson.getString("access_token");

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
            }
        }


        // C. Petición POST al GestorPublico para sincronizar
        JSONObject syncBody = new JSONObject();
        syncBody.put("username", username);

        HttpRequest syncRequest = HttpRequest.newBuilder()
                .uri(URI.create(urlPublica + "/usuario/sincronizar"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(syncBody.toString()))
                .build();

        // Enviamos de forma asíncrona o síncrona según prefieras. Aquí síncrona:
        try {
            HttpResponse<String> syncResponse = httpClient.send(syncRequest, HttpResponse.BodyHandlers.ofString());
            if (syncResponse.statusCode() >= 400) {
                System.err.println("Advertencia: El GestorPublico devolvió error " + syncResponse.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Error contactando al GestorPublico: " + e.getMessage());
            // Decidimos no bloquear el login si falla la sincro, pero lo logueamos
        };

        return Map.of("username", username,"rolUsuario", rolUsuario, "accessToken", accessToken);
    }

    private static String formData(Map<String, String> data) {
        return data.entrySet().stream()
                .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" +
                        URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }
}
