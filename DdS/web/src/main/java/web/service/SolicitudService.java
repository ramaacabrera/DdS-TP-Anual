package web.service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
//import utils.Dominio.HechosYColecciones.Hecho;
import web.domain.HechosYColecciones.Hecho;
import web.domain.HechosYColecciones.HechoModificado;
import web.domain.Solicitudes.SolicitudDeEliminacion;
import web.domain.Solicitudes.SolicitudDeModificacion;
import web.dto.Hechos.HechoDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SolicitudService {
    private final String urlAdmin;
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final HechoService hechoService;
    //private final SolicitudRepositorio solicitudRepo;

    public SolicitudService(String urlAdmin, HechoService hechoService) {
        this.urlAdmin = urlAdmin;
        this.hechoService = hechoService;
        //this.solicitudRepo = solicitudRepo;
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();
    }

    public SolicitudDeEliminacion obtenerSolicitudEliminacion(String id) {
        String url = urlAdmin + "/api/solicitudes/" + id;

        return executeRequest(url, SolicitudDeEliminacion.class);
    }

    public SolicitudDeModificacion obtenerSolicitudModificacion(String id) {
        String url = urlAdmin + "/api/solicitudes/modificacion/" + id;

        return executeRequest(url, SolicitudDeModificacion.class);
    }

    private <T> T executeRequest(String url, Class<T> valueType) {
        Request request = new Request.Builder().url(url).get().build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String body = response.body().string();
                return mapper.readValue(body, valueType);
            } else {
                System.err.println("Error API Admin (" + response.code() + ") en URL: " + url);
                return null;
            }
        } catch (IOException e) {
            System.err.println("Excepción conectando al Admin: " + e.getMessage());
            return null;
        }
    }

    public List<SolicitudDeEliminacion> obtenerSolicitudesEliminacion() {
        String apiUrl = urlAdmin + "/api/solicitudes";
        System.out.println("DEBUG: Intentando obtener solicitudes de: " + apiUrl);

        Request request = new Request.Builder()
                .url(apiUrl)
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            System.out.println("DEBUG: Código de respuesta de la API Admin: " + response.code());

            if (response.isSuccessful()) {
                String body = response.body().string();
                System.out.println("DEBUG: Cuerpo de respuesta (JSON recibido): " + (body.length() > 200 ? body.substring(0, 200) + "..." : body));

                // Manejo de errores de Deserialización
                try {
                    List<SolicitudDeEliminacion> lista = mapper.readValue(body, new TypeReference<List<SolicitudDeEliminacion>>() {});
                    System.out.println("DEBUG: Solicitudes encontradas (Deserializadas): " + lista.size());
                    return lista;
                } catch (Exception e) {
                    System.err.println("❌ ERROR CRÍTICO AL DESERIALIZAR JSON de la API Admin: " + e.getMessage());
                    e.printStackTrace();
                    return new ArrayList<>(); // Retorna lista vacía si falla el JSON
                }
            } else {
                System.err.println("❌ Error HTTP en la API Admin: " + response.code() + " - Mensaje: " + response.message());
                return new ArrayList<>(); // Retorna lista vacía si falla la red
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<SolicitudDeModificacion> obtenerSolicitudesModificacion() {
        try {
            String urlCompleta = urlAdmin + "api/solicitudes";
            System.out.println("=== DEBUG HTTP REQUEST ===");
            System.out.println("🔍 URL Admin base: " + urlAdmin);
            System.out.println("🔍 URL completa: " + urlCompleta);

            Request request = new Request.Builder()
                    .url(urlAdmin + "/api/solicitudes-modificacion") // Ajusta el endpoint
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    return mapper.readValue(body, new TypeReference<List<SolicitudDeModificacion>>() {});
                }
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo solicitudes de modificación: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public int actualizarEstadoSolicitud(String id, String tipo, String accion){
        String esModificacion = tipo.equals("modificacion")?"modificacion/":"";
        String url = urlAdmin + "/api/solicitudes/"+ esModificacion + id;
        if (tipo.equals("modificacion") && accion.equals("ACEPTADA")) {

            // 1. Traer solicitud
            SolicitudDeModificacion solicitud = obtenerSolicitudModificacion(id);
            if(solicitud == null) return 404;

            // 2. Traer el hecho original
            UUID idHecho = solicitud.getHechoModificado().getHecho_id();
            HechoDTO original = hechoService.obtenerHechoPorId(String.valueOf(idHecho));

            // 3. Tomar el hecho modificado desde la solicitud
            HechoModificado nuevo = solicitud.getHechoModificado();

            // 4. Aplicar cambios (solo campos editables)
            original.setTitulo(nuevo.getTitulo());
            original.setDescripcion(nuevo.getDescripcion());
            original.setCategoria(nuevo.getCategoria());
            //original.setUbicacion(nuevo.getUbicacion());
            original.setFechaDeAcontecimiento(nuevo.getFechaDeAcontecimiento());
            original.setFechaDeCarga(nuevo.getFechaDeCarga());
            //original.setFuente(nuevo.getFuente());
            //original.setEstadoHecho(nuevo.getEstadoHecho());
            //original.setContribuyente(nuevo.getContribuyente());
            //original.setEtiquetas(nuevo.getEtiquetas());
            //original.setContenidoMultimedia(nuevo.getContenidoMultimedia());



            // 5. Persistir
            hechoService.actualizarHecho(original);

            // 6. Cambiar estado
            //solicitud.setEstado("ACEPTADA");
            //solicitudRepo.actualizar(solicitud);
        }

        try {
            String jsonBody = mapper.writeValueAsString(Map.of("accion", accion));

            RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));

            Request request = new Request.Builder().url(url).patch(body).build();

            try (Response response = client.newCall(request).execute()) {
                return response.code();
            }
        } catch (Exception e) {
            System.err.println("Error actualizando estado de solicitud: " + e.getMessage());
            return 500;
        }
    }
}
