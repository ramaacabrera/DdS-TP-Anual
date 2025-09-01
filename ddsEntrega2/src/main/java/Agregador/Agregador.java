package Agregador;

import Agregador.Normalizador.NormalizadorMock;
import Agregador.Persistencia.ColeccionRepositorio;
import Agregador.Persistencia.HechoRepositorio;
import Agregador.Persistencia.SolicitudEliminacionRepositorio;
import Agregador.Persistencia.SolicitudModificacionRepositorio;
import Agregador.Solicitudes.DetectorDeSpam;
import utils.DTO.HechoDTO;
import Agregador.Criterios.Criterio;
import utils.DTO.SolicitudDeEliminacionDTO;
import Agregador.HechosYColecciones.Coleccion;
import Agregador.HechosYColecciones.Hecho;
import Agregador.Solicitudes.SolicitudDeEliminacion;
import Agregador.fuente.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.time.Duration;
import java.time.LocalDateTime;

public class Agregador {

    private HechoRepositorio hechoRepositorio;
    private ColeccionRepositorio coleccionRepositorio;
    private SolicitudEliminacionRepositorio solicitudEliminacionRepositorio;
    private SolicitudModificacionRepositorio solicitudModificacionRepositorio;
    private NormalizadorMock normalizador;
    ConexionCargador conexionCargador;

    public Agregador(HechoRepositorio hechoRepositorio, ColeccionRepositorio coleccionRepositorio, SolicitudEliminacionRepositorio solicitudEliminacionRepositorio,
                     SolicitudModificacionRepositorio solicitudModificacionRepositorio, NormalizadorMock normalizador, ConexionCargador conexionCargador){
        this.hechoRepositorio = hechoRepositorio;
        this.coleccionRepositorio = coleccionRepositorio;
        //this.fuentesDisponibles = fuentesDisponibles;
        this.solicitudEliminacionRepositorio = solicitudEliminacionRepositorio;
        this.normalizador = normalizador;
        this.conexionCargador = conexionCargador;

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            this.actualizarHechosDesdeFuentes();
        }, 0, 1, TimeUnit.HOURS);

        long delayInicial = calcularDelayHastaHora(2);  // 2 AM
        scheduler.scheduleAtFixedRate(() -> {
            this.ejecutarAlgoritmoDeConsenso();
        }, delayInicial, 24, TimeUnit.HOURS);


    }
    /*
    Constructor (lo hicimos para inicializar) que cuando se crea el objeto Agregador,
    arranca un scheduler que cada 1 hora ejecuta el metodo actualizarHechosDesdeFuentes().
    */



    private Hecho normalizarHecho(Hecho hecho){
        return normalizador.normalizar(hecho);
    }

    public void actualizarHechosDesdeFuentes() {

        List<HechoDTO> hechos = conexionCargador.obtenerHechosNuevos();
        hechos.forEach(h -> {hechoRepositorio.guardar(h);});

        // Traemos hechos nuevos de TODAS las fuentes disponibles
        /*for (Fuente fuente : fuentesDisponibles) {

            List<Hecho> nuevosHechos = obtenerHechosExterno(fuente, new ArrayList<>());
            if (fuente.getTipoDeFuente() == TipoDeFuente.DINAMICA && fuente instanceof FuenteDinamica) {
                agregarSolicitudes((FuenteDinamica) fuente);
            }

            for (Hecho hecho : nuevosHechos) {
                int nuevo = 0;
                Hecho hechoNormalizado = normalizarHecho(hecho);
                // Por cada colección, vemos si el hecho NORMALIZADO cumple criterios
                for (Coleccion coleccion : coleccionRepositorio.obtenerTodas()) {
                    if (coleccion.cumpleCriterio(hechoNormalizado)) {
                        boolean esNuevo = coleccion.agregarHecho(hechoNormalizado);
                        if (esNuevo) {
                            nuevo++;
                        }
                    }
                }
                if (nuevo > 0) {
                    hechoRepositorio.guardar(hechoNormalizado);
                } else {
                    Hecho hechoExistente = buscarHechoSimilar(hechoNormalizado);
                    if (hechoExistente != null) {
                        hechoRepositorio.actualizar(hechoExistente);
                    }

                }
            }

        }
        */
    }


    private Hecho buscarHechoSimilar(Hecho hechoNuevo) {
        for (Hecho h : hechoRepositorio.buscarHechos(null)) {
            if (h.tieneMismosAtributosQue(hechoNuevo)) {
                return h;
            }
        }
        return null;
    }

    private Hecho buscarHechoPorTitulo(String titulo) {
        for (Hecho h : hechoRepositorio.buscarHechos(null)) {
            if (h.getTitulo().equalsIgnoreCase(titulo)) {
                return h;
            }
        }
        return null;
    }

//    public List<Hecho> obtenerHechosExterno(Fuente fuente, List<Criterio> criterios) {
//        List<Hecho> hechos = new ArrayList<>();
//        for (HechoDTO hechoDTO : fuente.obtenerHechos(criterios)) {
//            Hecho hecho = new Hecho(hechoDTO);
//            hechos.add(hecho);
//        }
//        return hechos;
//    }

//    public void agregarSolicitudes(FuenteDinamica fuente) {
//        List<SolicitudDeEliminacion> solicitudesDeEliminacion = new ArrayList<>();
//
//        for (SolicitudDeEliminacionDTO solicitudDeEliminacionDTO : fuente.obtenerSolicitudDeEliminacion()) {
//            SolicitudDeEliminacion nueva = new SolicitudDeEliminacion(solicitudDeEliminacionDTO);
//
//            boolean yaExisteSoli = solicitudEliminacionRepositorio.buscarPorId(nueva.getId()).isPresent();
//
//            boolean soliEsSpam = DetectorDeSpam.esSpam(solicitudDeEliminacionDTO.getJustificacion());
//
//            if (!yaExisteSoli && !soliEsSpam) {
//                solicitudEliminacionRepositorio.add(nueva);
//            }
//
//            if(soliEsSpam) {
//                nueva.rechazarSolicitud();
//            }
//        }
//    }

    public void ejecutarAlgoritmoDeConsenso() {
        for (Coleccion coleccion : coleccionRepositorio.obtenerTodas()) {
            coleccion.ejecutarAlgoritmoDeConsenso();
        }
    }

    private long calcularDelayHastaHora(int horaObjetivo) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime proximaEjecucion = ahora.withHour(horaObjetivo).withMinute(0).withSecond(0).withNano(0);

        if (ahora.compareTo(proximaEjecucion) >= 0) {
            // Si ya pasó la hora objetivo de hoy, ponemos para mañana
            proximaEjecucion = proximaEjecucion.plusDays(1);
        }

        Duration duration = Duration.between(ahora, proximaEjecucion);
        long delayEnHoras = duration.toHours();

        return delayEnHoras;
    }
}