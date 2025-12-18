package gestorPublico.service.Normalizador;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DiccionarioCategorias {

    private final Map<String, String> mapa = new HashMap<>();

    public DiccionarioCategorias() {
        cargar();
    }

    private void cargar() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("categorias.json")) {
            if (is == null) return; // O lanzar excepción

            ObjectMapper mapper = new ObjectMapper();
            Map<String, List<String>> datos = mapper.readValue(is, new TypeReference<Map<String, List<String>>>(){});

            for (Map.Entry<String, List<String>> entry : datos.entrySet()) {
                String catOficial = entry.getKey();
                for (String sinonimo : entry.getValue()) {
                    mapa.put(NormalizadorCategorias.normalizar(sinonimo), catOficial);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String buscarPorTerminoNormalizado(String terminoNormalizado) {
        return mapa.getOrDefault(terminoNormalizado, "OTROS");
    }

    public List<String> obtenerCategoriasCanonicas() {
        return mapa.values().stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
