package presentacion;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.example.agregador.Hecho;
import org.jetbrains.annotations.NotNull;
import org.example.agregador.HechoRepositorio;

public class PostHechoHandler implements Handler {

    private final HechoRepositorio repositorio;

    public PostHechoHandler(HechoRepositorio hechoRepositorio) { repositorio = hechoRepositorio; }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String bodyString = context.body();
        Hecho hecho = context.bodyAsClass(Hecho.class);

        System.out.println("Creando hecho: " + bodyString);
        repositorio.guardar(hecho);

        context.status(201);
    }
}
