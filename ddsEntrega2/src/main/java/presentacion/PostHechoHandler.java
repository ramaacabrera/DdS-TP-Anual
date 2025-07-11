package presentacion;
import Persistencia.DinamicoRepositorio;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.example.agregador.Contribuyente;
import org.example.agregador.DTO.HechoDTO;
import org.example.fuenteDinamica.ControllerSubirHechos;
import org.jetbrains.annotations.NotNull;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;

public class PostHechoHandler implements Handler {

    private final ControllerSubirHechos controller;

    public PostHechoHandler(ControllerSubirHechos controllerNuevo) { controller = controllerNuevo; }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String bodyString = context.body();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(bodyString, new TypeReference<>() {});

        String hechoJson = mapper.writeValueAsString(map.get("hecho"));
        String contribuyenteJson = mapper.writeValueAsString(map.get("contribuyente"));

        HechoDTO hecho = mapper.readValue(hechoJson, HechoDTO.class);
        Contribuyente contribuyente = null;

        if (map.containsKey("contribuyente") && map.get("contribuyente") != null) {
            contribuyente = mapper.readValue(contribuyenteJson, Contribuyente.class);
        }

        if (contribuyente != null) {
            controller.subirHecho(hecho, contribuyente);
        } else {
            controller.subirHecho(hecho);
        }

        System.out.println("Creando hecho: " + bodyString);

        context.status(201);
    }
}
