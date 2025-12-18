
package cargadorEstatico.service;

import cargadorEstatico.controller.ConexionEstatica;
import cargadorEstatico.controller.ConexionEstaticaDrive;
import cargadorEstatico.domain.fuente.Fuente;
import cargadorEstatico.dto.Hechos.FuenteDTO;
import cargadorEstatico.dto.Hechos.HechoDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static java.nio.file.Files.*;

public class HechosEstaticoService {

    String fileServer;
    Fuente fuente;
    public HechosEstaticoService(String path, Fuente fuenteNueva) {
        fileServer = path;
        fuente = fuenteNueva;
    }

    public List<HechoDTO> obtenerHechos(){
        List<HechoDTO> hechosTotales = new ArrayList<>();

        Path carpeta = Paths.get(fileServer); // "csv_files"
        Path pathGuia = carpeta.resolve("guia.csv");

        // Cargar archivo guía
        Set<String> procesados = cargarArchivosProcesados(pathGuia);

        try (DirectoryStream<Path> stream = newDirectoryStream(carpeta, "*.csv")) {
            for (Path archivo : stream) {
                String nombreArchivo = archivo.getFileName().toString();

                if (nombreArchivo.equals("guia.csv") || procesados.contains(nombreArchivo))
                    continue; // Saltear si ya fue procesado o es la guía

                ConexionEstatica conexion = new ConexionEstatica(fileServer + "/" + nombreArchivo);

                List<HechoDTO> hechos = conexion.obtenerHechos();
                fuente.setDescriptor(nombreArchivo);
                hechos.forEach(hecho -> {hecho.setFuente(new FuenteDTO(fuente.getId(),fuente.getTipoDeFuente().toString(),fuente.getDescriptor()));});
                hechosTotales.addAll(hechos); // o tus criterios

                procesados.add(nombreArchivo);
            }

            guardarGuia(pathGuia, procesados);

        } catch (IOException e) {
            System.err.println("Error al leer archivos CSV: " + e.getMessage());
        }

        hechosTotales.forEach(hecho->hecho.setFuente(new FuenteDTO(fuente.getId(),fuente.getTipoDeFuente().toString(),fuente.getDescriptor())));
        return hechosTotales;
    }

    public List<HechoDTO> obtenerHechosDrive() {
        List<HechoDTO> hechosTotales = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            System.out.println("=== OBTENIENDO HECHOS DE GOOGLE DRIVE ===");

            // Obtener instancia del cargador de Drive
            ConexionEstaticaDrive driveCargador = ConexionEstaticaDrive.getInstance();

            List<HechoDTO> hechosDrive = driveCargador.obtenerHechos();

            for (HechoDTO hecho : hechosDrive) {
                try {
                    System.out.println("Hecho obtenido: " + mapper.writeValueAsString(hecho));
                } catch (Exception e) {
                    System.out.println("Hecho obtenido (sin serializar): " + hecho);
                }
                hechosTotales.add(hecho);
            }

            System.out.println("=== PROCESO COMPLETADO ===");

        } catch (Exception e) {
            System.err.println("Error obteniendo hechos de Drive: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al obtener hechos de Google Drive", e);
        }

        System.out.println("Total hechos obtenidos: " + hechosTotales.size());
        return hechosTotales;
    }


    private Set<String> cargarArchivosProcesados(Path pathGuia) {
        Set<String> procesados = new HashSet<>();
        if (!exists(pathGuia)) return procesados;

        try (BufferedReader reader = newBufferedReader(pathGuia)) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                procesados.add(linea.trim());
            }
        } catch (IOException e) {
            System.err.println("No se pudo leer la guía: " + e.getMessage());
        }
        return procesados;
    }

    private void actualizarGuiaProcesados(Set<String> procesados) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("guia.csv"))) {
            writer.write("Última sincronización: " + new Date());
            writer.newLine();
            writer.write("Hechos obtenidos desde Google Drive");
        } catch (IOException e) {
            System.err.println("No se pudo escribir la guía: " + e.getMessage());
        }
    }


    private void guardarGuia(Path pathGuia, Set<String> procesados) {
        try (BufferedWriter writer = newBufferedWriter(pathGuia)) {
            for (String archivo : procesados) {
                writer.write(archivo);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("No se pudo escribir la guía: " + e.getMessage());
        }
    }

}
