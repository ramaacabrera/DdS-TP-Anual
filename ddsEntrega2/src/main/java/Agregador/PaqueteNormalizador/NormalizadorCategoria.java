package Agregador.PaqueteNormalizador;

import java.util.HashMap;
import java.util.Map;

public class NormalizadorCategoria {
        private static final Map<String, String> MAPEO_CATEGORIAS = new HashMap<>();

        static {
            // Mapeo de categorías sinónimas a categorías normalizadas
            MAPEO_CATEGORIAS.put("fuego forestal", "Incendio Forestal");
            MAPEO_CATEGORIAS.put("forest fire", "Incendio Forestal");
            MAPEO_CATEGORIAS.put("incendio", "Incendio Forestal");
            MAPEO_CATEGORIAS.put("wildfire", "Incendio Forestal");
            MAPEO_CATEGORIAS.put("incendio forestal", "Incendio Forestal");

            MAPEO_CATEGORIAS.put("accidente vial", "Accidente de Tránsito");
            MAPEO_CATEGORIAS.put("car crash", "Accidente de Tránsito");
            MAPEO_CATEGORIAS.put("road accident", "Accidente de Tránsito");
            MAPEO_CATEGORIAS.put("accidente de tráfico", "Accidente de Tránsito");
            MAPEO_CATEGORIAS.put("accidente de transito", "Accidente de Tránsito");

            MAPEO_CATEGORIAS.put("desaparición", "Desaparición Forzada");
            MAPEO_CATEGORIAS.put("desaparicion", "Desaparición Forzada");
            MAPEO_CATEGORIAS.put("missing person", "Desaparición Forzada");
            MAPEO_CATEGORIAS.put("desaparecido", "Desaparición Forzada");

            MAPEO_CATEGORIAS.put("contaminación", "Contaminación Ambiental");
            MAPEO_CATEGORIAS.put("contaminacion", "Contaminación Ambiental");
            MAPEO_CATEGORIAS.put("pollution", "Contaminación Ambiental");

            MAPEO_CATEGORIAS.put("inundación", "Inundación");
            MAPEO_CATEGORIAS.put("inundacion", "Inundación");
            MAPEO_CATEGORIAS.put("flood", "Inundación");
        }

        public static String normalizarCategoria(String categoriaOriginal) {
            if (categoriaOriginal == null || categoriaOriginal.trim().isEmpty()) {
                return "Sin Categoría";
            }

            String categoriaLower = categoriaOriginal.toLowerCase().trim();
            return MAPEO_CATEGORIAS.getOrDefault(categoriaLower, categoriaOriginal);
        }
    }
