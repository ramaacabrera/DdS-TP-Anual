package agregador.service;

import agregador.domain.HechosYColecciones.Hecho;
import agregador.dto.Hechos.FuenteDTO;
import agregador.dto.Hechos.HechoDTO;
import agregador.dto.Hechos.UbicacionDTO;
import agregador.service.normalizacion.MockNormalizador;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicioNormalizacionTest {

    @Mock
    private MockNormalizador mockNormalizador;

    @InjectMocks
    private ServicioNormalizacion servicio;

    @Test
    @DisplayName("Debe convertir DTO completo a Entidad")
    void testConversionCompleta() {
        // Arrange
        HechoDTO dto = new HechoDTO();
        dto.setTitulo("Título Original");
        dto.setDescripcion("Desc");

        UbicacionDTO ubi = new UbicacionDTO();
        ubi.setLatitud(10.0);
        ubi.setLongitud(20.0);
        dto.setUbicacion(ubi);

        FuenteDTO fuente = new FuenteDTO();
        fuente.setDescriptor("Fuente1");
        dto.setFuente(fuente);

        when(mockNormalizador.normalizar(any(Hecho.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Hecho resultado = servicio.normalizar(dto);

        // Assert
        assertEquals("Título Original", resultado.getTitulo());
        assertNotNull(resultado.getUbicacion());
        assertEquals(10.0, resultado.getUbicacion().getLatitud());
        assertEquals("Fuente1", resultado.getFuente().getDescriptor());

        verify(mockNormalizador).normalizar(any(Hecho.class));
    }

    @Test
    @DisplayName("Debe manejar DTO con campos nulos sin explotar")
    void testConversionParcial() {
        HechoDTO dto = new HechoDTO();
        dto.setTitulo("Solo Titulo");

        when(mockNormalizador.normalizar(any(Hecho.class))).thenAnswer(i -> i.getArguments()[0]);

        Hecho resultado = servicio.normalizar(dto);

        assertEquals("Solo Titulo", resultado.getTitulo());
        assertNull(resultado.getUbicacion());
        assertNull(resultado.getFuente());
        assertTrue(resultado.getEtiquetas().isEmpty());
    }

    @Test
    @DisplayName("Si el DTO es null retorna null")
    void testDtoNulo() {
        assertNull(servicio.normalizar(null));
    }
}