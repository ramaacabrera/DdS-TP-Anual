package CargadorProxy;

import java.util.ArrayList;
import java.util.List;
import Agregador.fuente.Fuente;
import kotlin.collections.ArrayDeque;

public class Cargador {
    private List<Fuente> fuentes;

    public Cargador(){ this.fuentes = new ArrayList<Fuente>();}

    public void agregarFuente(Fuente fuente){fuentes.add(fuente);}

    public List<Fuente> getFuentes(){return fuentes;}
}
