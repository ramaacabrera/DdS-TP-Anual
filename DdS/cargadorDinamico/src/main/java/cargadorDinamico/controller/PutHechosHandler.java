package cargadorDinamico.controller;

import cargadorDinamico.domain.DinamicaDto.Hecho_D_DTO;
import cargadorDinamico.service.HechosDinamicoService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class PutHechosHandler implements Handler {

    private final HechosDinamicoService service;

    public PutHechosHandler(HechosDinamicoService service) {
        this.service = service;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {

        String id = ctx.pathParam("id");
        Hecho_D_DTO dto = ctx.bodyAsClass(Hecho_D_DTO.class);

        boolean actualizado = service.actualizarHecho(id, dto);

        if (!actualizado) {
            ctx.status(404).result("Hecho no encontrado");
            return;
        }

        ctx.status(200).result("Hecho actualizado correctamente");
    }
}
