package utils;

import utils.Dominio.fuente.Fuente;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.Controladores.Controlador;
import utils.DTO.*;
import utils.DTO.ModelosMensajesDTO.*;
import okhttp3.WebSocket;

import java.util.List;
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
        client = new OkHttpClient.Builder().pingInterval(1000, TimeUnit.SECONDS)
                .connectTimeout(100, TimeUnit.SECONDS)    // Timeout de conexión
                .readTimeout(600, TimeUnit.SECONDS)      // ⬅️ AUMENTAR ESTE (5 minutos)
                .writeTimeout(600, TimeUnit.SECONDS)     // ⬅️ Y ESTE (5 minutos)
                .retryOnConnectionFailure(true)
                .build();
    }

    public void conectar(Fuente fuenteDTO) {
        String jsonFuente;
        System.out.println("Conectando al agregador: " + fuenteDTO + "en " + AGREGADOR_URL +"cargador");
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
                try {
                    JsonNode root = mapper.readTree(text);
                    String type = root.get("type").asText();
                    switch (type) {
                        case "obtenerHechos" -> {
                            List<HechoDTO> hechos = controlador.obtenerHechos();
                            HechosObtenidosPayload payload = new HechosObtenidosPayload(hechos);
                            WsMessage<HechosObtenidosPayload> mensaje = new WsMessage<HechosObtenidosPayload>("hechosObtenidos", payload);

                            webSocket.send(mapper.writeValueAsString(mensaje));
                        }
                        case "obtenerSolicitudesModificacion" -> {
                            List<SolicitudDeModificacionDTO> solicitudes = controlador.obtenerSolicitudesModificacion();
                            SolicitudesModificacionObtenidosPayload payload = new SolicitudesModificacionObtenidosPayload(solicitudes);
                            WsMessage<SolicitudesModificacionObtenidosPayload> mensaje = new WsMessage<SolicitudesModificacionObtenidosPayload>("solicitudesModificacionObtenidos", payload);

                            webSocket.send(mapper.writeValueAsString(mensaje));
                        }
                        case "obtenerSolicitudesEliminacion" -> {
                            List<SolicitudDeEliminacionDTO> solicitudes = controlador.obtenerSolicitudesEliminacion();
                            solicitudes.forEach(System.out::println);
                            SolicitudesEliminacionObtenidosPayload payload = new SolicitudesEliminacionObtenidosPayload(solicitudes);
                            WsMessage<SolicitudesEliminacionObtenidosPayload> mensaje = new WsMessage<SolicitudesEliminacionObtenidosPayload>("solicitudesDeEliminacionObtenidos", payload);

                            webSocket.send(mapper.writeValueAsString(mensaje));
                        }
                        case "idCargador" -> {
                            IdCargadorPayload payload = mapper.treeToValue(root.get("payload"), IdCargadorPayload.class);
                            controlador.guardarId(payload.getIdCargador());
                        }
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
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
}

