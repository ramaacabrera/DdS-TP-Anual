package FuenteProxy;

import utils.DTO.HechoDTO;
import Agregador.HechosYColecciones.ContenidoMultimedia;
import Agregador.Criterios.Criterio;
import Agregador.HechosYColecciones.EstadoHecho;
import Agregador.HechosYColecciones.Ubicacion;
import Agregador.fuente.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.type.TypeReference;
import FuenteProxy.APIMock.HechoMock;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class ConexionDemo extends Conexion{
    protected String urlServicioExterno;
    private final ObjectMapper objectMapper;
    public LocalDateTime fechaUltimaConsulta;
    private FuenteDemo fuenteAsociada;

    public ConexionDemo() {
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule()); // Para manejar LocalDateTime del HechoMock
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Para que LocalDateTime se serialice como string ISO
    }
    // Constructor que permite inicializar la URL del servicio externo y la Fuente asociada
    public ConexionDemo(String urlServicioExterno, FuenteDemo fuenteAsociada) {
        this(); // Llama al constructor por defecto para inicializar ObjectMapper
        this.urlServicioExterno = urlServicioExterno;
        this.fuenteAsociada = fuenteAsociada; // Asigna la fuente asociada
    }

    // Getters y Setters
    public String getUrlServicioExterno() {
        return urlServicioExterno;
    }

    public void setUrlServicioExterno(String urlServicioExterno) {
        this.urlServicioExterno = urlServicioExterno;
    }

    public LocalDateTime getFechaUltimaConsulta() {
        return fechaUltimaConsulta;
    }

    public void setFechaUltimaConsulta(LocalDateTime fechaUltimaConsulta) {
        this.fechaUltimaConsulta = fechaUltimaConsulta;
    }

    public void setFuenteAsociada(FuenteDemo fuenteAsociada) {
        this.fuenteAsociada = fuenteAsociada;
    }

    @Override
    public List<HechoDTO> obtenerHechos(List<Criterio> criterios){
        LocalDateTime fechaParametro;
        if(fechaUltimaConsulta == null) {
            // Si es la primera vez, inicia desde una fecha hace mucho
            fechaParametro = LocalDateTime.of(2000, 1, 1, 0, 0);
        }else{
            fechaParametro = fechaUltimaConsulta;
        }
        this.fechaUltimaConsulta = LocalDateTime.now();
        return obtenerHechos(fechaParametro);
    }

     public List<HechoDTO> obtenerHechos(LocalDateTime fechaUltimaConsulta) {
        Objects.requireNonNull(urlServicioExterno, "La URL del servicio externo no puede ser nula.");
        Objects.requireNonNull(fechaUltimaConsulta, "La fecha de última consulta no puede ser nula.");


        //System.out.println("ConexionDemo: Buscando hechos en " + urlServicioExterno. + " desde " + fechaUltimaConsulta.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        HttpURLConnection connection = null;
        try {
            // Formatear la fecha para la API Mock
            String fechaFormateada = fechaUltimaConsulta.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);;
            String requestUrl = urlServicioExterno + "?fechaDesde=" + fechaFormateada;

            URL url = new URL(requestUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            //System.out.println("ConexionDemo: Status de respuesta: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) { // HTTP 200
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String responseBody = response.toString();
                //System.out.println("ConexionDemo: Respuesta del servicio externo: " + responseBody);

                List<HechoMock> hechosExternos =  objectMapper.readValue(responseBody, new TypeReference<List<HechoMock>>() {});

                if (hechosExternos == null || hechosExternos.isEmpty()) {
                    System.out.println("ConexionDemo: No se encontraron nuevos hechos en la respuesta (lista vacía o nula).");
                    return new ArrayList<>(); // Devolver lista vacía en lugar de null

                }

                //Mapear HechoMock a HechoDTO
                return hechosExternos.stream()
                        .map(hechoMock -> mapHechoMockAHechoDTO(hechoMock))
                        .collect(Collectors.toList());


            } else if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) { // HTTP 204
                //System.out.println("ConexionDemo: No hay nuevos hechos por ahora (HTTP 204 No Content).");
                return null;
            } else {
                String errorBody = "";
                if (connection.getErrorStream() != null) {
                    BufferedReader errorIn = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    String errorLine;
                    StringBuilder errorResponse = new StringBuilder();
                    while ((errorLine = errorIn.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                    errorIn.close();
                    errorBody = errorResponse.toString();
                }
                //System.err.println("ConexionDemo: Error al obtener hechos. Status: " + responseCode + ", Body: " + errorBody);
                return null;
            }
        } catch (Exception e) {
            System.err.println("ConexionDemo: Error de comunicación con el servicio externo: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Metodo para mapear HechoMock (desde la API) a HechoDTO (general de MetaMapa)
    private HechoDTO mapHechoMockAHechoDTO(HechoMock hechoMock) {
        // Manejo de la fecha de acontecimiento: LocalDateTime a Date
        Date fechaDeAcontecimiento = null;
        if (hechoMock.getMockFechaAcontecimiento() != null) {
            fechaDeAcontecimiento = Date.from(hechoMock.getMockFechaAcontecimiento().atZone(ZoneId.systemDefault()).toInstant());
        }

        // Manejo de la ubicación
        Ubicacion ubicacion = null;
        String ubicacionStr = hechoMock.getMockUbicacion(); // Obtener el String de ubicacion

        if (ubicacionStr != null && !ubicacionStr.trim().isEmpty()) {
            // Intentar parsear como Lat/Lon (ej: "Lat: -34.5800, Lon: -58.4200")
            if (ubicacionStr.contains("Lat:") && ubicacionStr.contains("Lon:")) {
                try {
                    double lat = Double.parseDouble(ubicacionStr.split("Lat:")[1].split(",")[0].trim());
                    double lon = Double.parseDouble(ubicacionStr.split("Lon:")[1].trim());
                    ubicacion = new Ubicacion(lat, lon, ubicacionStr); // Usar el constructor con 3 parámetros si existe
                    // O new Ubicacion(lat, lon) si el de 3 no existe
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("ConexionDemo: Error al parsear Lat/Lon para ubicación: " + ubicacionStr + ". Usando como texto simple.");
                    ubicacion = new Ubicacion(ubicacionStr); // Si falla, guardar como texto simple
                }
            } else {
                // Si no es Lat/Lon, asumimos que es un nombre de lugar o dirección
                ubicacion = new Ubicacion(ubicacionStr);
            }
        } else {
            // Si el string de ubicación es nulo o vacío
            System.out.println("ConexionDemo: Ubicación en blanco o vacía para el hecho con ID Externo: " + hechoMock.getMockId() + ". Usando Ubicacion Desconocida.");
            ubicacion = new Ubicacion();//desconocida
        }

        // Etiquetas
        List<String> etiquetas = new ArrayList<>();
        if (hechoMock.getMockCategoria() != null && !hechoMock.getMockCategoria().isEmpty()) {
            etiquetas.add(hechoMock.getMockCategoria().toLowerCase());
        }
        if (hechoMock.getMockOrigen() != null && !hechoMock.getMockOrigen().isEmpty()) {
            etiquetas.add("origen-" + hechoMock.getMockOrigen().toLowerCase().replace(" ", "-"));
        }
        if (hechoMock.getcampoExtra() != null && !hechoMock.getcampoExtra().isEmpty()) {
            etiquetas.add("extra-" + hechoMock.getcampoExtra().toLowerCase().replace(" ", "-"));
        }
        etiquetas.add("fuente-demo");

        Date fechaDeCarga = new Date();
        EstadoHecho estadoHecho = EstadoHecho.ACTIVO;
        // Contribuyente nulo por ahora para hechos externos
        List<ContenidoMultimedia> contenidoMultimedia = new ArrayList<>();
        boolean esEditable = false;
        FuenteDemo fuente = fuenteAsociada;

        // Validar título antes de crear DTO
        String tituloDTO = hechoMock.getMockTitulo();
        if (tituloDTO == null || tituloDTO.trim().isEmpty()) {
            //System.err.println("ConexionDemo: HechoMock sin título (ID: " + hechoMock.getMockId() + "). Usando ID como título.");
            tituloDTO = "Hecho Externo (ID: " + hechoMock.getMockId() + ")";
        }

        return new HechoDTO(
                tituloDTO,
                hechoMock.getMockDescripcion(),
                hechoMock.getMockCategoria(),
                ubicacion,
                fechaDeAcontecimiento,
                fechaDeCarga,
                fuente,
                estadoHecho,
                null, // Contribuyente
                etiquetas,
                esEditable,
                contenidoMultimedia
        );
    }

}