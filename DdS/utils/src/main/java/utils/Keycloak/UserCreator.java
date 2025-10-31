package utils.Keycloak;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import org.json.JSONObject;
import utils.LecturaConfig;

import java.util.Properties;

public class UserCreator {
    private String urlServidor;
    private String urlServidorAdmin;

    public UserCreator(){
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        this.urlServidor = config.getProperty("urlServidorSSO");
        this.urlServidorAdmin = config.getProperty("urlServidorSSOAdmin");
    }

    public int crearUsuario(String username, String clave, String nombre, String apellido, String email){

        /*
            //////////////////////////////////////////////////////////////////
                PIDO EL ACCES TOKEN DEL CLIENTE PARA PODER CREAR USUARIOS
            /////////////////////////////////////////////////////////////////
         */

        String client_secret = "AZAGxoNbaW0aRksB3YWPG7Qj05tKJhr5";
        String tokenUrl = urlServidor + "/protocol/openid-connect/token";

        HttpResponse<String> response = Unirest.post(tokenUrl)
                .field("client_id", "miapp-backend")
                .field("client_secret", client_secret)
                .field("grant_type", "client_credentials")
                .asString();

        JSONObject json = new JSONObject(response.getBody());
        String accessToken = json.getString("access_token");

        /*
            //////////////////////////////////////////////////////////////////
                            CREO EL USUARIO SIN PASSWORD
            /////////////////////////////////////////////////////////////////
         */

        String apiUrl = urlServidorAdmin + "/users";
        JSONObject newUser = new JSONObject();
        newUser.put("username", username);
        newUser.put("email", email);
        newUser.put("firstName", nombre);
        newUser.put("lastName", apellido);
        newUser.put("enabled", true);
        newUser.put("emailVerified", true);

        HttpResponse<String> createResponse = Unirest.post(apiUrl)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .body(newUser.toString())
                .asString();

        if (createResponse.getStatus() == 201) {
            System.out.println("✅ Usuario creado correctamente en Keycloak");
        } else {
            System.out.println("❌ Error creando usuario: " + createResponse.getStatus());
            System.out.println(createResponse.getBody());
            return createResponse.getStatus();
        }

        /*
            //////////////////////////////////////////////////////////////////
                    LE ASIGNO UNA PASSWORD AL USUARIO YA CREADO
            /////////////////////////////////////////////////////////////////
         */

        String userId = null;

        String searchUrl = urlServidorAdmin + "/users?username=" + username;
        HttpResponse<String> res = Unirest.get(searchUrl)
                .header("Authorization", "Bearer " + accessToken)
                .asString();

        JSONArray arr = new JSONArray(res.getBody());
        if (arr.length() > 0) {
            userId = arr.getJSONObject(0).getString("id");
        }
        if (userId == null) {
            throw new RuntimeException("Usuario no encontrado después de crearlo");
        }

        JSONObject password = new JSONObject();
        password.put("type", "password");
        password.put("value", clave);
        password.put("temporary", false);

        String resetUrl = urlServidorAdmin + "/users/" + userId + "/reset-password";

        HttpResponse<String> pwdResponse = Unirest.put(resetUrl)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .body(password.toString())
                .asString();

        if (pwdResponse.getStatus() == 204) {
            System.out.println("🔑 Contraseña asignada correctamente");
        } else {
            System.out.println("❌ Error asignando contraseña: " + pwdResponse.getStatus());
            System.out.println(pwdResponse.getBody());
        }
        return pwdResponse.getStatus();
    }

}
