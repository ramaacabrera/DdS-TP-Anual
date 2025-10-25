package utils.Conexiones;

import java.util.ArrayList;
import java.util.List;

public class Cargador {
    private final List<FuenteExternaConexion> conexiones;

    public Cargador(){ this.conexiones = new ArrayList<>();}

    public void agregarConexion(FuenteExternaConexion conexion){conexiones.add(conexion);}

    public List<FuenteExternaConexion> getConexiones(){return conexiones;}
}
