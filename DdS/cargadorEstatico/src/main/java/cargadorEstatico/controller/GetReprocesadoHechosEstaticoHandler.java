package cargadorEstatico.controller;

import cargadorEstatico.domain.fuente.Fuente;
import cargadorEstatico.dto.Hechos.FuenteDTO;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import cargadorEstatico.dto.Hechos.HechoDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class GetReprocesadoHechosEstaticoHandler implements Handler {

    String fileServerPath;
    Fuente fuente;

    public GetReprocesadoHechosEstaticoHandler(String path, Fuente fuenteNueva) {
        fileServerPath = path;
        fuente = fuenteNueva;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String nombreArchivo = context.pathParam("nombre");

        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            context.status(400).json(Map.of("error", "Nombre de archivo requerido"));
            return;
        }

        if (!nombreArchivo.endsWith(".csv")) {
            context.status(400).json(Map.of("error", "Solo se permiten archivos CSV"));
            return;
        }

        try {
            List<HechoDTO> hechos = this.obtenerHechosDeArchivo(nombreArchivo);
            hechos.forEach(hecho->hecho.setFuente(new FuenteDTO(fuente.getId(),fuente.getTipoDeFuente().toString(),fuente.getDescriptor())));
            context.json(hechos);
            context.status(200);
        } catch (IOException e) {
            context.status(404).json(Map.of("error", "Archivo no encontrado: " + nombreArchivo));
        } catch (Exception e) {
            context.status(500).json(Map.of("error", "Error procesando archivo: " + e.getMessage()));
        }
    }

    public List<HechoDTO> obtenerHechosDeArchivo(String nombreArchivo) throws IOException {
        Path archivoPath = Paths.get(fileServerPath).resolve(nombreArchivo);

        if (!Files.exists(archivoPath)) {
            throw new IOException("Archivo no encontrado: " + nombreArchivo);
        }

        if (!Files.isRegularFile(archivoPath)) {
            throw new IOException("No es un archivo válido: " + nombreArchivo);
        }

        ConexionEstatica conexion = new ConexionEstatica(archivoPath.toString());
        return conexion.obtenerHechos();
    }
}