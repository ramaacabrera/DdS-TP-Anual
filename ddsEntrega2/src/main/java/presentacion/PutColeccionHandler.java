package presentacion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.example.agregador.HechosYColecciones.Coleccion;
import Persistencia.ColeccionRepositorio;
import org.example.agregador.Criterios.Criterio;
import org.example.agregador.HechosYColecciones.Hecho;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Optional;

public class PutColeccionHandler implements Handler {
    private final ColeccionRepositorio repositorio;

    public PutColeccionHandler(ColeccionRepositorio coleccion) {repositorio = coleccion;}

    @Override
    public void handle(Context ctx) {
        String handle = ctx.pathParam("id");
        final Optional<Coleccion> resultadoBusqueda = repositorio.buscarPorHandle(handle);
        if (resultadoBusqueda.isPresent()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode body = mapper.readTree((ctx.body()));
                if(body.has("hechos")){
                    List<Hecho> nuevosHechos = mapper.readValue(body.get("hechos").toString(),
                            new TypeReference<List<Hecho>>(){});
                    resultadoBusqueda.get().setHechos(nuevosHechos);
                }
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
