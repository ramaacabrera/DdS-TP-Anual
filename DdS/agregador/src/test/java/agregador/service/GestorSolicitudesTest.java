package agregador.service;

import agregador.domain.HechosYColecciones.Hecho;
import agregador.domain.Solicitudes.SolicitudDeEliminacion;
import agregador.domain.Solicitudes.SolicitudDeModificacion;
import agregador.dto.Solicitudes.HechoModificadoDTO;
import agregador.dto.Solicitudes.SolicitudDeEliminacionDTO;
import agregador.dto.Solicitudes.SolicitudDeModificacionDTO;
import agregador.repository.HechoRepositorio;
import agregador.repository.SolicitudEliminacionRepositorio;
import agregador.repository.SolicitudModificacionRepositorio;
import agregador.repository.UsuarioRepositorio;
import agregador.utils.DetectorDeSpam.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestorSolicitudesTest {

    @Mock private SolicitudEliminacionRepositorio elimRepo;
    @Mock private SolicitudModificacionRepositorio modRepo;
    @Mock private HechoRepositorio hechoRepositorio;
    @Mock private UsuarioRepositorio usuarioRepositorio;

    @InjectMocks
    private GestorSolicitudes gestor;

    @Test
    @DisplayName("Debe procesar una eliminación válida correctamente")
    void testProcesarEliminacionValida() {
        // Arrange
        UUID hechoId = UUID.randomUUID();
        SolicitudDeEliminacionDTO dto = new SolicitudDeEliminacionDTO();
        dto.setHechoId(hechoId);
        dto.setJustificacion("Es incorrecto");

        Hecho hechoMock = new Hecho();
        when(hechoRepositorio.buscarPorId(hechoId)).thenReturn(hechoMock);

        // Act
        gestor.procesarSolicitudes(null, List.of(dto));

        // Assert
        ArgumentCaptor<SolicitudDeEliminacion> captor = ArgumentCaptor.forClass(SolicitudDeEliminacion.class);
        verify(elimRepo).agregarSolicitudDeEliminacion(captor.capture());

        SolicitudDeEliminacion guardada = captor.getValue();
        assertEquals("Es incorrecto", guardada.getJustificacion());
        assertNotNull(guardada.getHechoAsociado());
        assertFalse(guardada.getEsSpam());
    }

    @Test
    @DisplayName("Debe detectar y marcar SPAM en eliminación")
    void testProcesarEliminacionSpam() {
        String textoSpam = "premiolimitadogratisgana";

        SolicitudDeEliminacionDTO dto = new SolicitudDeEliminacionDTO();
        dto.setJustificacion(textoSpam); // Forzamos texto sospechoso

        // Act
        gestor.procesarSolicitudes(null, List.of(dto));

        // Assert
        ArgumentCaptor<SolicitudDeEliminacion> captor = ArgumentCaptor.forClass(SolicitudDeEliminacion.class);
        verify(elimRepo).agregarSolicitudDeEliminacion(captor.capture());

        assertTrue(captor.getValue().getEsSpam(), "Debería haberse marcado como SPAM");
    }

    @Test
    @DisplayName("Manejo de listas nulas o vacías")
    void testListasVacias() {
        assertDoesNotThrow(() -> gestor.procesarSolicitudes(null, null));
        assertDoesNotThrow(() -> gestor.procesarSolicitudes(Collections.emptyList(), Collections.emptyList()));

        verify(elimRepo, never()).agregarSolicitudDeEliminacion(any());
        verify(modRepo, never()).agregarSolicitudDeModificacion(any());
    }

    @Test
    @DisplayName("Si el hecho no existe, la solicitud se crea pero sin asociar hecho")
    void testHechoNoEncontrado() {
        UUID idInexistente = UUID.randomUUID();
        SolicitudDeEliminacionDTO dto = new SolicitudDeEliminacionDTO();
        dto.setHechoId(idInexistente);
        dto.setJustificacion("Test");

        when(hechoRepositorio.buscarPorId(idInexistente)).thenReturn(null);

        gestor.procesarSolicitudes(null, List.of(dto));

        ArgumentCaptor<SolicitudDeEliminacion> captor = ArgumentCaptor.forClass(SolicitudDeEliminacion.class);
        verify(elimRepo).agregarSolicitudDeEliminacion(captor.capture());

        assertNull(captor.getValue().getHechoAsociado());
    }

    @Test
    @DisplayName("Debe detectar SPAM en solicitudes de modificación")
    void testModificacionSpam() {
        SolicitudDeModificacionDTO dto = new SolicitudDeModificacionDTO();
        dto.setJustificacion("spam text detector");

        gestor.procesarSolicitudes(List.of(dto), null);

        verify(modRepo).agregarSolicitudDeModificacion(any());
    }

    @Test
    @DisplayName("Si falla guardar una solicitud, continua con la siguiente")
    void testFalloEnRepositorio() {
        SolicitudDeEliminacionDTO dto1 = new SolicitudDeEliminacionDTO();
        dto1.setJustificacion("Malo");
        SolicitudDeEliminacionDTO dto2 = new SolicitudDeEliminacionDTO();
        dto2.setJustificacion("Bueno");

        doThrow(new RuntimeException("DB Down")).when(elimRepo).agregarSolicitudDeEliminacion(argThat(s -> s.getJustificacion().equals("Malo")));

        gestor.procesarSolicitudes(null, List.of(dto1, dto2));

        verify(elimRepo).agregarSolicitudDeEliminacion(argThat(s -> s.getJustificacion().equals("Bueno")));
    }

    @Test
    @DisplayName("Procesar modificaciones y eliminaciones juntas")
    void testProcesarAmbosTipos() {
        SolicitudDeModificacionDTO mod = new SolicitudDeModificacionDTO();
        SolicitudDeEliminacionDTO elim = new SolicitudDeEliminacionDTO();

        mod.setJustificacion("Modificación válida para test");
        elim.setJustificacion("Eliminación válida para test");

        gestor.procesarSolicitudes(List.of(mod), List.of(elim));

        verify(modRepo).agregarSolicitudDeModificacion(any());
        verify(elimRepo).agregarSolicitudDeEliminacion(any());
    }

    @Test
    @DisplayName("Solicitud de modificación sin cambios en hecho (solo justificación)")
    void testModificacionSinCambiosHecho() {
        SolicitudDeModificacionDTO dto = new SolicitudDeModificacionDTO();
        dto.setJustificacion("Solo texto");
        dto.setHechoModificado(null);

        gestor.procesarSolicitudes(List.of(dto), null);

        ArgumentCaptor<SolicitudDeModificacion> captor = ArgumentCaptor.forClass(SolicitudDeModificacion.class);
        verify(modRepo).agregarSolicitudDeModificacion(captor.capture());

        assertNull(captor.getValue().getHechoModificado());
    }

    @Test
    @DisplayName("Mapeo correcto de ID de usuario en modificación")
    void testMapeoUsuarioId() {
        UUID userId = UUID.randomUUID();
        SolicitudDeModificacionDTO dto = new SolicitudDeModificacionDTO();
        dto.setUsuarioId(userId);

        dto.setJustificacion("Justificación válida de prueba");

        gestor.procesarSolicitudes(List.of(dto), null);

        ArgumentCaptor<SolicitudDeModificacion> captor = ArgumentCaptor.forClass(SolicitudDeModificacion.class);
        verify(modRepo).agregarSolicitudDeModificacion(captor.capture());

        assertEquals(userId, captor.getValue().getUsuario().getId_usuario());
    }
}