/*package tests;

import gestorAdministrativo.repository.HechoRepositorio;
import gestorAdministrativo.dto.Solicitudes.*;
import gestorAdministrativo.repository.*;
import gestorAdministrativo.service.*;
import gestorAdministrativo.domain.*;
import gestorAdministrativo.dto.*;
import gestorAdministrativo.utils.*;
import gestorAdministrativo.controller.*;

import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static jdk.internal.org.objectweb.asm.util.CheckClassAdapter.verify;
import static net.bytebuddy.matcher.ElementMatchers.any;
import static org.junit.Assert.assertEquals;

@ExtendWith(MockitoExtension.class)
class SolicitudEliminacionServiceTest {

    @Mock private SolicitudEliminacionRepositorio solicitudRepo;
    @Mock private HechoRepositorio hechoRepo;
    @Mock private UsuarioRepositorio usuarioRepo;

    private SolicitudEliminacionService service;

    @BeforeEach
    void setup() {
        service = new SolicitudEliminacionService(solicitudRepo, hechoRepo, usuarioRepo);
}
    @Test
    void crearSolicitudEliminacion_creaCorrectamente() {
        SolicitudDTO dto = new SolicitudDTO();
        dto.setHechoId(1L);
        dto.setUsuarioId(20L);
        dto.setJustificacion("La info es incorrecta");

        when(hechoRepo.buscarPorId(1L)).thenReturn(new Hecho());
        when(usuarioRepo.buscarPorId(20L)).thenReturn(new Usuario());

        service.crearSolicitudEliminacion(dto);

        verify(solicitudRepo).guardar(any(SolicitudDeEliminacion.class));
    }

    @Test
    void procesarSolicitud_aceptada_deberiaEliminarHecho() {
        UUID id = UUID.randomUUID();

        Hecho h = new Hecho();
        SolicitudDeEliminacion sol = new SolicitudDeEliminacion();
        sol.setHechoAsociado(h);

        when(solicitudRepo.actualizarEstadoSolicitudEliminacion("ACEPTADA", id)).thenReturn(true);
        when(solicitudRepo.buscarPorId(id)).thenReturn(Optional.of(sol));

        boolean ok = service.procesarSolicitud(id, "ACEPTADA");

        assertTrue(ok);
        verify(hechoRepo).eliminar(h);
    }
    @Test
    void procesarSolicitud_rechazada_noElimina() {
        UUID id = UUID.randomUUID();

        when(solicitudRepo.actualizarEstadoSolicitudEliminacion("RECHAZADA", id)).thenReturn(true);

        boolean ok = service.procesarSolicitud(id, "RECHAZADA");

        assertTrue(ok);
        verify(hechoRepo, never()).eliminar(any());
    }
    @Test
    void obtenerTodasLasSolicitudes_ok() {
        SolicitudDeEliminacion s = new SolicitudDeEliminacion();
        s.setJustificacion("x");
        when(solicitudRepo.buscarTodas()).thenReturn(List.of(s));

        List<SolicitudDeEliminacionDTO> dtos = service.obtenerTodasLasSolicitudes();

        assertEquals(1, dtos.size());
        assertEquals("x", dtos.get(0).getJustificacion());
    }
} */