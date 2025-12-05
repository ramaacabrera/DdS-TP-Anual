package gestorAdministrativo.controller;

import gestorAdministrativo.domain.Criterios.Criterio;
import gestorAdministrativo.dto.Coleccion.ColeccionDTO;
import gestorAdministrativo.dto.Criterios.CriterioDTO;
import gestorAdministrativo.dto.Criterios.CriterioUbicacionDTO;
import gestorAdministrativo.dto.Hechos.FuenteDTO;
import gestorAdministrativo.domain.HechosYColecciones.TipoAlgoritmoConsenso;
import gestorAdministrativo.domain.fuente.Fuente;
import gestorAdministrativo.domain.fuente.TipoDeFuente;
import gestorAdministrativo.service.ColeccionService;
import io.javalin.http.Handler;

import java.util.UUID;

public class ColeccionController {
    private ColeccionService coleccionService = null;


    public ColeccionController(ColeccionService coleccionService) {
        this.coleccionService = coleccionService;
    }

    public Handler crearColeccion = ctx -> {
        try {
            System.out.println("Creando coleccion");
            System.out.println(ctx.body());
            ColeccionDTO request = ctx.bodyAsClass(ColeccionDTO.class);

            System.out.println(request.getCriteriosDePertenencia().size());

            System.out.println("DTO creado: " + request.toString());
            for(CriterioDTO c : request.getCriteriosDePertenencia()){
                if(c instanceof CriterioUbicacionDTO){
                    System.out.println("Ubicacion: " + ((CriterioUbicacionDTO) c).getUbicacion().getDescripcion());
                }
            }

            if (request.getTitulo() == null || request.getTitulo().trim().isEmpty()) {
                ctx.status(400).json("El título es requerido");
                return;
            }

            ColeccionDTO response = this.coleccionService.crearColeccion(request);
            System.out.println(response.getCriteriosDePertenencia().size());
            ctx.status(201).json(response);

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(400).json("Error creando colección: " + e.getMessage());
        }
    };

    public Handler actualizarColeccion = ctx -> {
        String idString = ctx.pathParam("id");
        System.out.println("Actualizando coleccion: " + idString);
        try {
            UUID id = UUID.fromString(idString);
            System.out.println("Creando dto");
            ColeccionDTO request = ctx.bodyAsClass(ColeccionDTO.class);

            System.out.println("Coleccion dto creada");

            for(FuenteDTO fuente : request.getFuentes()){
                System.out.println("Fuentes del dto: " + fuente.getTipoFuente() + " " + fuente.getDescriptor() + " " + fuente.getFuenteId());
            }

            ColeccionDTO response = coleccionService.actualizarColeccion(id, request);

            System.out.println("Se actualizo exitosamente");
            ctx.status(200).json(response);

        } catch (IllegalArgumentException e) {
            ctx.status(404).json("Colección no encontrada o ID inválido");
        } catch (Exception e) {
            ctx.status(400).json("Error actualizando colección: " + e.getMessage());
        }
    };

    public Handler eliminarColeccion = ctx -> {
        System.out.println("Comenzando a borrar");
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
            FuenteDTO fuenteDto = ctx.bodyAsClass(FuenteDTO.class);

            ColeccionDTO response = coleccionService.agregarFuente(id, fuenteDto);
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
            FuenteDTO fuenteDto = ctx.bodyAsClass(FuenteDTO.class);

            Fuente fuenteParaBorrar = new Fuente();
            fuenteParaBorrar.setId(fuenteDto.getFuenteId());
            if (fuenteDto.getDescriptor() != null) {
                fuenteParaBorrar.setDescriptor(fuenteDto.getDescriptor());
            }
            if (fuenteDto.getTipoFuente() != null) {
                try {
                    fuenteParaBorrar.setTipoDeFuente(TipoDeFuente.valueOf(fuenteDto.getTipoFuente()));
                } catch (Exception ignored) {}
            }

            ColeccionDTO response = coleccionService.eliminarFuente(id, fuenteParaBorrar);
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
}