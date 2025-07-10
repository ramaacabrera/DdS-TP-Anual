package presentacion;
import Persistencia.DinamicoRepositorio;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.example.agregador.DTO.HechoDTO;
import org.jetbrains.annotations.NotNull;
import Persistencia.HechoRepositorio;

public class PostHechoHandler implements Handler {

    private final DinamicoRepositorio repositorio;

    public PostHechoHandler(DinamicoRepositorio dinamicoRepositorio) { repositorio = dinamicoRepositorio; }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String bodyString = context.body();
        ObjectMapper mapper = new ObjectMapper();
        HechoDTO hecho = mapper.readValue(bodyString, HechoDTO.class);

        System.out.println("Creando hecho: " + bodyString);
        repositorio.guardarHecho(hecho);

        context.status(201);
    }
}
