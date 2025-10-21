<<<<<<<< HEAD:DdS/agregador/src/main/java/agregador/Handlers/GetHechosRepoHandler.java
package agregador.Handlers;
========
package Agregador.Handlers;
>>>>>>>> c99213c (Connect y close en agregador implementados):ddsEntrega2/src/main/java/Agregador/Handlers/GetHechosRepoHandler.java

import utils.Dominio.HechosYColecciones.Hecho;
import utils.Persistencia.HechoRepositorio;
import org.jetbrains.annotations.NotNull;
import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.util.List;

public class GetHechosRepoHandler implements Handler {

    private final HechoRepositorio repositorio;

    public GetHechosRepoHandler(HechoRepositorio hechos) { repositorio = hechos; }

    public void handle(@NotNull Context ctx) throws Exception {
        List<Hecho> hechos = repositorio.getHechos();
        System.out.println(hechos.get(0).getFuente());
        ctx.json(hechos);
    }
}
