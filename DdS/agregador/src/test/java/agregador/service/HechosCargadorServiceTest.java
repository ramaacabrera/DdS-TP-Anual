package agregador.service;

import agregador.domain.fuente.Fuente;
import agregador.domain.fuente.TipoDeFuente;
import agregador.repository.ConexionCargadorRepositorio;
import agregador.repository.FuenteRepositorio;
import io.javalin.websocket.WsContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HechosCargadorServiceTest {

    @Mock
    private FuenteRepositorio fuenteRepositorio;

    @Mock
    private ConexionCargadorRepositorio conexionCargadorRepositorio;

    @InjectMocks
    private HechosCargadorService service;

    @Test
    @DisplayName("Debe enviar mensaje 'obtenerHechos' a todas las fuentes conectadas")
    void testObtenerHechosNuevos() {
        // Arrange
        UUID fuenteId1 = UUID.randomUUID();
        WsContext ctxMock = mock(WsContext.class);

        ConcurrentMap<UUID, WsContext> mapaSimulado = new ConcurrentHashMap<>();
        mapaSimulado.put(fuenteId1, ctxMock);

        when(conexionCargadorRepositorio.obtenerFuentesCtxs()).thenReturn(mapaSimulado);

        // Act
        service.obtenerHechosNuevos();

        // Assert
        verify(ctxMock, times(1)).send(contains("obtenerHechos"));
    }

    @Test
    @DisplayName("Si no hay fuentes conectadas, no debe fallar ni enviar nada")
    void testObtenerHechosNuevos_SinFuentes() {
        when(conexionCargadorRepositorio.obtenerFuentesCtxs()).thenReturn(new ConcurrentHashMap<>());

        service.obtenerHechosNuevos();
    }

    @Test
    @DisplayName("obtenerSolicitudes: No hace nada si mapa vacío")
    void testObtenerSolicitudes_MapaVacio() {
        when(conexionCargadorRepositorio.obtenerFuentesCtxs()).thenReturn(new ConcurrentHashMap<>());
        service.obtenerSolicitudes();
        verifyNoInteractions(fuenteRepositorio);
    }

    @Test
    @DisplayName("Verificar contenido del JSON enviado en obtenerHechos")
    void testContenidoMensajeHechos() {
        // Arrange
        UUID id = UUID.randomUUID();
        WsContext ctx = mock(WsContext.class);
        ConcurrentHashMap<UUID, WsContext> map = new ConcurrentHashMap<>();
        map.put(id, ctx);

        when(conexionCargadorRepositorio.obtenerFuentesCtxs()).thenReturn(map);

        // Act
        service.obtenerHechosNuevos();

        verify(ctx).send((String) argThat(msg -> msg != null && ((String) msg).contains("\"type\":\"obtenerHechos\"")));
    }
}