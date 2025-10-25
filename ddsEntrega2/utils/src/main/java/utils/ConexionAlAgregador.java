package utils;

import utils.DTO.HechosYColecciones.TipoDeFuente;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.DTO.FuenteDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

public class ConexionAlAgregador {
    private final String puertoAgregador;

    public ConexionAlAgregador() {
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        puertoAgregador = config.getProperty("puertoAgregador");
    }

    public void conectarse(TipoDeFuente tipo, String puerto) throws JsonProcessingException {
        FuenteDTO self = new  FuenteDTO(tipo, "http://localhost:" + puerto);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(self);


        String url = "http://localhost:"+puertoAgregador+"/cargador";

        HttpClient cliente = HttpClient.newBuilder()
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());


            if (response.statusCode() == 200 || response.statusCode() == 201) {
                System.out.println("Fuente registrada exitosamente");
            } else if (response.statusCode() == 409) {
                System.out.println("La agregador.fuente ya existe en el agregador");
            } else {
                System.out.println("Error al registrar agregador.fuente: " + response.statusCode());
            }

        } catch (Exception e) {
            System.err.println("Error en la conexión: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
