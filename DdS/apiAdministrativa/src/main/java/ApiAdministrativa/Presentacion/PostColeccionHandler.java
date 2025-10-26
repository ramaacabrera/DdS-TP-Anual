package ApiAdministrativa.Presentacion;

import utils.Persistencia.ColeccionRepositorio;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Handler;
import io.javalin.http.Context;
import utils.DTO.ColeccionDTO;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PostColeccionHandler implements Handler {
    private final ColeccionRepositorio coleccionRepositorio;

    public PostColeccionHandler(ColeccionRepositorio coleccionRepositorio) {
        this.coleccionRepositorio = coleccionRepositorio;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try{
            ColeccionDTO nueva = ctx.bodyAsClass(ColeccionDTO.class);

            coleccionRepositorio.guardar(nueva);

            ctx.status(201).result("Coleccion agregada exitosamente");
        } catch (Exception e) {
            ctx.status(500).result("Error interno: " + e.getMessage());
        }
    }
}
