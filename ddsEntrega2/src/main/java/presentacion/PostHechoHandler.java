package presentacion;
import io.javalin.http;

public class PostHechoHandler implements Handler {

    private final RepositorioHechos repositorio = new RepositorioHechos();

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String bodyString = context.body();
        Hecho hecho = context.bodyAsClass(Hecho.class);

        System.out.println("Creando hecho: " + bodyString);
        repositorio.agregar(hecho);

        context.status(201);
    }
}

