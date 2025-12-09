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
        UUID hechoId = UUID.fromString(ctx.pathParam("id"));

        AgregarEtiquetaDTO etiquetaDTO = ctx.bodyAsClass(AgregarEtiquetaDTO.class);

        if (etiquetaDTO.getNombre() == null || etiquetaDTO.getNombre().trim().isEmpty()) {
            ctx.status(400).result("El nombre de la etiqueta es obligatorio");
            return;
        }

        try {
            hechoService.agregarEtiquetas(hechoId, etiquetaDTO.getNombre());
            ctx.status(200).result("Etiqueta agregada correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Error al agregar la etiqueta: " + e.getMessage());
        }
    };
}
