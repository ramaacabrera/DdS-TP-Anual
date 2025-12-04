package estadisticas.utils;

import estadisticas.domain.Estadisticas;
import estadisticas.domain.EstadisticasCategoria;
import estadisticas.domain.EstadisticasColeccion;
import java.util.List;

public class EstadisticasCSVTransformer {

    public static String transformarAFormatoCSV(Estadisticas estadisticas,
                                                List<EstadisticasCategoria> categorias,
                                                List<EstadisticasColeccion> colecciones) {
        StringBuilder csv = new StringBuilder();

        csv.append("sep=;\n");

        csv.append("--- REPORTE GENERAL DE ESTADISTICAS ---\n");
        csv.append("ID Reporte;Fecha Generacion;Spam Detectado;Categoria Mas Frecuente\n");
        csv.append(String.format("%s;%s;%d;%s\n\n",
                estadisticas.getEstadisticas_id(),
                estadisticas.getEstadisticas_fecha(),
                estadisticas.getSpam(),
                estadisticas.getCategoria_max_hechos()
        ));

        csv.append("--- DETALLE POR CATEGORIA ---\n");
        csv.append("Categoria;Provincia Mas Activa;Hora Pico\n");
        for (EstadisticasCategoria cat : categorias) {
            csv.append(String.format("%s;%s;%d\n",
                    cat.getCategoria(),
                    cat.getProvincia(),
                    cat.getHora()
            ));
        }
        csv.append("\n");

        csv.append("--- DETALLE POR COLECCION ---\n");
        csv.append("ID Coleccion;Titulo;Provincia Principal\n");
        for (EstadisticasColeccion col : colecciones) {
            csv.append(String.format("%s;%s;%s\n",
                    col.getColeccionId(),
                    col.getTitulo(),
                    col.getProvincia()
            ));
        }

        return csv.toString();
    }
}