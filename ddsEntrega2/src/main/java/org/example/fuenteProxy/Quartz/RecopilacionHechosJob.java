package org.example.fuenteProxy.Quartz;

import org.quartz.Job;
import org.example.fuenteProxy.ConexionDemo;
import org.example.agregador.Hecho;
import org.example.fuente.HechoDTO;
import org.example.fuente.Fuente;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.net.URL;
import java.util.List;

public class RecopilacionHechosJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        String apiUrlStr = dataMap.getString("apiUrl");
        LocalDateTime ultimaConsultaFecha = (LocalDateTime) dataMap.get("ultimaConsultaFecha");
        ConexionDemo conexionDemo = (ConexionDemo) dataMap.get("conexionDemoInstance");
        Fuente fuenteDemoMetaMapa = (Fuente) dataMap.get("fuenteDemoMetaMapaInstance");

        if (conexionDemo == null || ultimaConsultaFecha == null || apiUrlStr == null || fuenteDemoMetaMapa == null) {
            throw new JobExecutionException("Faltan parámetros en JobDataMap para RecopilacionHechosJob.");
        }

        try {
            URL apiUrl = new URL(apiUrlStr);
            System.out.println("--- Ejecutando tarea de recopilación de hechos (Quartz) ---");
            System.out.println("Fecha de última consulta enviada a la API: " + ultimaConsultaFecha.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            List<HechoDTO> nuevosHechosDTO = conexionDemo.obtenerNuevosHechos(apiUrl, ultimaConsultaFecha, fuenteDemoMetaMapa);

            if (nuevosHechosDTO != null && !nuevosHechosDTO.isEmpty()) {
                System.out.println("Recopilador: Se encontraron " + nuevosHechosDTO.size() + " nuevos hechos.");
                for (HechoDTO hechoDTO : nuevosHechosDTO) {
                    Hecho hechoInterno = new Hecho(hechoDTO);

                    if (hechoInterno != null) {
                        System.out.println("Recopilador: Hecho [Título: " + hechoInterno.getTitulo() + "] recuperado y mapeado.");
                        //agregador.agregarHecho(hechoInterno); FALTA ESTO *(entrega 3)* ver si cambia o sea si el agregador agrega de a 1 o lista

                        System.out.println("  HechoDTO - Titulo: " + hechoDTO.getTitulo() + ", Ubicacion: " + (hechoDTO.getUbicacion() != null ? hechoDTO.getUbicacion().getDescripcion() : "N/A"));
                        System.out.println("  Hecho Model - Titulo: " + hechoInterno.getTitulo() + ", Ubicacion: " + (hechoInterno.getUbicacion() != null ? hechoInterno.getUbicacion().getDescripcion() : "N/A"));
                    } else {
                        System.out.println("Recopilador: No se pudo mapear un HechoDTO a un objeto Hecho interno.");
                    }
                }
                dataMap.put("ultimaConsultaFecha", LocalDateTime.now());
                System.out.println("Recopilador: Fecha de última consulta actualizada a: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } else {
                System.out.println("Recopilador: No se encontraron nuevos hechos en esta ejecución.");
            }
        } catch (Exception e) {
            System.err.println("Recopilador: Error durante la ejecución del Job de recopilación de hechos: " + e.getMessage());
            e.printStackTrace();
            throw new JobExecutionException(e);
        }
    }

}