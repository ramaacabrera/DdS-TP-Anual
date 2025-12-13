package agregador.service;

import agregador.service.normalizacion.ServicioGeoref;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioGeorefTest {

    @Mock private HttpClient httpClient;
    @Mock private HttpResponse<String> httpResponse;
    private ServicioGeoref servicio;

    @BeforeEach
    void setUp() {
        servicio = new ServicioGeoref(httpClient, new ObjectMapper());
    }

    @Test
    @DisplayName("Debe parsear correctamente Municipio y Provincia")
    void testParseoCompleto() throws IOException, InterruptedException {
        String json = "{\"ubicacion\": {\"municipio_nombre\": \"Rosario\", \"provincia_nombre\": \"Santa Fe\"}}";

        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(json);

        doReturn(httpResponse).when(httpClient).send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

        String resultado = servicio.obtenerDescripcionPorCoordenadas(-32.0, -60.0);

        assertEquals("Rosario, Santa Fe", resultado);
    }

    @Test
    @DisplayName("Debe parsear solo Provincia si no hay municipio")
    void testParseoSoloProvincia() throws IOException, InterruptedException {
        String json = "{\"ubicacion\": {\"municipio_nombre\": null, \"provincia_nombre\": \"Córdoba\"}}";

        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(json);

        doReturn(httpResponse).when(httpClient).send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

        String resultado = servicio.obtenerDescripcionPorCoordenadas(-31.0, -64.0);

        assertEquals("Córdoba", resultado);
    }

    @Test
    @DisplayName("Debe manejar error 500 de la API")
    void testErrorApi() throws IOException, InterruptedException {
        when(httpResponse.statusCode()).thenReturn(500);

        doReturn(httpResponse).when(httpClient).send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

        assertNull(servicio.obtenerDescripcionPorCoordenadas(-34.0, -58.0));
    }

    @Test
    @DisplayName("Debe manejar excepción de red (Timeout)")
    void testExcepcionRed() throws IOException, InterruptedException {
        when(httpClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenThrow(new IOException("Timeout"));

        assertNull(servicio.obtenerDescripcionPorCoordenadas(-34.0, -58.0));
    }

    @Test
    @DisplayName("Validación: Coordenadas 0,0 retorna null rápido")
    void testCoordenadasCero() {
        assertNull(servicio.obtenerDescripcionPorCoordenadas(0, 0));
        verifyNoInteractions(httpClient);
    }
}