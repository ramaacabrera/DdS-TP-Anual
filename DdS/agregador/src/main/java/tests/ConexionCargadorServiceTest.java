package tests;

import agregador.service.ConexionCargadorService;
import agregador.repository.ConexionCargadorRepositorio;
import agregador.repository.FuenteRepositorio;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConexionCargadorServiceTest {

    @Test
    void testBorrarFuentePorSessionCuandoExiste() {
        ConexionCargadorRepositorio repo = mock(ConexionCargadorRepositorio.class);
        FuenteRepositorio fuenteRepo = mock(FuenteRepositorio.class);

        ConexionCargadorService service = new ConexionCargadorService(fuenteRepo, repo);

        UUID id = UUID.randomUUID();
        when(repo.borrarFuentePorSession("abc")).thenReturn(id);

        boolean resultado = service.borrarFuentePorSession("abc");

        assertTrue(resultado);
        verify(repo).borrarFuentePorSession("abc");
    }

    @Test
    void testBorrarFuentePorSessionConSessionNull() {
        ConexionCargadorRepositorio repo = mock(ConexionCargadorRepositorio.class);
        ConexionCargadorService service = new ConexionCargadorService(null, repo);

        boolean resultado = service.borrarFuentePorSession(null);

        assertFalse(resultado);
        verify(repo, never()).borrarFuentePorSession(any());
}
}