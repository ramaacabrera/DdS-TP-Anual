package agregador.service;

import agregador.service.normalizacion.NormalizadorCategorias;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NormalizadorCategoriasTest {

    @Test
    @DisplayName("Debe convertir a minúsculas y trim")
    void testMinusculas() {
        assertEquals("incendio", NormalizadorCategorias.normalizar("  INCENDIO  "));
    }

    @Test
    @DisplayName("Debe eliminar acentos y diacríticos")
    void testAcentos() {
        assertEquals("inundacion_cordoba", NormalizadorCategorias.normalizar("Inundación Córdoba"));
    }

    @Test
    @DisplayName("Debe reemplazar espacios y guiones por underscore")
    void testEspaciosYGuiones() {
        assertEquals("fuego_forestal_grave", NormalizadorCategorias.normalizar("Fuego-Forestal Grave"));
    }

    @Test
    @DisplayName("Debe eliminar caracteres especiales no alfanuméricos")
    void testCaracteresEspeciales() {
        assertEquals("robo_armado", NormalizadorCategorias.normalizar("¡Robo Armado!"));
    }

    @Test
    @DisplayName("Debe manejar nulos y vacíos retornando default")
    void testNulos() {
        assertEquals("sin_categoria", NormalizadorCategorias.normalizar(null));
        assertEquals("sin_categoria", NormalizadorCategorias.normalizar("   "));
    }

    @Test
    @DisplayName("Debe eliminar underscores redundantes")
    void testUnderscoresMultiples() {
        assertEquals("a_b_c", NormalizadorCategorias.normalizar("a__b___c"));
        assertEquals("hola", NormalizadorCategorias.normalizar("_hola_"));
    }

    @Test
    @DisplayName("Caso complejo: Mezcla de todo")
    void testComplejo() {
        String input = "¡¡ATENCIÓN!! Caída de Árboles - Zona Norte";
        String expected = "atencion_caida_de_arboles_zona_norte";
        assertEquals(expected, NormalizadorCategorias.normalizar(input));
    }
}