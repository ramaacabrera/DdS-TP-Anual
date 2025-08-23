package FuenteDinamica;

import Persistencia.DinamicoRepositorio;
import Agregador.HechosYColecciones.Hecho;
import Agregador.DTO.HechoDTO;
import Agregador.Contribuyente;

public class ControllerSubirHechos {
    private final DinamicoRepositorio baseDeDatos;

    public ControllerSubirHechos(DinamicoRepositorio baseDeDatos){
        this.baseDeDatos = baseDeDatos;
    }

    public void subirHecho(HechoDTO hecho){
        baseDeDatos.guardarHecho(hecho);
    }

    public void subirHecho(HechoDTO hecho, Contribuyente contribuyente){
        baseDeDatos.guardarHecho(hecho);
        this.notificar(hecho, contribuyente);
    }

    public void notificar(HechoDTO hechoDTO, Contribuyente contribuyente){
        contribuyente.hechoSubido(new Hecho(hechoDTO));
    }

}
