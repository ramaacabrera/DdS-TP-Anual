package org.example;

public class FuenteProxy extends Fuente {

    private Conexion conexion; // falta hacer clase
    private URL url;

    public FuenteProxy(Conexion conexion, URL url) {
        this.conexion = conexion;
        this.url = url;
    }

    @Override
    public List<Hecho> obtenerHechos() {
        List<Hecho> hechos = new ArrayList<>();
        Date ultimaConsulta = new Date(); // Fecha de la última consulta

        Map<String, Object> datosHecho = conexion.siguienteHecho(url, ultimaConsulta);

        while (datosHecho != null) {
            Hecho hecho = convertirAHecho(datosHecho);
            hechos.add(hecho);

            //actualizar la fecha de la última consulta
            ultimaConsulta = new Date();

            datosHecho = conexion.siguienteHecho(url, ultimaConsulta);
        }

        return hechos;
    }

    private Hecho convertirAHecho(Map<String, Object> datosHecho) {
        // Crear un objeto Hecho desde el mapa recibido
        // Esto depende de cómo estructuren el mapeo
        return new Hecho(...); // Armar con sus campos.
    }
}

