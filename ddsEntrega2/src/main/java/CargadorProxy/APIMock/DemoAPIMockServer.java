package CargadorProxy.APIMock;

import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class DemoAPIMockServer {
    private static final int PORT = 7000;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final AtomicInteger factIdCounter = new AtomicInteger(100);
    private static LocalDateTime lastGeneratedTime = LocalDateTime.now().minusHours(2);

    public static void main(String[] args) {
        // Configuración de ObjectMapper para Jackson
        ObjectMapper objectMapper = createObjectMapper();

        // Inicializar Javalin con el ObjectMapper personalizado
        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(objectMapper));
            config.showJavalinBanner = false;
            // Se eliminó la configuración CORS
        }).start(PORT);

        System.out.println("API Mock de Fuente Demo iniciada en http://localhost:" + PORT);
        System.out.println("Generando hechos simulados...");

        setupEndpoints(app);
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    private static void setupEndpoints(Javalin app) {
        // Endpoint para obtener una LISTA de hechos
        app.get("/api/hechos", ctx -> {
            String fechaDesdeStr = ctx.queryParam("fechaDesde");
            LocalDateTime fechaDesde = parseFechaDesde(fechaDesdeStr, ctx);
            if (fechaDesde == null && fechaDesdeStr != null) {
                return; // Ya se envió respuesta de error
            }

            List<HechoMock> nuevosHechos = generarHechosSiEsNecesario(fechaDesde);

            if (nuevosHechos.isEmpty()) {
                System.out.println("Mock API: Solicitud desde " + (fechaDesde != null ? fechaDesde.format(FORMATTER) : "N/A") + ". No hay nuevos hechos en este momento. Enviando 204.");
                ctx.status(204); // No Content
            } else {
                System.out.println("Mock API: Solicitud desde " + (fechaDesde != null ? fechaDesde.format(FORMATTER) : "N/A") + ". Enviando " + nuevosHechos.size() + " nuevos hechos.");
                ctx.json(nuevosHechos);
            }
        });

        // Simula un hecho aleatorio
        app.get("/api/hecho/random", ctx -> ctx.json(generarHechoAleatorio(LocalDateTime.now())));

        // Endpoint de salud para verificar que el servidor está funcionando
        app.get("/health", ctx -> ctx.result("OK"));
    }

    private static LocalDateTime parseFechaDesde(String fechaDesdeStr, io.javalin.http.Context ctx) {
        if (fechaDesdeStr != null) {
            try {
                return LocalDateTime.parse(fechaDesdeStr, FORMATTER);
            } catch (Exception e) {
                System.err.println("Mock API: Formato de fecha inválido: " + fechaDesdeStr);
                ctx.status(400).result("Formato de fecha inválido. Use ISO_LOCAL_DATE_TIME (yyyy-MM-ddTHH:mm:ss).");
                return null;
            }
        }
        return null;
    }

    private static List<HechoMock> generarHechosSiEsNecesario(LocalDateTime fechaDesde) {
        List<HechoMock> nuevosHechos = new ArrayList<>();
        Random random = new Random();

        // Simula la generación de hechos "nuevos"
        LocalDateTime now = LocalDateTime.now();
        if (fechaDesde == null || now.isAfter(lastGeneratedTime)) {
            int numHechos = random.nextInt(3) + 1; // Genera entre 1 y 3 hechos cada vez
            for (int i = 0; i < numHechos; i++) {
                nuevosHechos.add(generarHechoAleatorio(now.plusMinutes(random.nextInt(5))));
            }
            lastGeneratedTime = now;
        }
        return nuevosHechos;
    }

    private static HechoMock generarHechoAleatorio(LocalDateTime fechaAcontecimiento) {
        String[] categorias = {"Delito", "Accidente", "Fenómeno Natural", "Evento Social", "Incidente"};
        String[] descripciones = {
                "Un incidente menor fue reportado.",
                "Se registró una actividad inusual.",
                "Las autoridades están investigando.",
                "Gran impacto en la comunidad local.",
                "Situación bajo control."
        };
        String[] ubicaciones = {
                "Lat: -34.5800, Lon: -58.4200", // CABA
                "Av. Corrientes 1234, Buenos Aires",
                "Lat: -34.9213, Lon: -57.9545", // La Plata
                "Centro de la ciudad",
                "Zona rural, a 10km del pueblo"
        };
        String[] origenes = {"Policía", "Vecinos", "Medios", "Testigo"};
        String[] extraData = {"", "Urgente", "Verificado", "En Curso"};

        Random random = new Random();
        String id = "EXT-" + factIdCounter.getAndIncrement();
        String titulo = categorias[random.nextInt(categorias.length)] + " - " + id;
        String descripcion = descripciones[random.nextInt(descripciones.length)];
        String categoria = categorias[random.nextInt(categorias.length)];
        String ubicacion = ubicaciones[random.nextInt(ubicaciones.length)];
        String origen = origenes[random.nextInt(origenes.length)];
        String extra = extraData[random.nextInt(extraData.length)];

        return new HechoMock(id, titulo, descripcion, categoria, ubicacion, fechaAcontecimiento, origen, extra);
    }
}