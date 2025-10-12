package Estadisticas;

import utils.LecturaConfig;
import java.util.Properties;


public class MainEstadisticas {
    public static void main(String[] args){
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puertoAgregador = Integer.parseInt(config.getProperty("puertoAgregador"));

        // Ver forma de obtener la ruta del agregador

        String url = "";

        GeneradorEstadisticas generador = new GeneradorEstadisticas(url);

    }
}
