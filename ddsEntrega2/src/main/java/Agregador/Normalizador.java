package Agregador;
import Agregador.HechosYColecciones.Hecho;

import java.util.*;
public class Normalizador {

    private final Map<String, String> mapaCategorias;

    public Normalizador() {
        mapaCategorias = new HashMap<>();
        mapaCategorias.put("Incendio Forestal", "Incendio forestal");
        mapaCategorias.put("Fuego forestal", "Incendio forestal");
        mapaCategorias.put("Incendio de monte", "Incendio forestal");
        // etc (esto seria manualmente, ver como podria hacerse de manera mas generica)
    }

    public Hecho normalizar(Hecho hecho) {
        // Normalizar categoría
        String categoriaNormalizada = mapaCategorias.getOrDefault(
                hecho.getCategoria(), hecho.getCategoria()
        );
        hecho.setCategoria(categoriaNormalizada);

        // Normalizar fecha
        hecho.setFechaDeAcontecimiento(
                convertirFecha(hecho.getFechaDeAcontecimiento())
        );
        // Normalizar ubicacion
        hecho.setUbicacion(hecho.getUbicacion().toLowerCase().trim());

        return hecho;
    }

    private LocalDate convertirFecha(String fechaRaw) {
        List<DateTimeFormatter> formatos = List.of(
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.forLanguageTag("es")),
                DateTimeFormatter.ofPattern("dd-MM-yyyy")
        );

        for (DateTimeFormatter formato : formatos) {
            try {
                return LocalDate.parse(fechaRaw, formato);
            } catch (Exception e) {
                // que siga probando con igual formato
            }
        }

        throw new IllegalArgumentException("Formato de fecha no soportado: " + fechaRaw);
    }

}
