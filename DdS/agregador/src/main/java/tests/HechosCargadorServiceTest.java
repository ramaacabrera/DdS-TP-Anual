/*
package tests;

import agregador.service.HechosCargadorService;
import agregador.repository.ConexionCargadorRepositorio;
import agregador.repository.FuenteRepositorio;
import io.javalin.websocket.WsContext;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.*;




import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.*;

class HechosCargadorServiceTest {

    @Test
    void testObtenerHechosEnvíaMensajeAWebsockets() {
        ConexionCargadorRepositorio repo = mock(ConexionCargadorRepositorio.class);
        FuenteRepositorio fuenteRepo = mock(FuenteRepositorio.class);

        WsContext ctx = mock(WsContext.class);

        ConcurrentHashMap<UUID, WsContext> mapa = new ConcurrentHashMap<>();
        mapa.put(UUID.randomUUID(), ctx);

        when(repo.obtenerFuentesCtxs()).thenReturn(mapa);

        HechosCargadorService service = new HechosCargadorService(fuenteRepo, repo);

        service.obtenerHechosNuevos();

        verify(ctx, times(1)).send(anyString());
}
}
*/
