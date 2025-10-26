package Agregador.Presentacion;

import Agregador.Cargador.ConexionCargador;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import utils.DTO.FuenteDTO;

public class PostFuenteHandler implements Handler {
    private final ConexionCargador conexionCargador;

    public PostFuenteHandler(ConexionCargador conexionCargadorNuevo) { conexionCargador = conexionCargadorNuevo; }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        FuenteDTO nuevo = ctx.bodyAsClass(FuenteDTO.class);

        if(conexionCargador.agregarFuente(nuevo)){
            ctx.status(201).result("Fuente agregada con éxito.");
        } else {
            ctx.status(409).result("La Fuente ya existe en el sistema.");
        }

    }
}