package web.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.json.JSONObject;
import okhttp3.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class UsuarioService {
    private final String urlPublica;
    private final ObjectMapper mapper = new ObjectMapper();

    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    public UsuarioService(String urlPublica){
        this.urlPublica = urlPublica;
    }

    public Map<String, Object> authCallback(String code, String clientId, String clientSecret, String redirectUri, String keycloakUrl) {

        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("code", code)
                .add("redirect_uri", redirectUri)
                .build();

        Request tokenRequest = new Request.Builder()
                .url(keycloakUrl + "/protocol/openid-connect/token")
                .post(formBody)
                .build();

        String accessToken;

        try (Response tokenResponse = httpClient.newCall(tokenRequest).execute()) {

            String responseBody = tokenResponse.body() != null ? tokenResponse.body().string() : "";

            if (!tokenResponse.isSuccessful()) {
                throw new RuntimeException("Error al conectar con Keycloak: " + responseBody);
            }

            JSONObject tokenJson = new JSONObject(responseBody);
            accessToken = tokenJson.getString("access_token");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        DecodedJWT jwt = JWT.decode(accessToken);
        String username = jwt.getClaim("preferred_username").asString();
        String rolUsuario = "VISITANTE";

        Claim realmAccessClaim = jwt.getClaim("realm_access");

        if (!realmAccessClaim.isNull()) {
            Map<String, Object> realmAccessMap = realmAccessClaim.asMap();

            if (realmAccessMap != null && realmAccessMap.containsKey("roles")) {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) realmAccessMap.get("roles");

                if (roles.contains("administrador")) {
                    rolUsuario = "ADMINISTRADOR";
                } else if (roles.contains("contribuyente")) {
                    rolUsuario = "CONTRIBUYENTE";
                }
            }
        }

        JSONObject syncBody = new JSONObject();
        syncBody.put("username", username);

        RequestBody jsonBody = RequestBody.create(syncBody.toString(), JSON_MEDIA_TYPE);

        Request syncRequest = new Request.Builder()
                .url(urlPublica + "/usuario/sincronizar")
                .post(jsonBody)
                .build();

        try (Response syncResponse = httpClient.newCall(syncRequest).execute()) {
            if (!syncResponse.isSuccessful()) {
                System.err.println("Advertencia: El GestorPublico devolvió error " + syncResponse.code());
            }
        } catch (Exception e) {
            System.err.println("Error contactando al GestorPublico: " + e.getMessage());
        }

        return Map.of("username", username, "rolUsuario", rolUsuario, "accessToken", accessToken);
    }

    public String obtenerId(String username) {
        System.out.println(">>> Buscando ID para el usuario: " + username);
        String id = null;

        Request request = new Request.Builder()
                .url(urlPublica + "/usuario/" + username)
                .header("Content-Type", "application/json") // Aunque en GET no suele ser necesario, lo mantenemos por consistencia
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                System.err.println(">>> Error API Publica. Status: " + response.code());
                return null;
            }

            String responseBody = response.body() != null ? response.body().string() : "";
            System.out.println(">>> JSON Recibido: " + responseBody);

            JsonNode root = mapper.readTree(responseBody);

            if (root.has("usuarioId")) {
                id = root.get("usuarioId").asText();
                System.out.println(">>> ID UUID Encontrado: " + id);
            } else if (root.has("id_usuario")) {
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

}