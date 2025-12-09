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

        // 2. Normalizar el nombre (opcional: minúsculas, trim)
        String nombreLimpio = nombre.toLowerCase();

        // 3. Buscar si la etiqueta ya existe en el sistema para reutilizarla
        // (Asumo que tienes un repositorio de etiquetas, si no, lo manejas dentro de HechoRepo)
        Etiqueta etiqueta = hechoRepositorio.buscarEtiquetaPorNombre(nombreLimpio);

        if (etiqueta == null) {
            // Si no existe, creamos una nueva instancia
            etiqueta = new Etiqueta();
            etiqueta.setNombre(nombreLimpio);
            // etiquetaRepositorio.guardar(etiqueta); // Opcional si tienes CascadeType.ALL
        }

        // 4. Validar que el hecho no tenga ya esa etiqueta asignada
        if (!hecho.getEtiquetas().contains(etiqueta)) {
            hecho.getEtiquetas().add(etiqueta);

            // 5. Llamar al repositorio para guardar la relación
            hechoRepositorio.guardar(hecho);
        }

    }
}
