package utils.Persistencia;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import utils.BDUtils;
import utils.Dominio.HechosYColecciones.Hecho;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class HechoRepositorioTest {

    @Test
    void testGetHechos() {
        try (MockedStatic<BDUtils> utilities = mockStatic(BDUtils.class)) {
            // Configurar mocks
            EntityManager em = mock(EntityManager.class);
            utilities.when(BDUtils::getEntityManager).thenReturn(em);

            HechoRepositorio hechoRepositorio = new HechoRepositorio();
            List<Hecho> hechosMock = new ArrayList<>();

            Hecho hecho1 = new Hecho();
            hecho1.setTitulo("Hecho 1");
            hechosMock.add(hecho1);

            Hecho hecho2 = new Hecho();
            hecho2.setTitulo("Hecho 2");
            hechosMock.add(hecho2);

            TypedQuery<Hecho> query = mock(TypedQuery.class);

            // Query EXACTA como en tu implementación
            when(em.createQuery("SELECT h FROM Hecho h", Hecho.class)).thenReturn(query);
            when(query.getResultList()).thenReturn(hechosMock);

            // Ejecutar
            List<Hecho> resultado = hechoRepositorio.getHechos();

            // Verificar
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            assertEquals("Hecho 1", resultado.get(0).getTitulo());

            // Verificaciones
            verify(em).createQuery("SELECT h FROM Hecho h", Hecho.class);
            verify(query).getResultList();
            verify(em).close();
        }
    }

    @Test
    void testBuscarPorTitulo_Encontrado() {
        try (MockedStatic<BDUtils> utilities = mockStatic(BDUtils.class)) {
            // Configurar mocks
            EntityManager em = mock(EntityManager.class);
            utilities.when(BDUtils::getEntityManager).thenReturn(em);

            HechoRepositorio hechoRepositorio = new HechoRepositorio();
            String titulo = "Test Hecho";
            Hecho hechoMock = new Hecho();
            hechoMock.setTitulo(titulo);

            TypedQuery<Hecho> query = mock(TypedQuery.class);

            // Query EXACTA como en tu implementación (con :paramTitulo)
            when(em.createQuery("SELECT h FROM Hecho h WHERE h.titulo = :paramTitulo", Hecho.class))
                    .thenReturn(query);
            when(query.setParameter("paramTitulo", titulo)).thenReturn(query);
            when(query.getSingleResult()).thenReturn(hechoMock);

            // Ejecutar
            Hecho resultado = hechoRepositorio.buscarPorTitulo(titulo);

            // Verificar
            assertNotNull(resultado);
            assertEquals(titulo, resultado.getTitulo());

            // Verificaciones
            verify(em).createQuery("SELECT h FROM Hecho h WHERE h.titulo = :paramTitulo", Hecho.class);
            verify(query).setParameter("paramTitulo", titulo);
            verify(query).getSingleResult();
            verify(em).close();
        }
    }

    @Test
    void testBuscarPorTitulo_NoEncontrado() {
        try (MockedStatic<BDUtils> utilities = mockStatic(BDUtils.class)) {
            // Configurar mocks
            EntityManager em = mock(EntityManager.class);
            utilities.when(BDUtils::getEntityManager).thenReturn(em);

            HechoRepositorio hechoRepositorio = new HechoRepositorio();
            String titulo = "Titulo Inexistente";

            TypedQuery<Hecho> query = mock(TypedQuery.class);

            when(em.createQuery("SELECT h FROM Hecho h WHERE h.titulo = :paramTitulo", Hecho.class))
                    .thenReturn(query);
            when(query.setParameter("paramTitulo", titulo)).thenReturn(query);
            when(query.getSingleResult()).thenThrow(NoResultException.class);

            // Ejecutar
            Hecho resultado = hechoRepositorio.buscarPorTitulo(titulo);

            // Verificar
            assertNull(resultado);
            verify(em).close();
        }
    }

    @Test
    void testBuscarPorId_String_Encontrado() {
        try (MockedStatic<BDUtils> utilities = mockStatic(BDUtils.class)) {
            // Configurar mocks
            EntityManager em = mock(EntityManager.class);
            utilities.when(BDUtils::getEntityManager).thenReturn(em);

            HechoRepositorio hechoRepositorio = new HechoRepositorio();
            UUID id = UUID.randomUUID();
            String idString = id.toString();
            Hecho hechoMock = new Hecho();
            hechoMock.setTitulo("Hecho por ID");

            when(em.find(Hecho.class, id)).thenReturn(hechoMock);

            // Ejecutar
            Hecho resultado = hechoRepositorio.buscarPorId(idString);

            // Verificar
            assertNotNull(resultado);
            assertEquals("Hecho por ID", resultado.getTitulo());
            verify(em).find(Hecho.class, id);
            verify(em).close();
        }
    }

    @Test
    void testBuscarPorId_String_NoEncontrado() {
        try (MockedStatic<BDUtils> utilities = mockStatic(BDUtils.class)) {
            // Configurar mocks
            EntityManager em = mock(EntityManager.class);
            utilities.when(BDUtils::getEntityManager).thenReturn(em);

            HechoRepositorio hechoRepositorio = new HechoRepositorio();
            UUID id = UUID.randomUUID();
            String idString = id.toString();

            when(em.find(Hecho.class, id)).thenReturn(null);

            // Ejecutar
            Hecho resultado = hechoRepositorio.buscarPorId(idString);

            // Verificar
            assertNull(resultado);
            verify(em).find(Hecho.class, id);
            verify(em).close();
        }
    }

    @Test
    void testBuscarPorId_String_Invalido() {
        try (MockedStatic<BDUtils> utilities = mockStatic(BDUtils.class)) {
            // Configurar mocks
            EntityManager em = mock(EntityManager.class);
            utilities.when(BDUtils::getEntityManager).thenReturn(em);

            HechoRepositorio hechoRepositorio = new HechoRepositorio();
            String idInvalido = "id-invalido";

            // Ejecutar
            Hecho resultado = hechoRepositorio.buscarPorId(idInvalido);

            // Verificar
            assertNull(resultado);
            // No debería llamar a find con ID inválido
            verify(em, never()).find(any(), any());
            verify(em).close();
        }
    }

    @Test
    void testBuscarPorId_UUID_Encontrado() {
        try (MockedStatic<BDUtils> utilities = mockStatic(BDUtils.class)) {
            // Configurar mocks
            EntityManager em = mock(EntityManager.class);
            utilities.when(BDUtils::getEntityManager).thenReturn(em);

            HechoRepositorio hechoRepositorio = new HechoRepositorio();
            UUID id = UUID.randomUUID();
            Hecho hechoMock = new Hecho();
            hechoMock.setTitulo("Hecho por UUID");

            when(em.find(Hecho.class, id)).thenReturn(hechoMock);

            // Ejecutar
            Hecho resultado = hechoRepositorio.buscarPorId(id);

            // Verificar
            assertNotNull(resultado);
            assertEquals("Hecho por UUID", resultado.getTitulo());
            verify(em).find(Hecho.class, id);
            verify(em).close();
        }
    }

    @Test
    void testBuscarCategorias() {
        try (MockedStatic<BDUtils> utilities = mockStatic(BDUtils.class)) {
            // Configurar mocks
            EntityManager em = mock(EntityManager.class);
            utilities.when(BDUtils::getEntityManager).thenReturn(em);

            HechoRepositorio hechoRepositorio = new HechoRepositorio();
            List<String> categoriasMock = List.of("Categoria1", "Categoria2", "Categoria3");

            TypedQuery<String> query = mock(TypedQuery.class);

            when(em.createQuery("SELECT DISTINCT h.categoria FROM Hecho h WHERE h.categoria IS NOT NULL ORDER BY h.categoria", String.class))
                    .thenReturn(query);
            when(query.getResultList()).thenReturn(categoriasMock);

            // Ejecutar
            List<String> resultado = hechoRepositorio.buscarCategorias();

            // Verificar
            assertNotNull(resultado);
            assertEquals(3, resultado.size());
            assertEquals("Categoria1", resultado.get(0));

            verify(em).createQuery("SELECT DISTINCT h.categoria FROM Hecho h WHERE h.categoria IS NOT NULL ORDER BY h.categoria", String.class);
            verify(query).getResultList();
            verify(em).close();
        }
    }

    @Test
    void testBuscarCategorias_Excepcion() {
        try (MockedStatic<BDUtils> utilities = mockStatic(BDUtils.class)) {
            // Configurar mocks
            EntityManager em = mock(EntityManager.class);
            utilities.when(BDUtils::getEntityManager).thenReturn(em);

            HechoRepositorio hechoRepositorio = new HechoRepositorio();

            TypedQuery<String> query = mock(TypedQuery.class);

            when(em.createQuery(anyString(), eq(String.class))).thenReturn(query);
            when(query.getResultList()).thenThrow(new RuntimeException("Error de base de datos"));

            // Ejecutar
            List<String> resultado = hechoRepositorio.buscarCategorias();

            // Verificar que retorna lista vacía en caso de error
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verify(em).close();
        }
    }
}