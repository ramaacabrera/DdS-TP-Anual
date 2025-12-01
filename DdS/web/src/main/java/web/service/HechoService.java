package web.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import web.dto.Hechos.HechoDTO;
import web.dto.PageDTO;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HechoService {

    private final String urlPublica;
    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public HechoService(String urlPublica) {
        this.urlPublica = urlPublica;
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();
    }

    public HechoDTO obtenerHechoPorId(String hechoIdString) {
        // Construimos la URL de forma segura
        HttpUrl url = HttpUrl.parse(urlPublica + "/hechos/" + hechoIdString);
        if (url == null) {
            System.err.println("❌ URL inválida para hecho ID: " + hechoIdString);
            return null;
        }

        Request request = new Request.Builder().url(url).get().build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("❌ Error API al obtener hecho: " + response.code() + " " + response.message());
                return null;
            }

            String body = Objects.requireNonNull(response.body()).string();

            return mapper.readValue(body, HechoDTO.class);

        } catch (Exception e) {
            System.err.println("❌ Excepción al obtener el hecho: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}