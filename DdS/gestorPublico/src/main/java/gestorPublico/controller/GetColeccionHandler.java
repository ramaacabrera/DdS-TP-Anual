package gestorPublico.controller;

import utils.Persistencia.ColeccionRepositorio;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import utils.Dominio.HechosYColecciones.Coleccion;

import java.io.IOException;
import java.net.URISyntaxException;

public class GetColeccionHandler implements Handler {
    //private final ColeccionRepositorio repositorio;
    ObjectMapper mapper = new ObjectMapper();
    private final ColeccionRepositorio coleccionRepositorio;

    public GetColeccionHandler(ColeccionRepositorio coleccionRepositorio) {
        this.coleccionRepositorio = coleccionRepositorio;
    }

//    public GetColeccionHandler(ColeccionRepositorio coleccion) {repositorio = coleccion;}

    @Override
    public void handle(Context ctx) throws URISyntaxException, IOException, InterruptedException {
        String handle = ctx.pathParam("id");

        if(!handle.trim().isEmpty()) {
            Coleccion coleccion = coleccionRepositorio.buscarPorHandle(handle);

            ctx.status(200).json(coleccion);

        } else {
            ctx.status(404);
        }

    }
}
