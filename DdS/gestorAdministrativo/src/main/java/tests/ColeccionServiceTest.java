/*
package tests;

import gestorAdministrativo.domain.HechosYColecciones.Coleccion;
import gestorAdministrativo.dto.Coleccion.ColeccionDTO;
import gestorAdministrativo.repository.HechoRepositorio;
import org.junit.Test;
import utils.Persistencia.ColeccionRepositorio;
import gestorAdministrativo.repository.*;
import gestorAdministrativo.service.*;
import gestorAdministrativo.domain.*;
import gestorAdministrativo.dto.*;
import gestorAdministrativo.utils.*;
import gestorAdministrativo.controller.*;


import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static javax.management.Query.times;
import static jdk.internal.org.objectweb.asm.util.CheckClassAdapter.verify;
import static net.bytebuddy.matcher.ElementMatchers.any;
import static org.junit.Assert.assertEquals;

@ExtendWith(MockitoExtension.class)
class ColeccionServiceTest {

    @Mock
    private ColeccionRepositorio coleccionRepositorio;

    @Mock
    private HechoRepositorio hechoRepositorio;

    private ColeccionService service;

    @BeforeEach
    void setup() {
        service = new ColeccionService(coleccionRepositorio, hechoRepositorio);
}
    @Test
    void crearColeccion_deberiaGuardarYRetornarDTO() {
        // Arrange
        ColeccionDTO dto = new ColeccionDTO();
        dto.setTitulo("Mi coleccion");
        dto.setDescripcion("Descripcion");

        // Mock: no necesita criterios ni fuentes
        when(hechoRepositorio.buscarHechos(any())).thenReturn(List.of());

        // Act
        ColeccionDTO result = service.crearColeccion(dto);

        // Assert
        verify(coleccionRepositorio, times(1)).guardar(any(Coleccion.class));
        assertEquals("Mi coleccion", result.getTitulo());
        assertEquals("Descripcion", result.getDescripcion());
    }
    @Test
    public void actualizarColeccion_deberiaActualizarTituloDescripcionYCriterios() {
        UUID id = UUID.randomUUID();

        Coleccion existente = new Coleccion();
        existente.setTitulo("Viejo");
        existente.setDescripcion("Vieja desc");

        when(coleccionRepositorio.buscarPorHandle(id.toString()))
                .thenReturn(existente);

        ColeccionDTO cambios = new ColeccionDTO();
        cambios.setTitulo("Nuevo");
        cambios.setDescripcion("Nueva desc");

        when(hechoRepositorio.buscarHechos(any())).thenReturn(List.of());

        // Act
        ColeccionDTO result = service.actualizarColeccion(id, cambios);

        // Assert
        verify(coleccionRepositorio).guardar(existente);
        assertEquals("Nuevo", result.getTitulo());
        assertEquals("Nueva desc", result.getDescripcion());
    }
    @Test
    void eliminarColeccion_deberiaEliminarSiExiste() {
        UUID id = UUID.randomUUID();

        Coleccion coleccion = new Coleccion();
        when(coleccionRepositorio.buscarPorHandle(id.toString()))
                .thenReturn(coleccion);

        // Act
        service.eliminarColeccion(id);

        // Assert
        verify(coleccionRepositorio).eliminar(coleccion);
    }
    @Test
    void obtenerColeccionPorId_devuelveDTO() {
        UUID id = UUID.randomUUID();

        Coleccion coleccion = new Coleccion();
        coleccion.setTitulo("T");

        when(coleccionRepositorio.buscarPorHandle(id.toString()))
                .thenReturn(coleccion);

        ColeccionDTO dto = service.obtenerColeccionPorId(id);

        assertEquals("T", dto.getTitulo());
    }
}
*/
