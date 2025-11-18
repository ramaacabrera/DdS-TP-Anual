// gestorAdministrativo/controller/ColeccionController.java
package gestorAdministrativo.controller;

import DominioGestorAdministrativo.DTO.Coleccion.ColeccionDTO;
import gestorAdministrativo.service.ColeccionService;
import io.javalin.http.Handler;
import utils.Dominio.HechosYColecciones.TipoAlgoritmoConsenso;
import utils.Dominio.fuente.Fuente;

import java.util.List;
import java.util.UUID;

public class ColeccionController {
    private final ColeccionService coleccionService;

    public ColeccionController(ColeccionService coleccionService) {
        this.coleccionService = coleccionService;
    }

    public Handler crearColeccion = ctx -> {
        try {
            ColeccionDTO request = ctx.bodyAsClass(ColeccionDTO.class);

            if (request.getTitulo() == null || request.getTitulo().trim().isEmpty()) {
                ctx.status(400).json("El título es requerido");
                return;
            }

            ColeccionDTO response = coleccionService.crearColeccion(request);
            ctx.status(201).json(response);

        } catch (Exception e) {
            ctx.status(400).json("Error creando colección: " + e.getMessage());
        }
    };

    public Handler actualizarColeccion = ctx -> {
        String idString = ctx.pathParam("id");

        try {
            UUID id = UUID.fromString(idString);
            ColeccionDTO request = ctx.bodyAsClass(ColeccionDTO.class);

            ColeccionDTO response = coleccionService.actualizarColeccion(id, request);
            ctx.status(200).json(response);

        } catch (IllegalArgumentException e) {
            ctx.status(404).json("Colección no encontrada");
        } catch (Exception e) {
            ctx.status(400).json("Error actualizando colección: " + e.getMessage());
        }
    };

    public Handler eliminarColeccion = ctx -> {
        String idString = ctx.pathParam("id");

        try {
            UUID id = UUID.fromString(idString);
            coleccionService.eliminarColeccion(id);
            ctx.status(200).json("Colección eliminada exitosamente");

        } catch (IllegalArgumentException e) {
            ctx.status(404).json("Colección no encontrada");
        } catch (Exception e) {
            ctx.status(400).json("Error eliminando colección: " + e.getMessage());
        }
    };

    public Handler agregarFuente = ctx -> {
        String idString = ctx.pathParam("id");

        try {
            UUID id = UUID.fromString(idString);
            Fuente fuente = ctx.bodyAsClass(Fuente.class);

            ColeccionDTO response = coleccionService.agregarFuente(id, fuente);
            ctx.status(200).json(response);

        } catch (IllegalArgumentException e) {
            ctx.status(404).json("Colección no encontrada");
        } catch (Exception e) {
            ctx.status(400).json("Error agregando fuente: " + e.getMessage());
        }
    };

    public Handler borrarFuente = ctx -> {
        String idString = ctx.pathParam("id");

        try {
            UUID id = UUID.fromString(idString);
            Fuente fuente = ctx.bodyAsClass(Fuente.class);

            ColeccionDTO response = coleccionService.eliminarFuente(id, fuente);
            ctx.status(200).json(response);

        } catch (IllegalArgumentException e) {
            ctx.status(404).json("Colección no encontrada");
        } catch (Exception e) {
            ctx.status(400).json("Error eliminando fuente: " + e.getMessage());
        }
    };

    public Handler actualizarAlgoritmoConsenso = ctx -> {
        String idString = ctx.pathParam("id");

        try {
            UUID id = UUID.fromString(idString);
            TipoAlgoritmoConsenso algoritmo = ctx.bodyAsClass(TipoAlgoritmoConsenso.class);

            ColeccionDTO response = coleccionService.actualizarAlgoritmoConsenso(id, algoritmo);
            ctx.status(200).json(response);

        } catch (IllegalArgumentException e) {
            ctx.status(404).json("Colección no encontrada");
        } catch (Exception e) {
            ctx.status(400).json("Error actualizando algoritmo: " + e.getMessage());
        }
    };

    // Handlers adicionales para GET
    public Handler obtenerTodasLasColecciones = ctx -> {
        try {
            List<ColeccionDTO> colecciones = coleccionService.obtenerTodasLasColecciones();
            ctx.status(200).json(colecciones);
        } catch (Exception e) {
            ctx.status(500).json("Error obteniendo colecciones: " + e.getMessage());
        }
    };

    public Handler obtenerColeccionPorId = ctx -> {
        String idString = ctx.pathParam("id");

        try {
            UUID id = UUID.fromString(idString);
            ColeccionDTO coleccion = coleccionService.obtenerColeccionPorId(id);
            ctx.status(200).json(coleccion);

        } catch (IllegalArgumentException e) {
            ctx.status(404).json("Colección no encontrada");
        } catch (Exception e) {
            ctx.status(500).json("Error obteniendo colección: " + e.getMessage());
        }
    };
}