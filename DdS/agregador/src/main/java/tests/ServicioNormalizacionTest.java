/*
package tests;


import agregador.service.ServicioNormalizacion;
import agregador.service.normalizacion.MockNormalizador;
import agregador.domain.HechosYColecciones.*;
import agregador.dto.Hechos.HechoDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicioNormalizacionTest {

    @Test
    void testNormalizaHechoDelegandoEnMockNormalizador() {
        MockNormalizador mockN = mock(MockNormalizador.class);
        ServicioNormalizacion servicio = new ServicioNormalizacion(mockN);

        HechoDTO dto = new HechoDTO();
        dto.setTitulo("Robo en Cabildo");

        Hecho entidad = new Hecho();
        entidad.setTitulo("Robo en Cabildo");

        when(mockN.normalizar(any())).thenReturn(entidad);

        Hecho resultado = servicio.normalizar(dto);

        assertNotNull(resultado);
        assertEquals("Robo en Cabildo", resultado.getTitulo());
        verify(mockN).normalizar(any());
    }
    @Test
    void testConvierteMapaDTOaEntidad() {
        MockNormalizador mockN = mock(MockNormalizador.class);
        when(mockN.normalizar(any())).thenAnswer(i -> i.getArguments()[0]);

        ServicioNormalizacion servicio = new ServicioNormalizacion(mockN);

        HechoDTO dto = new HechoDTO();
        dto.setTitulo("Accidente");
        dto.setDescripcion("Chocaron dos autos");
        dto.setCategoria("Tránsito");

        UbicacionDTO u = new UbicacionDTO();
        u.setLatitud(-34.1);
        u.setLongitud(-58.5);
        dto.setUbicacion(u);

        Hecho entidad = servicio.normalizar(dto);

        assertEquals("Accidente", entidad.getTitulo());
        assertEquals("Tránsito", entidad.getCategoria());
        assertNotNull(entidad.getUbicacion());
        assertEquals(-34.1, entidad.getUbicacion().getLatitud());
}
    @Test
    void testNormalizarNull() {
        ServicioNormalizacion servicio = new ServicioNormalizacion(mock(MockNormalizador.class));
        assertNull(servicio.normalizar(null));
}
}
}
*/
