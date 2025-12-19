package web.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import web.dto.Hechos.FuenteDTO;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FuenteService {
    private final String urlAdmin;
    private final ObjectMapper mapper = new ObjectMapper();
    private final OkHttpClient http;

    public FuenteService(String urlPublica) {
        this.urlAdmin = urlPublica;
        this.http = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public List<FuenteDTO> listarFuentes() {
        try {
            String finalUrl = urlAdmin + "/fuentes";

            System.out.println("--- DEBUG SERVICE ---");
            System.out.println("Solicitando fuentes a: " + finalUrl);

            Request request = new Request.Builder()
                    .url(finalUrl)
                    .get()
                    .build();

            try (Response response = http.newCall(request).execute()) {
                System.out.println("Respuesta Backend Código: " + response.code());

                if (response.code() == 200 && response.body() != null) {
                    return mapper.readValue(response.body().string(), new TypeReference<List<FuenteDTO>>() {});
                }
            }

            return Collections.emptyList();

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}