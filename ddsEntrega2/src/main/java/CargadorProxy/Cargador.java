package CargadorProxy;

import java.util.ArrayList;
import java.util.List;

public class Cargador {
    private final List<ConexionProxy> conexiones;

    public Cargador(){ this.conexiones = new ArrayList<>();}

    //public void agregarConexion(ConexionProxy conexion){conexiones.add(conexion);}

    public List<ConexionProxy> getConexiones(){return conexiones;}
}
