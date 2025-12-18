package gestorAdministrativo.service;

import gestorAdministrativo.domain.HechosYColecciones.Etiqueta;
import gestorAdministrativo.domain.HechosYColecciones.Hecho;
import gestorAdministrativo.repository.HechoRepositorio;

import java.util.UUID;

public class HechoService {

    private HechoRepositorio hechoRepositorio;

    public HechoService(HechoRepositorio hechoRepositorio) {
        this.hechoRepositorio = hechoRepositorio;
    }

    public void agregarEtiquetas(UUID hechoId, String nombre) {
        Hecho hecho = hechoRepositorio.buscarPorId(hechoId);
        if (hecho == null) {
            throw new RuntimeException("El hecho con ID " + hechoId + " no existe.");
        }

        String nombreLimpio = nombre.toLowerCase();

        Etiqueta etiqueta = hechoRepositorio.buscarEtiquetaPorNombre(nombreLimpio);

        if (etiqueta == null) {
            etiqueta = new Etiqueta();
            etiqueta.setNombre(nombreLimpio);
        }

        if (!hecho.getEtiquetas().contains(etiqueta)) {
            hecho.getEtiquetas().add(etiqueta);

            hechoRepositorio.guardar(hecho);
        }

    }
}
