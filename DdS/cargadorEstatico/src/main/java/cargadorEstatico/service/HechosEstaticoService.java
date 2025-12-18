
package cargadorEstatico.service;

import cargadorEstatico.controller.ConexionEstaticaDrive;
import cargadorEstatico.domain.fuente.Fuente;
import cargadorEstatico.dto.Hechos.HechoDTO;
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
