package FuenteEstatica;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class GetHechosEstaticoHandler implements Handler, Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        List<HechosDTO> hechos = this.obtenerHechos();
        context.json(hechos);
        context.status(200);
    }

    public void obtenerHechos(){

    }
}
