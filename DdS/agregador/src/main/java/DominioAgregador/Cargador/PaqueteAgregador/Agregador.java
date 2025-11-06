package DominioAgregador.Cargador.PaqueteAgregador;

import DominioAgregador.Cargador.ConexionCargador;
import utils.PaqueteNormalizador.MockNormalizador;
import utils.Persistencia.*;
import utils.Dominio.fuente.Fuente;
import utils.Persistencia.ColeccionRepositorio;
import utils.Persistencia.HechoRepositorio;
import utils.Persistencia.SolicitudEliminacionRepositorio;
import utils.Persistencia.SolicitudModificacionRepositorio;
import utils.Dominio.Solicitudes.SolicitudDeEliminacion;
import utils.Dominio.Solicitudes.SolicitudDeModificacion;
import utils.DTO.*;
import utils.Dominio.HechosYColecciones.Coleccion;
import utils.Dominio.HechosYColecciones.Hecho;

import java.util.ArrayList;
import java.util.List;

public class Agregador {

    private final HechoRepositorio hechoRepositorio;
    private final ColeccionRepositorio coleccionRepositorio;
    private final SolicitudEliminacionRepositorio solicitudEliminacionRepositorio;
    private final SolicitudModificacionRepositorio solicitudModificacionRepositorio;
    private final FuenteRepositorio fuenteRepositorio;
    private final MockNormalizador normalizador;
    private final DetectorDeSpam detectorDeSpam =  new DetectorDeSpam();
    private final ConexionCargador conexionCargador;

    public Agregador(HechoRepositorio hechoRepositorio, ColeccionRepositorio coleccionRepositorio, SolicitudEliminacionRepositorio solicitudEliminacionRepositorio,
                     SolicitudModificacionRepositorio solicitudModificacionRepositorio, FuenteRepositorio fuenteRepositorio, MockNormalizador normalizador, ConexionCargador conexionCargador){
        this.hechoRepositorio = hechoRepositorio;
        this.coleccionRepositorio = coleccionRepositorio;
        this.solicitudEliminacionRepositorio = solicitudEliminacionRepositorio;
        this.fuenteRepositorio = fuenteRepositorio;
        this.normalizador = normalizador;
        this.conexionCargador = conexionCargador;
        this.solicitudModificacionRepositorio = solicitudModificacionRepositorio;
    }

    public void iniciarBusquedaAgregador(){
        conexionCargador.obtenerHechosNuevos();
        conexionCargador.obtenerSolicitudes();
    }

    private Hecho normalizarHecho(HechoDTO hecho){
        return normalizador.normalizar(new Hecho(hecho));
    }

    public void actualizarColecciones(List<Hecho> hechos){
        System.out.println("-----------------------Actualizando Colecciones------------------------ ");

        List<Coleccion> colecciones = coleccionRepositorio.obtenerTodas();
        for(Coleccion coleccion : colecciones){
            boolean hayQueActualizar = false;
            for(Hecho hecho : hechos){
                if(coleccion.cumpleCriterio(hecho)){
                    coleccion.agregarHecho(hecho);
                    hayQueActualizar = true;
                }
            }
            if(!hayQueActualizar){
                coleccionRepositorio.actualizar(coleccion);
            }
        }
    }

    public void actualizarHechosDesdeFuentes(List<HechoDTO> hechos) {
        System.out.println("Hechos a procesar: " + hechos.size());
        if(!hechos.isEmpty()) {
            List<Hecho> hechosNormalizados = new ArrayList<>();
            for (HechoDTO hechoDTO : hechos) {
                try {
                    Fuente fuenteTransitoria = hechoDTO.getFuente();

                    Fuente fuentePersistida = this.fuenteRepositorio.buscarPorRuta(fuenteTransitoria.getDescriptor());

                    if (fuentePersistida == null) {
                        System.out.println("Fuente no encontrada en DB. Guardándola: " + fuenteTransitoria.getDescriptor());

                        fuentePersistida = this.fuenteRepositorio.guardar(fuenteTransitoria);
                    }

                    hechoDTO.setFuente(fuentePersistida);

                    Hecho hechoNormalizado = this.normalizarHecho(hechoDTO);
                    hechoRepositorio.guardar(hechoNormalizado);
                    hechosNormalizados.add(hechoNormalizado);
                } catch (Exception e) {
                    System.err.println("ERROR al procesar un hecho. Saltando el hecho. Causa: " + e.getMessage());
                    e.printStackTrace();
                }

            }
            this.actualizarColecciones(hechosNormalizados);
        }
    }

    private Hecho buscarHechoSimilar(Hecho hechoNuevo) {
        for (Hecho h : hechoRepositorio.buscarHechos(null)) {
            if (h.tieneMismosAtributosQue(hechoNuevo)) {
                return h;
            }
        }
        return null;
    }

    public void agregarSolicitudes(List<SolicitudDeModificacionDTO> solicitudesDeModificacion, List<SolicitudDeEliminacionDTO> solicitudesDeEliminacion) {
        solicitudesDeEliminacion.forEach(System.out::println);
        for (SolicitudDeEliminacionDTO dto : solicitudesDeEliminacion) {
            boolean esSpam = detectorDeSpam.esSpam(dto.getJustificacion());
            SolicitudDeEliminacion solicitud = new SolicitudDeEliminacion(dto, hechoRepositorio);
            if (esSpam){
                solicitud.rechazarSolicitud();
                solicitud.setEsSpam(true);
              }
                solicitudEliminacionRepositorio.agregarSolicitudDeEliminacion(solicitud);
        }
        for (SolicitudDeModificacionDTO dto : solicitudesDeModificacion) {
            boolean esSpam = detectorDeSpam.esSpam(dto.getJustificacion());
            SolicitudDeModificacion solicitud = new SolicitudDeModificacion(dto, hechoRepositorio);
            if (esSpam) {
                solicitud.rechazarSolicitud();
                solicitud.setEsSpam(true);
            }
            solicitudModificacionRepositorio.agregarSolicitudDeModificacion(solicitud);
        }
    }


    public void ejecutarAlgoritmoDeConsenso() {
        for (Coleccion coleccion : coleccionRepositorio.obtenerTodas()) {
            boolean hayQueActualizar = coleccion.ejecutarAlgoritmoDeConsenso();
            if(hayQueActualizar){
                coleccionRepositorio.actualizar(coleccion);
            }
        }
    }
}