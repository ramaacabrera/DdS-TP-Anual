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

        JSONObject tokenJson = new JSONObject(tokenResponse.body());
        String accessToken = tokenJson.getString("access_token");

        DecodedJWT jwt = JWT.decode(accessToken);
        String username = jwt.getClaim("preferred_username").asString();
        String rolUsuario = "VISITANTE";

        Claim realmAccessClaim = jwt.getClaim("realm_access");

        if (!realmAccessClaim.isNull()) {
            Map<String, Object> realmAccessMap = realmAccessClaim.asMap();

            if (realmAccessMap != null && realmAccessMap.containsKey("roles")) {
                List<String> roles = (List<String>) realmAccessMap.get("roles");

                if (roles.contains("administrador")) {
                    rolUsuario = "ADMINISTRADOR";
                }else if (roles.contains("contribuyente")) {
                    rolUsuario = "CONTRIBUYENTE";
                }
            }
        }

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
        };

        return Map.of("username", username,"rolUsuario", rolUsuario, "accessToken", accessToken);
    }

    public String obtenerId(String username) {
        System.out.println(">>> Buscando ID para el usuario: " + username);
        String id = null;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlPublica + "/usuario/" + username))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println(">>> Error API Publica. Status: " + response.statusCode());
                return null;
            }

            System.out.println(">>> JSON Recibido: " + response.body());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());

            if (root.has("usuarioId")) {
                id = root.get("usuarioId").asText();
                System.out.println(">>> ID UUID Encontrado: " + id);
            } else if (root.has("id_usuario")) {
                // Fallback por si acaso cambiara el DTO
                id = root.get("id_usuario").asText();
                System.out.println(">>> ID Encontrado (clave id_usuario): " + id);
            } else {
                System.err.println(">>> NO SE ENCONTRO NINGUNA CLAVE DE ID EN EL JSON.");
            }

        } catch (Exception e) {
            System.err.println(">>> Excepción en obtenerId: " + e.getMessage());
            e.printStackTrace();
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
