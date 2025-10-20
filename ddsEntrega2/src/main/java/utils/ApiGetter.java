package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiGetter {
    public <T> T getFromApi(String url, TypeReference<T> typeRef){

        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
        try {
            HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                String errorMsg = "Servicio remoto devolvió un error (Status: " + response.statusCode() + "). Cuerpo: " + response.body();


                System.err.println("ERROR API: " + errorMsg);

                // Devolvemos NULL o una lista vacía para que el llamador (ConexionCargador) lo maneje.
                throw new RuntimeException("Fallo al obtener datos de la fuente. Estado HTTP: " + response.statusCode());
            }

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.body(), typeRef);

        } catch (java.net.ConnectException e) {
            // Manejar el "Connection Refused" como fallo de red (para reintentos/tolerancia)
            throw new RuntimeException("Fallo de red: El servicio remoto no está en línea.", e);
        } catch (com.fasterxml.jackson.core.JsonParseException | com.fasterxml.jackson.databind.exc.MismatchedInputException e) {
            // Capturar errores específicos de formato JSON.
            System.err.println("Error de formato JSON en respuesta de " + url + ": " + e.getMessage());
            throw new RuntimeException("Formato JSON inválido recibido.", e);
        } catch (Exception e) {
            // Manejar otros errores
            System.err.println("Error desconocido al consultar API (" + url + "): " + e.getMessage());
            throw new RuntimeException("Error de I/O o desconocido.", e);
        }
    }
}