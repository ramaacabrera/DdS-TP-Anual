package cargadorDinamico.Presentacion;

import cargadorDinamico.DinamicoRepositorio;
<<<<<<<< HEAD:DdS/cargadorDinamico/src/main/java/cargadorDinamico/Presentacion/PostHechosHandler.java
import cargadorDinamico.Dominio.HechosYColeccionesD.Hecho_D;
========
import CargadorDinamica.Dominio.HechosYColecciones.Hecho_D;
>>>>>>>> 198c43e (Pruebas):ddsEntrega2/cargadorDinamico/src/main/java/cargadorDinamico/Presentacion/PostHechosHandler.java
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import cargadorDinamico.DinamicaDto.Hecho_D_DTO;

public class PostHechosHandler implements Handler {

    private final DinamicoRepositorio repositorio;

    public PostHechosHandler(DinamicoRepositorio repositorioNuevo) {
        this.repositorio = repositorioNuevo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String jsonBody = ctx.body();
        Hecho_D_DTO nueva = ctx.bodyAsClass(Hecho_D_DTO.class);

        Hecho_D entidad = new Hecho_D(nueva);

        System.out.println("Creando hechoDTO: " + jsonBody);
        repositorio.guardarHecho(entidad);

        ctx.status(201).result("HechoDTO creado con éxito.");
    }
}
