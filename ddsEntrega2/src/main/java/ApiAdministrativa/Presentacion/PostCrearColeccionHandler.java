package ApiAdministrativa.Presentacion;

import Agregador.HechosYColecciones.Coleccion;
import Agregador.Persistencia.ColeccionRepositorio;
import Agregador.Criterios.Criterio;
import Agregador.HechosYColecciones.TipoAlgoritmoConsenso;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PostCrearColeccionHandler implements Handler {

    private final ColeccionRepositorio coleccionRepositorio;

    public PostCrearColeccionHandler(ColeccionRepositorio repo) {
        this.coleccionRepositorio = repo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {

        // Obtener datos del formulario
        String titulo = ctx.formParam("titulo");
        String descripcion = ctx.formParam("descripcion");

        TipoAlgoritmoConsenso algoritmo = TipoAlgoritmoConsenso.valueOf(ctx.formParam("algoritmo"));
        List<Criterio> criterios = new ArrayList<>(); // por ahora vacio

        // Crea la entidad
        Coleccion nueva = new Coleccion(titulo, descripcion, criterios, algoritmo);

        // Guarda en la base
        coleccionRepositorio.guardar(nueva);

        // Redirigir o renderizar mensaje de éxito
        ctx.render("coleccion-creada.ftlh",
                Map.of("titulo", titulo));
    }
}

