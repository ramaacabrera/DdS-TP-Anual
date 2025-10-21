package utils;

import Agregador.fuente.Fuente;
import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.Controladores.Controlador;
import utils.DTO.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ClienteDelAgregador {
    private static String AGREGADOR_URL;
    private Controlador controlador;
    private OkHttpClient client;
    private WebSocket socket;

    ObjectMapper mapper = new ObjectMapper();

    public ClienteDelAgregador(String url, Controlador contr) {
        controlador = contr;
        AGREGADOR_URL = url;
        client = new OkHttpClient.Builder().pingInterval(10, TimeUnit.SECONDS).build();
    }

    public void conectar(FuenteDTO fuenteDTO) {
        String jsonFuente;
        System.out.println("Conectando al agregador: " + fuenteDTO + "en " + AGREGADOR_URL+"cargador");
        try{
            jsonFuente = mapper.writeValueAsString(fuenteDTO);
        }
        catch (Exception e) {
            jsonFuente = "";
            e.printStackTrace();
        }
        Request request = new Request.Builder()
                .url(AGREGADOR_URL+"cargador")
                .header("FuenteDTO", jsonFuente)  // Aquí enviamos la clase FuenteDTO en el header
                .build();

        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                System.out.println("Conexión establecida con el servidor");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                System.out.println("Mensaje recibido: " + text);
                // Procesa el mensaje recibido del servidor
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                System.out.println("Cerrando la conexión: " + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                System.out.println("Error en la conexión: " + t.getMessage());
            }
        };

        socket = client.newWebSocket(request, listener);
    }

    public void enviarMensaje(String mensaje) {
        if (socket != null) {
            socket.send(mensaje);  // Enviar mensaje al servidor
        }
    }

    public void cerrar() {
        if (socket != null) {
            socket.close(1000, "Cerrando conexión");
        }
    }

    /*
    public static void main(String[] args) {
        CargadorWebSocketClient cliente = new CargadorWebSocketClient();
        cliente.conectar();
        cliente.enviarMensaje("Hola desde el cargador");
    }
    */
}

