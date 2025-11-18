// gestorAdministrativo/service/ColeccionService.java
package gestorAdministrativo.service;

import DominioGestorAdministrativo.DTO.Coleccion.ColeccionDTO;
import gestorAdministrativo.repository.ColeccionRepositorio;
import gestorAdministrativo.repository.HechoRepositorio;
import utils.Dominio.Criterios.Criterio;
import utils.Dominio.HechosYColecciones.Coleccion;
import utils.Dominio.HechosYColecciones.Hecho;
import utils.Dominio.HechosYColecciones.TipoAlgoritmoConsenso;
import utils.Dominio.fuente.Fuente;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ColeccionService {
    private final ColeccionRepositorio coleccionRepositorio;
    private final HechoRepositorio hechoRepositorio;

    public ColeccionService(ColeccionRepositorio coleccionRepositorio, HechoRepositorio hechoRepositorio) {
        this.coleccionRepositorio = coleccionRepositorio;
        this.hechoRepositorio = hechoRepositorio;
    }

    public ColeccionDTO crearColeccion(ColeccionDTO coleccionDTO) {
        System.out.println("Iniciando creación de colección");

        Coleccion coleccion = new Coleccion(coleccionDTO);

        // Filtrar hechos que cumplen los criterios
        List<Criterio> criterios = coleccion.getCriteriosDePertenencia();
        List<Hecho> hechos = hechoRepositorio.getHechos();
        List<Hecho> hechosQueCumplen = hechos.stream()
                .filter(hecho -> criterios.stream().allMatch(criterio -> criterio.cumpleConCriterio(hecho)))
                .collect(Collectors.toList());

        coleccion.setHechos(hechosQueCumplen);
        coleccionRepositorio.guardar(coleccion);

        System.out.println("Colección guardada: " + coleccionDTO.getTitulo());
        return convertirADTO(coleccion);
    }

    public ColeccionDTO actualizarColeccion(UUID id, ColeccionDTO coleccionDTO) {
        Coleccion coleccionExistente = coleccionRepositorio.buscarPorHandle(id.toString());

        if (coleccionExistente == null) {
            throw new IllegalArgumentException("Colección no encontrada");
        }

        // Actualizar campos básicos
        if (coleccionDTO.getTitulo() != null) {
            coleccionExistente.setTitulo(coleccionDTO.getTitulo());
        }
        if (coleccionDTO.getDescripcion() != null) {
            coleccionExistente.setDescripcion(coleccionDTO.getDescripcion());
        }
        if (coleccionDTO.getCriteriosDePertenencia() != null) {
            coleccionExistente.setCriteriosDePertenencia(coleccionDTO.getCriteriosDePertenencia());
        }

        coleccionRepositorio.actualizar(coleccionExistente);
        return convertirADTO(coleccionExistente);
    }

    public void eliminarColeccion(UUID id) {
        Coleccion coleccion = coleccionRepositorio.buscarPorHandle(id.toString());

        if (coleccion == null) {
            throw new IllegalArgumentException("Colección no encontrada");
        }

        coleccionRepositorio.eliminar(coleccion);
    }

    public ColeccionDTO agregarFuente(UUID coleccionId, Fuente fuente) {
        Coleccion coleccion = coleccionRepositorio.buscarPorHandle(coleccionId.toString());

        if (coleccion == null) {
            throw new IllegalArgumentException("Colección no encontrada");
        }

        coleccion.agregarFuente(fuente);
        coleccionRepositorio.actualizar(coleccion);

        return convertirADTO(coleccion);
    }

    public ColeccionDTO eliminarFuente(UUID coleccionId, Fuente fuente) {
        Coleccion coleccion = coleccionRepositorio.buscarPorHandle(coleccionId.toString());

        if (coleccion == null) {
            throw new IllegalArgumentException("Colección no encontrada");
        }

        coleccion.eliminarFuente(fuente);
        coleccionRepositorio.actualizar(coleccion);

        return convertirADTO(coleccion);
    }

    public ColeccionDTO actualizarAlgoritmoConsenso(UUID coleccionId, TipoAlgoritmoConsenso algoritmo) {
        Coleccion coleccion = coleccionRepositorio.buscarPorHandle(coleccionId.toString());

        if (coleccion == null) {
            throw new IllegalArgumentException("Colección no encontrada");
        }

        coleccion.setAlgoritmoDeConsenso(algoritmo);
        coleccionRepositorio.actualizar(coleccion);

        return convertirADTO(coleccion);
    }

    public List<ColeccionDTO> obtenerTodasLasColecciones() {
        List<Coleccion> colecciones = coleccionRepositorio.obtenerTodas();
        return colecciones.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public ColeccionDTO obtenerColeccionPorId(UUID id) {
        Coleccion coleccion = coleccionRepositorio.buscarPorHandle(id.toString());
        if (coleccion == null) {
            throw new IllegalArgumentException("Colección no encontrada");
        }
        return convertirADTO(coleccion);
    }

    private ColeccionDTO convertirADTO(Coleccion coleccion) {
        return new ColeccionDTO(
                coleccion.getTitulo(),
                coleccion.getDescripcion(),
                coleccion.getFuente(),
                coleccion.getCriteriosDePertenencia(),
                coleccion.getAlgoritmoDeConsenso()
        );
    }
}