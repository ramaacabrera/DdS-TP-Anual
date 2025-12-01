package web.domain.Normalizador;

import java.text.Normalizer;
import java.util.Locale;

public class NormalizadorCategorias {

    /**
     * Normaliza un texto para usarlo como identificador técnico
     * - Convierte a minúsculas
     * - Elimina acentos y caracteres especiales
     * - Reemplaza espacios por underscore
     * - Elimina caracteres no alfanuméricos
     */
    public static String normalizar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "sin_categoria";
        }

        // Paso 1: Convertir a minúsculas
        String normalizado = texto.toLowerCase(Locale.ROOT);

        // Paso 2: Eliminar acentos y diacríticos
        normalizado = Normalizer.normalize(normalizado, Normalizer.Form.NFD);
        normalizado = normalizado.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        // Paso 3: Reemplazar espacios y caracteres especiales por underscore
        normalizado = normalizado.replaceAll("[\\s\\-]+", "_");

        // Paso 4: Eliminar caracteres no alfanuméricos (excepto underscore)
        normalizado = normalizado.replaceAll("[^a-z0-9_]", "");

        // Paso 5: Eliminar underscores múltiples y de los extremos
        normalizado = normalizado.replaceAll("_+", "_");
        normalizado = normalizado.replaceAll("^_|_$", "");

        // Si queda vacío, retornar valor por defecto
        if (normalizado.isEmpty()) {
            return "sin_categoria";
        }

        return normalizado;
    }
}