package agregador.Handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import utils.Dominio.HechosYColecciones.Coleccion;
import utils.Persistencia.ColeccionRepositorio;
import utils.Dominio.Criterios.Criterio;
import com.fasterxml.jackson.core.type.TypeReference;
import io.javalin.http.Handler;

import java.util.List;
import java.util.Optional;

public class PutColeccionRepoHandler implements Handler {
    private final ColeccionRepositorio repositorio;

    public PutColeccionRepoHandler(ColeccionRepositorio coleccion) {repositorio = coleccion;}

    @Override
    public void handle(Context ctx) {
        String handle = ctx.pathParam("id");
        Optional<Coleccion> resultadoBusqueda = repositorio.buscarPorHandle(handle);
        if (resultadoBusqueda.isPresent()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode body = mapper.readTree((ctx.body()));
                if(body.has("titulo")){
                    resultadoBusqueda.get().setTitulo(body.get("titulo").asText());
                }
                if(body.has("descripcion")){
                    resultadoBusqueda.get().setDescripcion(body.get("descripcion").asText());
                }
                if(body.has("criteriosDePertenencia")){
                    List<Criterio> nuevosCriterios = mapper.readValue(body.get("criteriosDePertenencia")
                                    .toString(), new TypeReference<List<Criterio>>(){});
                    resultadoBusqueda.get().setCriteriosDePertenencia(nuevosCriterios);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            ctx.status(200);
        } else{
            ctx.status(404);
        }
    }
}
