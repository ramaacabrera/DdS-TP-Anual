package agregador.service;

import agregador.domain.fuente.Fuente;
import agregador.repository.ConexionCargadorRepositorio;
import agregador.repository.FuenteRepositorio;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConexionCargadorServiceTest {

    @Mock private FuenteRepositorio fuenteRepositorio;
    @Mock private ConexionCargadorRepositorio conexionCargadorRepositorio;

    @InjectMocks
    private ConexionCargadorService service;

    @Test
    @DisplayName("borrarFuentePorSession: Devuelve true si la sesión existía")
    void testBorrarFuenteExitosa() {
        String sessionId = "sess-1";
        UUID fuenteId = UUID.randomUUID();
        when(conexionCargadorRepositorio.borrarFuentePorSession(sessionId)).thenReturn(fuenteId);

        boolean resultado = service.borrarFuentePorSession(sessionId);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("borrarFuentePorSession: Devuelve false si session es null o no existe")
    void testBorrarFuenteFallida() {
        assertFalse(service.borrarFuentePorSession(null));

        when(conexionCargadorRepositorio.borrarFuentePorSession("fake")).thenReturn(null);
        assertFalse(service.borrarFuentePorSession("fake"));
    }

    @Test
    @DisplayName("borrarFuente (ID): Exitoso")
    void testBorrarFuentePorId_Exito() {
        UUID id = UUID.randomUUID();
        when(conexionCargadorRepositorio.borrarFuente(id)).thenReturn(true);

        assertTrue(service.borrarFuente(id));
    }

    @Test
    @DisplayName("borrarFuente (ID): ID Nulo retorna false")
    void testBorrarFuentePorId_Nulo() {
        assertFalse(service.borrarFuente(null));
        verify(conexionCargadorRepositorio, never()).borrarFuente(any());
    }

    @Test
    @DisplayName("borrarFuente (ID): No encontrado en repositorio")
    void testBorrarFuentePorId_NoEncontrado() {
        UUID id = UUID.randomUUID();
        when(conexionCargadorRepositorio.borrarFuente(id)).thenReturn(false);
        assertFalse(service.borrarFuente(id));
    }

    @Test
    @DisplayName("obtenerFuenteIdPorSession: Delegación correcta")
    void testObtenerFuenteId() {
        String sess = "s1";
        UUID id = UUID.randomUUID();
        when(conexionCargadorRepositorio.obtenerFuenteIdPorSession(sess)).thenReturn(id);

        assertEquals(id, service.obtenerFuenteIdPorSession(sess));
    }

    @Test
    @DisplayName("mostrarSesionesActivas: No explota")
    void testMostrarSesiones() {
        when(conexionCargadorRepositorio.obtenerFuentesIDs()).thenReturn(new ConcurrentHashMap<>());
        assertDoesNotThrow(() -> service.mostrarSesionesActivas());
    }

    @Test
    @DisplayName("agregarFuente: Delega al repositorio")
    void testAgregarFuenteLegacy() {
        UUID id = UUID.randomUUID();
        WsContext ctx = mock(WsContext.class);
        service.agregarFuente(id, ctx);
        verify(conexionCargadorRepositorio).agregarFuente(id, ctx);
    }
}