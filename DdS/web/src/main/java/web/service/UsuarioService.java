package web.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.json.JSONObject;
import web.domain.Usuario.Usuario;

import java.net.URI;
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

    public String obtenerId(String username){
        System.out.println("Consultando ID de usuario a: " + urlPublica);

        String id = null;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlPublica + "/usuario/" + username))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        try{
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() != 200){
                System.err.println("Error consultando ID de usuario: " + response.statusCode());
                return null;
            }
            ObjectMapper mapper = new  ObjectMapper();
            JsonNode root = mapper.readTree(response.body());

            id = root.get("id_usuario").asText();

            System.out.println("ID recuperado: " + id);

        } catch (Exception e){
            System.err.println("Error consultando ID de usuario: " + e.getMessage());
        }
        return id;
    }

    private static String formData(Map<String, String> data) {
        return data.entrySet().stream()
                .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" +
                        URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }
}
