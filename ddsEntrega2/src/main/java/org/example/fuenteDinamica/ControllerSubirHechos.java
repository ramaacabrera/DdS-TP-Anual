package org.example.fuenteDinamica;

import Persistencia.DinamicoRepositorio;
import org.example.agregador.HechosYColecciones.Hecho;
import org.example.agregador.DTO.HechoDTO;
import org.example.agregador.Contribuyente;

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
