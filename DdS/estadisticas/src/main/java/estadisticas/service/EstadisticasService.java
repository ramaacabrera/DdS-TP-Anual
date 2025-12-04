package estadisticas.service;

import estadisticas.domain.Estadisticas;
import estadisticas.domain.EstadisticasCategoria;
import estadisticas.domain.EstadisticasColeccion;
import estadisticas.repository.EstadisticasCategoriaRepositorio;
import estadisticas.repository.EstadisticasColeccionRepositorio;
import estadisticas.repository.EstadisticasRepositorio;
import estadisticas.service.normalizador.NormalizadorCategorias;
import estadisticas.utils.EstadisticasCSVTransformer;

import java.util.*;
import java.util.stream.Collectors;

public class EstadisticasService {

    private final EstadisticasRepositorio estadisticasRepo;
    private final EstadisticasCategoriaRepositorio categoriasRepo;
    private final EstadisticasColeccionRepositorio coleccionesRepo;

    public EstadisticasService(EstadisticasRepositorio estadisticasRepo,
                               EstadisticasCategoriaRepositorio categoriasRepo,
                               EstadisticasColeccionRepositorio coleccionesRepo) {
        this.estadisticasRepo = estadisticasRepo;
        this.categoriasRepo = categoriasRepo;
        this.coleccionesRepo = coleccionesRepo;
    }

    public Optional<String> obtenerCategoriaMaxHechos() {
        return estadisticasRepo.buscarCategoria_max_hechos();
    }

    public Optional<Long> obtenerSolicitudesSpam() {
        return estadisticasRepo.buscarSpam();
    }

    public List<String> obtenerCategoriasNormalizadas() {
        List<String> categorias = categoriasRepo.obtenerTodasLasCategorias();
        if (categorias == null) return Collections.emptyList();

        return categorias.stream()
                .map(NormalizadorCategorias::normalizar)
                .collect(Collectors.toList());
    }

    public Optional<Integer> obtenerHoraMaxPorCategoria(String categoriaRaw) {
        String categoria = NormalizadorCategorias.normalizar(categoriaRaw);
        return categoriasRepo.buscarHoraCategoria(categoria.trim());
    }

    public Optional<String> obtenerProvinciaPorCategoria(String categoriaRaw) {
        String categoria = NormalizadorCategorias.normalizar(categoriaRaw);
        return categoriasRepo.buscarProvinciaCategoria(categoria.trim());
    }

    public Map<String, String> obtenerProvinciaPorColeccion(String coleccionUuidStr) throws IllegalArgumentException {
        UUID coleccionUuid = UUID.fromString(coleccionUuidStr); // Puede lanzar IllegalArgumentException
        return coleccionesRepo.buscarProvinciaYNombreColeccion(coleccionUuid).orElse(null);
    }

    public Optional<String> generarReporteCSV() {
        Optional<Estadisticas> ultimaEstadisticaOpt = estadisticasRepo.obtenerUltimaEstadistica();

        if (ultimaEstadisticaOpt.isEmpty()) {
            return Optional.empty();
        }

        Estadisticas estadistica = ultimaEstadisticaOpt.get();
        List<EstadisticasCategoria> listaCategorias = categoriasRepo.buscarPorEstadisticaPadre(estadistica);
        List<EstadisticasColeccion> listaColecciones = coleccionesRepo.buscarPorEstadisticaPadre(estadistica);

        return Optional.of(EstadisticasCSVTransformer.transformarAFormatoCSV(estadistica, listaCategorias, listaColecciones));
    }
}