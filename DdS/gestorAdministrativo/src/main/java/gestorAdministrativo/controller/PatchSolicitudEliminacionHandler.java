package gestorAdministrativo.controller;

import utils.Persistencia.SolicitudEliminacionRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.util.UUID;

public class PatchSolicitudEliminacionHandler implements Handler {
    private final SolicitudEliminacionRepositorio solicitudEliminacionRepositorio;

    public PatchSolicitudEliminacionHandler(SolicitudEliminacionRepositorio solicitudEliminacionRepositorio) {
        this.solicitudEliminacionRepositorio = solicitudEliminacionRepositorio;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        String idString = ctx.pathParam("id");
        String accion = ctx.body(); // "ACEPTADA" o "RECHAZADA"

        try {
            UUID id = UUID.fromString(idString);

            // USAR EL REPOSITORIO DIRECTAMENTE
            boolean resultado = solicitudEliminacionRepositorio.actualizarEstadoSolicitudEliminacion(accion, id);

            if (resultado) {
                System.out.println("✅ Solicitud " + accion + ": " + id);
                ctx.status(200).result("Solicitud " + accion.toLowerCase() + " correctamente");
            } else {
                System.out.println("❌ Solicitud no encontrada para actualizar: " + id);
                ctx.status(404).result("Solicitud no encontrada");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("❌ ID inválido: " + idString);
            ctx.status(400).result("ID inválido");
        }
    }
}