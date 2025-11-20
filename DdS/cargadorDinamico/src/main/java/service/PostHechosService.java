package service;

import controller.ControladorDinamica;
import domain.DinamicaDto.Hecho_D_DTO;
import domain.HechosYColeccionesD.Hecho_D;
import repository.DinamicoRepositorio;

public class PostHechosService {
    
    private final DinamicoRepositorio repositorio;

    public PostHechosService(DinamicoRepositorio repositorio) { this.repositorio = repositorio;}

    public void guardarHecho(Hecho_D_DTO nueva) {
        try {
            repositorio.guardarHecho(nueva);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
