package agregador.PaqueteAgregador;

import agregador.Cargador.ConexionCargador;
import agregador.PaqueteNormalizador.MockNormalizador;
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

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.time.Duration;
import java.time.LocalDateTime;

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

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            //this.actualizarHechosDesdeFuentes();
            //System.out.println("scheduler funciona");
            conexionCargador.obtenerHechosNuevos();
            conexionCargador.obtenerSolicitudes();
            this.actualizarColecciones();
        //}, 0, 1, TimeUnit.HOURS);
        }, 1, 60, TimeUnit.SECONDS);

        long delayInicial = calcularDelayHastaHora(2);  // 2 AM
        scheduler.scheduleAtFixedRate(() -> {
            this.ejecutarAlgoritmoDeConsenso();
        }, delayInicial, 24, TimeUnit.HOURS);


    }
    /*
    Constructor (lo hicimos para inicializar) que cuando se crea el objeto Agregador,
    arranca un scheduler que cada 1 hora ejecuta el metodo actualizarHechosDesdeFuentes().
    */

    private Hecho normalizarHecho(HechoDTO hecho){
        return normalizador.normalizar(new Hecho(hecho));
    }

    public void actualizarColecciones(){
        List<Coleccion> colecciones = coleccionRepositorio.obtenerTodas();
        List<Hecho> hechos = hechoRepositorio.getHechos();
        for(Coleccion coleccion : colecciones){
            for(Hecho hecho : hechos){
                if(coleccion.cumpleCriterio(hecho)){
                    coleccion.agregarHecho(hecho);
                }
            }
        }
    }

    public void actualizarHechosDesdeFuentes(List<HechoDTO> hechos) {
        //List<HechoDTO> hechos = conexionCargador.obtenerHechosNuevos();
        System.out.println("Hechos a procesar: " + hechos.size());
        if(!hechos.isEmpty()) {
            for (HechoDTO hechoDTO : hechos) {
                try {
                    // Obtener la Fuente transitoria del utils.DTO
                    Fuente fuenteTransitoria = hechoDTO.getFuente();

                    // 2. BUSCAR LA FUENTE PERSISTIDA por su ruta
                    // Necesitamos la Fuente del repositorio (gestionada por Hibernate)
                    Fuente fuentePersistida = this.fuenteRepositorio.buscarPorRuta(fuenteTransitoria.getRuta());

                    if (fuentePersistida == null) {
                        // Si la agregador.fuente no se registró (nunca debería pasar si el Loader se conecta), la guardamos
                        System.out.println("Fuente no encontrada en DB. Guardándola: " + fuenteTransitoria.getRuta());

                        fuentePersistida = this.fuenteRepositorio.guardar(fuenteTransitoria);
                    }

                    // 3. ASIGNAR LA FUENTE PERSISTIDA al HechoDTO (Sustituir la transitoria)
                    hechoDTO.setFuente(fuentePersistida);

                    // 4. Normalizar y Guardar
                    Hecho hechoNormalizado = this.normalizarHecho(hechoDTO);
                    hechoRepositorio.guardar(hechoNormalizado);

                } catch (Exception e) {
                    // Manejo de errores de un hecho individual (ej: datos inválidos o fallos de DB)
                    System.err.println("ERROR al procesar un hecho. Saltando el hecho. Causa: " + e.getMessage());
                    e.printStackTrace();
                }

            }
        }
    }

    /*private Hecho buscarHechoSimilar(Hecho hechoNuevo) {
        List<Hecho> hechosSimilares = hechoRepositorio.buscarSimilares(hechoNuevo.getTitulo());
        for (Hecho h : hechosSimilares) {
            if (h.tieneMismosAtributosQue(hechoNuevo)) {
                return h;
            }
        }
        return null;
    }*/
    private Hecho buscarHechoSimilar(Hecho hechoNuevo) {
        for (Hecho h : hechoRepositorio.buscarHechos(null)) {
            if (h.tieneMismosAtributosQue(hechoNuevo)) {
                return h;
            }
        }
        return null;
    }

    public void agregarSolicitudes(List<SolicitudDeModificacionDTO> solicitudesDeModificacion, List<SolicitudDeEliminacionDTO> solicitudesDeEliminacion) {
        //List<SolicitudDeModificacionDTO> solicitudesDeModificacion = conexionCargador.obtenerSolicitudes();
        //List<SolicitudDeEliminacionDTO>  solicitudesDeEliminacion = conexionCargador.obtenerSolicitudesEliminacion();


        for (SolicitudDeEliminacionDTO dto : solicitudesDeEliminacion) {
            boolean esSpam = detectorDeSpam.esSpam(dto.getJustificacion());

            if (esSpam){ // !yaExiste &&) {
                SolicitudDeEliminacion solicitud = new SolicitudDeEliminacion(dto);
                solicitud.rechazarSolicitud();
                solicitud.setEsSpam(true);
                solicitudEliminacionRepositorio.agregarSolicitudDeEliminacion(solicitud);
            } else {
                solicitudEliminacionRepositorio.agregarSolicitudDeEliminacion(dto);
            }

        }
        for (SolicitudDeModificacionDTO dto : solicitudesDeModificacion) {
            boolean esSpam = detectorDeSpam.esSpam(dto.getJustificacion());

            if (esSpam){
                SolicitudDeModificacion solicitud = new SolicitudDeModificacion(dto);
                solicitud.rechazarSolicitud();
                solicitud.setEsSpam(true);
                solicitudModificacionRepositorio.agregarSolicitudDeModificacion(solicitud);
            } else {
                solicitudModificacionRepositorio.agregarSolicitudDeModificacion(dto);
            }

        }
    }


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