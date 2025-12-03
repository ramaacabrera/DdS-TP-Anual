package agregador.service;

import agregador.domain.HechosYColecciones.Coleccion;
import agregador.domain.HechosYColecciones.Hecho;
import agregador.domain.fuente.Fuente;
import agregador.domain.fuente.TipoDeFuente;
import agregador.dto.Hechos.FuenteDTO;
import agregador.dto.Hechos.HechoDTO;
import agregador.dto.Hechos.UbicacionDTO;
import agregador.repository.ColeccionRepositorio;
import agregador.repository.FuenteRepositorio;
import agregador.repository.HechoRepositorio;
import agregador.utils.BDUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgregadorOrquestadorTest {

    @Mock HechoRepositorio hechoRepositorio;
    @Mock ColeccionRepositorio coleccionRepositorio;
    @Mock FuenteRepositorio fuenteRepositorio;
    @Mock ServicioNormalizacion servicioNormalizacion;
    @Mock MotorConsenso motorConsenso;
    @Mock GestorSolicitudes gestorSolicitudes;
    @Mock HechosCargadorService hechosCargadorService;
    @Mock ServicioGeoref servicioGeorefMock;

    @InjectMocks
    private AgregadorOrquestador orquestador;

    private MockedStatic<BDUtils> bdUtilsMock;

    @BeforeEach
    void setUp() throws Exception {
        Field georefField = AgregadorOrquestador.class.getDeclaredField("servicioGeoref");
        georefField.setAccessible(true);
        georefField.set(orquestador, servicioGeorefMock);

        bdUtilsMock = mockStatic(BDUtils.class);

        // Configuramos para que devuelva un EntityManager falso
        EntityManager emMock = mock(EntityManager.class);
        bdUtilsMock.when(BDUtils::getEntityManager).thenReturn(emMock);
    }

    @AfterEach
    void tearDown() {
        if (bdUtilsMock != null) {
            bdUtilsMock.close();
        }
    }

    @Test
    @DisplayName("procesarNuevosHechos: Flujo completo SIN TOCAR DB REAL")
    void testProcesarNuevosHechos() {
        // Arrange
        HechoDTO dto = crearHechoDTOValido();
        List<HechoDTO> listaDtos = List.of(dto);

        // Mocks de comportamiento
        when(fuenteRepositorio.buscarPorDescriptor("fuente-demo")).thenReturn(null);

        Fuente fuenteGuardada = new Fuente();
        fuenteGuardada.setId(UUID.randomUUID());
        when(fuenteRepositorio.guardar(any(Fuente.class))).thenReturn(fuenteGuardada);

        Hecho hechoNormalizado = new Hecho();
        hechoNormalizado.setTitulo("Hecho Normalizado");
        when(servicioNormalizacion.normalizar(dto)).thenReturn(hechoNormalizado);

        when(coleccionRepositorio.contarTodas()).thenReturn(1L);
        Coleccion coleccionMock = mock(Coleccion.class);
        when(coleccionRepositorio.obtenerPaginadas(1, 100)).thenReturn(List.of(coleccionMock));
        when(coleccionMock.cumpleCriterio(hechoNormalizado)).thenReturn(true);

        // Act
        orquestador.procesarNuevosHechos(listaDtos);

        // Assert
        verify(fuenteRepositorio).buscarPorDescriptor("fuente-demo");
        verify(fuenteRepositorio).guardar(any(Fuente.class));
        verify(servicioNormalizacion).normalizar(dto);
        verify(hechoRepositorio).guardar(hechoNormalizado);
        verify(coleccionRepositorio).actualizarTodas(anyList());
    }

    @Test
    @DisplayName("procesarNuevosHechos: Manejo de fuente existente")
    void testProcesarFuenteExistente() {
        // Arrange
        HechoDTO dto = crearHechoDTOValido();
        Fuente fuenteExistente = new Fuente();
        fuenteExistente.setId(UUID.randomUUID());

        when(fuenteRepositorio.buscarPorDescriptor("fuente-demo")).thenReturn(fuenteExistente);
        when(servicioNormalizacion.normalizar(dto)).thenReturn(new Hecho());
        when(coleccionRepositorio.contarTodas()).thenReturn(0L);

        // Act
        orquestador.procesarNuevosHechos(List.of(dto));

        // Assert
        verify(fuenteRepositorio, never()).guardar(any());
    }

    @Test
    @DisplayName("iniciarBusquedaAgregador: Debe delegar al servicio cargador")
    void testIniciarBusqueda() {
        orquestador.iniciarBusquedaAgregador();
        verify(hechosCargadorService).obtenerHechosNuevos();
        verify(hechosCargadorService).obtenerSolicitudes();
    }

    @Test
    @DisplayName("ejecutarAlgoritmoDeConsenso: Debe delegar al motor")
    void testEjecutarConsenso() {
        orquestador.ejecutarAlgoritmoDeConsenso();
        verify(motorConsenso).ejecutar();
    }

    private HechoDTO crearHechoDTOValido() {
        HechoDTO dto = new HechoDTO();
        dto.setTitulo("Test");

        FuenteDTO fuenteDto = new FuenteDTO();
        fuenteDto.setDescriptor("fuente-demo");
        fuenteDto.setTipoDeFuente(TipoDeFuente.ESTATICA);
        dto.setFuente(fuenteDto);

        UbicacionDTO ubi = new UbicacionDTO();
        ubi.setLatitud(10.0);
        ubi.setLongitud(20.0);
        ubi.setDescripcion("Ubicación ya lista");
        dto.setUbicacion(ubi);

        return dto;
    }

    @Test
    @DisplayName("enriquecerUbicacion: Si ya tiene descripción, no llama a GeoRef")
    void testEnriquecerUbicacion_Existente() {
        HechoDTO dto = crearHechoDTOValido();
        dto.getUbicacion().setDescripcion("Descripción Manual");

        when(servicioNormalizacion.normalizar(dto)).thenReturn(new Hecho());
        when(coleccionRepositorio.contarTodas()).thenReturn(0L);

        orquestador.procesarNuevosHechos(List.of(dto));

        verifyNoInteractions(servicioGeorefMock);
    }

    @Test
    @DisplayName("enriquecerUbicacion: Si no tiene coordenadas, no llama a GeoRef")
    void testEnriquecerUbicacion_SinCoordenadas() {
        HechoDTO dto = crearHechoDTOValido();
        dto.getUbicacion().setLatitud(0.0);
        dto.getUbicacion().setLongitud(0.0);

        when(servicioNormalizacion.normalizar(dto)).thenReturn(new Hecho());
        when(coleccionRepositorio.contarTodas()).thenReturn(0L);

        orquestador.procesarNuevosHechos(List.of(dto));

        verifyNoInteractions(servicioGeorefMock);
    }

    @Test
    @DisplayName("gestionarFuente: Tipo desconocido se maneja sin error (Default ESTATICA)")
    void testGestionarFuente_TipoDesconocido() {
        HechoDTO dto = crearHechoDTOValido();
        dto.getFuente().setTipoDeFuente(null);

        when(fuenteRepositorio.buscarPorDescriptor(anyString())).thenReturn(null);
        when(fuenteRepositorio.guardar(any(Fuente.class))).thenReturn(new Fuente());
        when(servicioNormalizacion.normalizar(dto)).thenReturn(new Hecho());
        when(coleccionRepositorio.contarTodas()).thenReturn(0L);

        orquestador.procesarNuevosHechos(List.of(dto));

        verify(fuenteRepositorio).guardar(any(Fuente.class));
    }

    @Test
    @DisplayName("actualizarColecciones: Itera sobre múltiples páginas")
    void testActualizarColecciones_PaginacionMultiple() {
        HechoDTO dto = crearHechoDTOValido();
        Hecho hechoNorm = new Hecho();
        when(servicioNormalizacion.normalizar(dto)).thenReturn(hechoNorm);

        when(coleccionRepositorio.contarTodas()).thenReturn(150L);

        Coleccion c1 = mock(Coleccion.class);
        when(coleccionRepositorio.obtenerPaginadas(1, 100)).thenReturn(List.of(c1));
        when(coleccionRepositorio.obtenerPaginadas(2, 100)).thenReturn(Collections.emptyList());

        orquestador.procesarNuevosHechos(List.of(dto));

        verify(coleccionRepositorio).obtenerPaginadas(1, 100);
        verify(coleccionRepositorio).obtenerPaginadas(2, 100);
    }

    @Test
    @DisplayName("procesarSolicitudes: Delega correctamente al gestor")
    void testProcesarSolicitudes() {
        orquestador.procesarSolicitudes(Collections.emptyList(), Collections.emptyList());
        verify(gestorSolicitudes).procesarSolicitudes(anyList(), anyList());
    }

    @Test
    @DisplayName("procesarNuevosHechos: Lista vacía retorna rápido")
    void testProcesarListaVacia() {
        orquestador.procesarNuevosHechos(Collections.emptyList());
        verifyNoInteractions(servicioNormalizacion);
    }
}