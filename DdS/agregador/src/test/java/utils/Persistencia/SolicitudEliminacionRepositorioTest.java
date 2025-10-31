package utils.Persistencia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import utils.BDUtils;
import utils.Dominio.Solicitudes.SolicitudDeEliminacion;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SolicitudEliminacionRepositorioTest {

    private SolicitudEliminacionRepositorio solicitudRepositorio;
    private HechoRepositorio hechoRepositorio;
    private EntityManager em;
    private EntityTransaction transaction;

    @BeforeEach
    void setUp() {
        hechoRepositorio = mock(HechoRepositorio.class);
        solicitudRepositorio = new SolicitudEliminacionRepositorio(hechoRepositorio);
        em = mock(EntityManager.class);
        transaction = mock(EntityTransaction.class);
        when(em.getTransaction()).thenReturn(transaction);
    }

    @Test
    void testBuscarTodas_Success() {
        try (MockedStatic<BDUtils> utilities = mockStatic(BDUtils.class)) {
            utilities.when(BDUtils::getEntityManager).thenReturn(em);

            List<SolicitudDeEliminacion> solicitudesMock = new ArrayList<>();
            solicitudesMock.add(new SolicitudDeEliminacion());

            TypedQuery<SolicitudDeEliminacion> query = mock(TypedQuery.class);
            when(em.createQuery(anyString(), eq(SolicitudDeEliminacion.class))).thenReturn(query);
            when(query.getResultList()).thenReturn(solicitudesMock);

            List<SolicitudDeEliminacion> resultado = solicitudRepositorio.buscarTodas();

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
        }
    }

    @Test
    void testAgregarSolicitudDeEliminacion_Success() {
        try (MockedStatic<BDUtils> utilities = mockStatic(BDUtils.class)) {
            utilities.when(BDUtils::getEntityManager).thenReturn(em);

            SolicitudDeEliminacion solicitud = new SolicitudDeEliminacion();

            solicitudRepositorio.agregarSolicitudDeEliminacion(solicitud);

            verify(em).merge(solicitud);
        }
    }

    @Test
    void testActualizarEstadoSolicitudEliminacion_Aceptada() {
        try (MockedStatic<BDUtils> utilities = mockStatic(BDUtils.class)) {
            utilities.when(BDUtils::getEntityManager).thenReturn(em);

            UUID id = UUID.randomUUID();
            SolicitudDeEliminacion solicitud = new SolicitudDeEliminacion();

            when(em.find(SolicitudDeEliminacion.class, id)).thenReturn(solicitud);

            boolean resultado = solicitudRepositorio.actualizarEstadoSolicitudEliminacion("aceptada", id);

            assertTrue(resultado);
        }
    }

    @Test
    void testActualizarEstadoSolicitudEliminacion_NoEncontrada() {
        try (MockedStatic<BDUtils> utilities = mockStatic(BDUtils.class)) {
            utilities.when(BDUtils::getEntityManager).thenReturn(em);

            UUID id = UUID.randomUUID();
            when(em.find(SolicitudDeEliminacion.class, id)).thenReturn(null);

            boolean resultado = solicitudRepositorio.actualizarEstadoSolicitudEliminacion("aceptada", id);

            assertFalse(resultado);
            verify(em, never()).merge(any());
            verify(em).close();
        }
    }

    @Test
    void testBuscarPorId_Encontrado() {
        try (MockedStatic<BDUtils> utilities = mockStatic(BDUtils.class)) {
            utilities.when(BDUtils::getEntityManager).thenReturn(em);

            UUID id = UUID.randomUUID();
            SolicitudDeEliminacion solicitud = new SolicitudDeEliminacion();
            when(em.find(SolicitudDeEliminacion.class, id)).thenReturn(solicitud);

            Optional<SolicitudDeEliminacion> resultado = solicitudRepositorio.buscarPorId(id);

            assertTrue(resultado.isPresent());
            assertEquals(solicitud, resultado.get());
        }
    }

    @Test
    void testBuscarPorId_NoEncontrado() {
        try (MockedStatic<BDUtils> utilities = mockStatic(BDUtils.class)) {
            utilities.when(BDUtils::getEntityManager).thenReturn(em);

            UUID id = UUID.randomUUID();
            when(em.find(SolicitudDeEliminacion.class, id)).thenReturn(null);

            Optional<SolicitudDeEliminacion> resultado = solicitudRepositorio.buscarPorId(id);

            assertFalse(resultado.isPresent());
        }
    }
}