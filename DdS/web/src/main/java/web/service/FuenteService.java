package web.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import web.dto.Hechos.FuenteDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class FuenteService {
    private final String urlAdmin;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newHttpClient();

    public FuenteService(String urlPublica) {
        this.urlAdmin = urlPublica;
    }

    public List<FuenteDTO> listarFuentes() {
        try {
            String finalUrl = urlAdmin + "/fuentes";

            System.out.println("--- DEBUG SERVICE ---");
            System.out.println("Solicitando fuentes a: " + finalUrl);

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(new URI(finalUrl))
                    .GET();

            HttpRequest request = builder.build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Respuesta Backend Código: " + response.statusCode());

            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<List<FuenteDTO>>() {});
            }

            return Collections.emptyList();

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}