package gestorAdministrativo.controller;

import gestorAdministrativo.dto.Hechos.AgregarEtiquetaDTO;
import gestorAdministrativo.service.HechoService;
import io.javalin.http.Handler;

import java.util.UUID;

public class HechoController {

    private HechoService hechoService;
    public HechoController(HechoService hechoService) {
        this.hechoService = hechoService;
    }

    public Handler agregarEtiquetas = ctx -> {

        System.out.println("Agregando etiquetas");
        UUID hechoId = UUID.fromString(ctx.pathParam("hechoId"));

        String nombre = ctx.formParam("nombre");

        if (nombre == null || nombre.trim().isEmpty()) {
            ctx.status(400).result("El nombre de la etiqueta es obligatorio");
            return;
        }

        try {
            hechoService.agregarEtiquetas(hechoId, nombre);
            ctx.status(200).result("Etiqueta agregada correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Error al agregar la etiqueta: " + e.getMessage());
        }
    };
}
