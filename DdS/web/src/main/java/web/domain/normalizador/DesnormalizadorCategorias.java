package web.domain.normalizador;

import java.util.Locale;

public class DesnormalizadorCategorias {

    /**
     * Convierte un texto normalizado en una versión "linda" para mostrar
     * - Reemplaza underscores por espacios
     * - Capitaliza cada palabra
     * - Aplica reglas especiales para palabras comunes
     */
    public static String desnormalizar(String textoNormalizado) {
        if (textoNormalizado == null || textoNormalizado.trim().isEmpty()) {
            return "Sin Categoría";
        }

        // Paso 1: Reemplazar underscores por espacios
        String desnormalizado = textoNormalizado.replaceAll("_", " ");

        // Paso 2: Capitalizar cada palabra
        desnormalizado = capitalizarFrase(desnormalizado);

        // Paso 3: Aplicar correcciones específicas para palabras comunes
        desnormalizado = aplicarCorreccionesEspecificas(desnormalizado);

        return desnormalizado;
    }

    private static boolean contains(String[] array, String elemento) {
        for (String item : array) {
            if (item.equals(elemento)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Capitaliza cada palabra de una frase
     */
    private static String capitalizarFrase(String frase) {
        if (frase == null || frase.isEmpty()) {
            return frase;
        }

        String[] palabras = frase.split("\\s+");
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < palabras.length; i++) {
            if (!palabras[i].isEmpty()) {
                String palabra = palabras[i];
                // Capitalizar primera letra, resto en minúsculas
                String palabraCapitalizada = palabra.substring(0, 1).toUpperCase(Locale.ROOT) +
                        palabra.substring(1).toLowerCase(Locale.ROOT);
                resultado.append(palabraCapitalizada);

                if (i < palabras.length - 1) {
                    resultado.append(" ");
                }
            }
        }

        return resultado.toString();
    }

    /**
     * Aplica correcciones específicas para palabras comunes
     */
    private static String aplicarCorreccionesEspecificas(String texto) {
        if (texto == null) return texto;

        // Correcciones para palabras específicas
        String corregido = texto;

        // Preposiciones y artículos en minúsculas (excepto al inicio)
        String[] palabrasMinusculas = {"de", "del", "la", "las", "el", "los", "y", "e", "o", "u", "a", "en", "con", "por", "para", "sin", "sobre"};

        String[] palabras = corregido.split("\\s+");
        if (palabras.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < palabras.length; i++) {
                String palabra = palabras[i];
                String palabraLower = palabra.toLowerCase(Locale.ROOT);

                if (i > 0 && contains(palabrasMinusculas, palabraLower)) {
                    sb.append(palabraLower);
                } else {
                    sb.append(palabra);
                }

                if (i < palabras.length - 1) {
                    sb.append(" ");
                }
            }
            corregido = sb.toString();
        }

        // Correcciones específicas de categorías comunes
        corregido = corregido.replace("Gps", "GPS");
        corregido = corregido.replace("Gas Toxico", "Gas Tóxico");
        corregido = corregido.replace("Toxico", "Tóxico");
        corregido = corregido.replace("Transito", "Tránsito");
        corregido = corregido.replace("Policia", "Policía");
        corregido = corregido.replace("Genero", "Género");
        corregido = corregido.replace("Emanacion", "Emanación");

        return corregido;
    }
}