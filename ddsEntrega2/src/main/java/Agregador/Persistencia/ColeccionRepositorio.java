package Agregador.Persistencia;
import Agregador.HechosYColecciones.Coleccion;
import utils.DTO.ColeccionDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ColeccionRepositorio {
    private static final List<Coleccion> colecciones = new ArrayList<>();

    public void guardar(Coleccion coleccion) {
        colecciones.add(coleccion);
    }

    public void guardar(ColeccionDTO coleccionDTO) {
        colecciones.add(new Coleccion(coleccionDTO));
    }

    public List<Coleccion> obtenerTodas() {
        return colecciones;
    }

    public Optional<Coleccion> buscarPorHandle(String handle) {
        return colecciones.stream().filter(c -> c.getHandle().equals(handle)).findFirst();
    }

    public void actualizar(Coleccion coleccion) {
        Optional<Coleccion> col = this.buscarPorHandle(coleccion.getHandle());
        Coleccion buscar;
        if(col.isPresent()){
            buscar = col.get();
            colecciones.set(colecciones.indexOf(buscar), coleccion);
        }
    }

    public void eliminar(Coleccion coleccion) {colecciones.remove(coleccion);}
}
