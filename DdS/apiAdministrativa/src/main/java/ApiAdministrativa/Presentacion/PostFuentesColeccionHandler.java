package ApiAdministrativa.Presentacion;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Handler;
import utils.Dominio.HechosYColecciones.Coleccion;
import utils.Persistencia.ColeccionRepositorio;
import io.javalin.http.Context;
import utils.Dominio.fuente.Fuente;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.UUID;

public class PostFuentesColeccionHandler implements Handler {
    private final ColeccionRepositorio coleccionRepositorio;

    public PostFuentesColeccionHandler(ColeccionRepositorio coleccionRepositorio) {
        this.coleccionRepositorio = coleccionRepositorio;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String handle = ctx.pathParam("id");
        Fuente nueva = ctx.bodyAsClass(Fuente.class);

        try {
            Coleccion coleccion = coleccionRepositorio.buscarPorHandle(handle);

            if (coleccion == null) {
                ctx.status(404).result("Colección no encontrada");
                return;
            }

            coleccion.agregarFuente(nueva);

            coleccionRepositorio.actualizar(coleccion);

            ctx.status(201).result("Fuente agregada exitosamente");

        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Handle inválido: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Error interno: " + e.getMessage());
        }
    }
}
