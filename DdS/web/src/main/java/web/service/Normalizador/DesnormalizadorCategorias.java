package web.service.Normalizador;

import java.util.Locale;

public class DesnormalizadorCategorias {

    public static String desnormalizar(String textoNormalizado) {
        if (textoNormalizado == null || textoNormalizado.trim().isEmpty()) {
            return "Sin Categoría";
        }

        String desnormalizado = textoNormalizado.replaceAll("_", " ");

        desnormalizado = capitalizarFrase(desnormalizado);

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

    private static String capitalizarFrase(String frase) {
        if (frase == null || frase.isEmpty()) {
            return frase;
        }

        String[] palabras = frase.split("\\s+");
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < palabras.length; i++) {
            if (!palabras[i].isEmpty()) {
                String palabra = palabras[i];
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

    private static String aplicarCorreccionesEspecificas(String texto) {
        if (texto == null) return texto;

        String corregido = texto;

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