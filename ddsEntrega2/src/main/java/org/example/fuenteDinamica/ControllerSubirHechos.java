package org.example.fuenteDinamica;

import Persistencia.DinamicoRepositorio;
import org.example.agregador.HechosYColecciones.Hecho;
import org.example.agregador.DTO.HechoDTO;
import org.example.agregador.Contribuyente;

public class ControllerSubirHechos {
    private final Contribuyente contribuyente;
    private final DinamicoRepositorio baseDeDatos;

    public ControllerSubirHechos(Contribuyente contribuyente, DinamicoRepositorio baseDeDatos){
        this.contribuyente = contribuyente;
        this.baseDeDatos = baseDeDatos;
    }

    public void subirHecho(HechoDTO hecho){
        baseDeDatos.guardarHecho(hecho);
        this.notificar(hecho);
    }

    public void notificar(HechoDTO hechoDTO){
        contribuyente.hechoSubido(new Hecho(hechoDTO));
    }

}
