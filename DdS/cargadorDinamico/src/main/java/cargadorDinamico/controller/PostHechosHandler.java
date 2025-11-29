package cargadorDinamico.controller;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import cargadorDinamico.domain.DinamicaDto.Hecho_D_DTO;
import cargadorDinamico.service.HechosDinamicoService;

public class PostHechosHandler implements Handler {

    private final HechosDinamicoService service;

    public PostHechosHandler(HechosDinamicoService service) {
        this.service = service;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String jsonBody = ctx.body();
        Hecho_D_DTO nueva = ctx.bodyAsClass(Hecho_D_DTO.class);

        service.guardarHecho(nueva);

        ctx.status(201).result("HechoDTO creado con éxito.");
    }
}
