package utils;

import CargadorMetamapa.MainMetamapa;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LecturaConfig {

    public Properties leerConfig() {
        Properties config = new Properties();
        try{
            InputStream input = MainMetamapa.class.getClassLoader()
                    .getResourceAsStream("componentes.properties");
            if(input == null){
                throw new FileNotFoundException("No se encontro el archivo de propiedades");
            }
            config.load(input);
            return config;
        } catch (IOException e){
            System.out.println("Error al leer el archivo de propiedades");
            System.err.println(e.getMessage());
            return null;
        }

    }
}
