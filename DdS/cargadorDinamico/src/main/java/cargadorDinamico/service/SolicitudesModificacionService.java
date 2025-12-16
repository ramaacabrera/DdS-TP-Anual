package cargadorDinamico.service;

import cargadorDinamico.domain.DinamicaDto.SolicitudDeModificacionDTO;
// IMPORTANTE: Importar la nueva clase
import cargadorDinamico.domain.DinamicaDto.SolicitudModificacionSalienteDTO;
import cargadorDinamico.domain.HechosYColeccionesD.HechoModificado_D;
import cargadorDinamico.domain.HechosYColeccionesD.Ubicacion_D;
import cargadorDinamico.domain.HechosYColeccionesD.ContenidoMultimedia_D;
import cargadorDinamico.domain.HechosYColecciones.*; // Entidades finales (Hecho, Ubicacion, Multimedia)
import cargadorDinamico.domain.Solicitudes.SolicitudDeModificacion_D;
import cargadorDinamico.repository.DinamicoRepositorio;
import cargadorDinamico.repository.HechoRepositorio;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SolicitudesModificacionService {
    private final DinamicoRepositorio repositorio;
    private final HechoRepositorio hechoRepositorio;

    public SolicitudesModificacionService(DinamicoRepositorio repositorio, HechoRepositorio hechoRepositorio){
        this.repositorio = repositorio;
        this.hechoRepositorio = hechoRepositorio;
    }

    public List<SolicitudModificacionSalienteDTO> obtenerSolicitudes() {

        List<SolicitudDeModificacion_D> entidades = repositorio.buscarSolModificacionEntidades();

        List<SolicitudModificacionSalienteDTO> dtosSalida = new ArrayList<>();

        for (SolicitudDeModificacion_D entidad : entidades) {
            dtosSalida.add(new SolicitudModificacionSalienteDTO(entidad));
        }

        return dtosSalida;
    }

    public void aplicarModificacion(SolicitudDeModificacion_D sol){

        UUID id = sol.getID_HechoAsociado();
        Hecho original = hechoRepositorio.buscarPorId(id);

        if (original == null) {
            System.err.println("Error: Intentando aplicar modificación a un hecho que no existe ID: " + id);
            return;
        }

        HechoModificado_D propuesta = sol.getHechoModificado();

        if (propuesta == null) return;

        if(propuesta.getTitulo() != null) original.setTitulo(propuesta.getTitulo());
        if(propuesta.getDescripcion() != null) original.setDescripcion(propuesta.getDescripcion());
        if(propuesta.getCategoria() != null) original.setCategoria(propuesta.getCategoria());

        if(propuesta.getFechaDeAcontecimiento() != null) {
            original.setFechaDeAcontecimiento(propuesta.getFechaDeAcontecimiento());
        }

        if (propuesta.getUbicacion() != null) {
            Ubicacion_D ubD = propuesta.getUbicacion();
            Ubicacion nuevaUbicacion = new Ubicacion(
                    ubD.getLatitud(),
                    ubD.getLongitud(),
                    ubD.getDescripcion()
            );
            original.setUbicacion(nuevaUbicacion);
        }
        if (propuesta.getContenidoMultimedia() != null) {
            List<ContenidoMultimedia> nuevaLista = new ArrayList<>();

            for (ContenidoMultimedia_D mediaD : propuesta.getContenidoMultimedia()) {
                // Convertir el ENUM de _D al ENUM real
                TipoContenidoMultimedia tipoReal;
                try {
                    tipoReal = TipoContenidoMultimedia.valueOf(mediaD.getTipoContenido().name());
                } catch (Exception e) {
                    tipoReal = TipoContenidoMultimedia.IMAGEN;
                }

                ContenidoMultimedia mediaReal = new ContenidoMultimedia(tipoReal, mediaD.getContenido());
                mediaReal.setHecho(original);
                nuevaLista.add(mediaReal);
            }

            if (original.getContenidoMultimedia() == null) {
                original.setContenidoMultimedia(new ArrayList<>());
            } else {
                original.getContenidoMultimedia().clear();
            }
            original.getContenidoMultimedia().addAll(nuevaLista);
        }

        hechoRepositorio.actualizar(original);
        System.out.println("Modificación aplicada al hecho: " + original.getTitulo());
    }

    public void guardarSolicitudModificacion(SolicitudDeModificacion_D entidad) {
        repositorio.guardarSolicitudModificacion(entidad);
    }
}