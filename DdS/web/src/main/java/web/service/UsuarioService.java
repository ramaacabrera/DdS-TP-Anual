package web.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Request;
import okhttp3.Response;
import web.domain.Usuario.Usuario;
import web.domain.Usuario.RolUsuario;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UsuarioService {
    private final String urlPublica;
    private final ObjectMapper mapper = new ObjectMapper();

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
            System.out.println("Usuario: " + user.getId_usuario());
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
}
