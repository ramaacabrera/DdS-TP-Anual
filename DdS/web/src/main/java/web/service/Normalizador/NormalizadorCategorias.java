package web.service.Normalizador;

import java.text.Normalizer;
import java.util.Locale;

public class NormalizadorCategorias {

    public static String normalizar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "sin_categoria";
        }

        String normalizado = texto.toLowerCase(Locale.ROOT);

        normalizado = Normalizer.normalize(normalizado, Normalizer.Form.NFD);
        normalizado = normalizado.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        normalizado = normalizado.replaceAll("[\\s\\-]+", "_");

        normalizado = normalizado.replaceAll("[^a-z0-9_]", "");

        normalizado = normalizado.replaceAll("_+", "_");
        normalizado = normalizado.replaceAll("^_|_$", "");

        if (normalizado.isEmpty()) {
            return "sin_categoria";
        }

        return normalizado;
    }
}