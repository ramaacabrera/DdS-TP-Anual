package presentacion;

import Persistencia.ColeccionRepositorio;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.example.agregador.Criterios.Criterio;
import org.example.agregador.HechosYColecciones.Coleccion;
import org.example.agregador.HechosYColecciones.Hecho;
import org.example.agregador.HechosYColecciones.TipoAlgoritmoConsenso;
import org.example.agregador.fuente.Fuente;

import java.util.List;
import java.util.Optional;

public class PutAlgoritmoConsensoHandler implements Handler {
    private final ColeccionRepositorio repositorio;

    public PutAlgoritmoConsensoHandler(ColeccionRepositorio coleccion) {repositorio = coleccion;}

    @Override
    public void handle(Context ctx) {
        String handle = ctx.pathParam("id");
        TipoAlgoritmoConsenso algoritmo= ctx.bodyAsClass(TipoAlgoritmoConsenso.class);
        final Optional<Coleccion> resultadoBusqueda = repositorio.buscarPorHandle(handle);
        if (resultadoBusqueda.isPresent()) {
            resultadoBusqueda.get().setAlgoritmoDeConsenso(algoritmo);
            repositorio.actualizar(resultadoBusqueda.get());
            ctx.status(200);
        } else{
            ctx.status(404);
        }
    }
}
