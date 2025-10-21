<<<<<<<< HEAD:DdS/agregador/src/main/java/agregador/Handlers/DeleteFuenteRepoHandler.java
package agregador.Handlers;

import utils.Dominio.HechosYColecciones.Coleccion;
import utils.Persistencia.ColeccionRepositorio;
import utils.Dominio.fuente.Fuente;
========
package Agregador.Handlers;

import Agregador.HechosYColecciones.Coleccion;
import Agregador.Persistencia.ColeccionRepositorio;
import Agregador.fuente.Fuente;
>>>>>>>> c99213c (Connect y close en agregador implementados):ddsEntrega2/src/main/java/Agregador/Handlers/DeleteFuenteRepoHandler.java
import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

public class DeleteFuenteRepoHandler implements Handler {
    private final ColeccionRepositorio repositorio;

    public DeleteFuenteRepoHandler(ColeccionRepositorio repositorio) {this.repositorio = repositorio;}

    @Override
    public void handle(Context context) throws IOException, URISyntaxException, InterruptedException {
        String handle = context.pathParam("id");
        Fuente fuente = context.bodyAsClass(Fuente.class);

        Optional<Coleccion> resultadoBusqueda = repositorio.buscarPorHandle(handle);
        if(resultadoBusqueda.isPresent()){
            resultadoBusqueda.get().eliminarFuente(fuente);
            context.status(200);
        } else {
            context.status(404);
        }
    }
}
