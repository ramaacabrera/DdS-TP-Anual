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
    @DisplayName("nuevaConexion: Debe crear fuente si no existe y devolver JSON con ID")
    void testNuevaConexion_FuenteNueva() {
        // Arrange
        String sessionId = "sess-123";
        Fuente fuenteEntrante = new Fuente();
        fuenteEntrante.setDescriptor("mi-fuente-nueva");
        WsConnectContext ctx = mock(WsConnectContext.class);

        // Simulamos que no existe
        when(fuenteRepositorio.buscarPorDescriptor("mi-fuente-nueva")).thenReturn(null);

        // Simulamos el guardado (que asignaría un ID)
        Fuente fuenteGuardada = new Fuente();
        fuenteGuardada.setId(UUID.randomUUID());
        fuenteGuardada.setDescriptor("mi-fuente-nueva");
        when(fuenteRepositorio.guardar(fuenteEntrante)).thenReturn(fuenteGuardada);

        // Act
        String jsonRespuesta = service.nuevaConexion(sessionId, fuenteEntrante, ctx);

        // Assert
        assertNotNull(jsonRespuesta);
        assertTrue(jsonRespuesta.contains(fuenteGuardada.getId().toString())); // Verifica que el JSON tenga el ID
        verify(conexionCargadorRepositorio).registrarFuentePorSession(sessionId, fuenteGuardada.getId(), ctx);
    }

    @Test
    @DisplayName("nuevaConexion: Debe reusar fuente si ya existe")
    void testNuevaConexion_FuenteExistente() {
        // Arrange
        String sessionId = "sess-456";
        Fuente fuenteEntrante = new Fuente();
        fuenteEntrante.setDescriptor("fuente-existente");
        WsConnectContext ctx = mock(WsConnectContext.class);

        Fuente fuenteExistente = new Fuente();
        fuenteExistente.setId(UUID.randomUUID());
        when(fuenteRepositorio.buscarPorDescriptor("fuente-existente")).thenReturn(fuenteExistente);

        // Act
        String jsonRespuesta = service.nuevaConexion(sessionId, fuenteEntrante, ctx);

        // Assert
        verify(fuenteRepositorio, never()).guardar(any()); // No debe guardar de nuevo
        assertTrue(jsonRespuesta.contains(fuenteExistente.getId().toString()));
        verify(conexionCargadorRepositorio).registrarFuentePorSession(sessionId, fuenteExistente.getId(), ctx);
    }

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
    @DisplayName("nuevaConexion: Excepción en repositorio se relanza como Runtime")
    void testNuevaConexion_FalloCritico() {
        when(fuenteRepositorio.buscarPorDescriptor(any())).thenThrow(new RuntimeException("DB Error"));

        assertThrows(RuntimeException.class, () ->
                service.nuevaConexion("s1", new Fuente(), mock(WsConnectContext.class))
        );
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