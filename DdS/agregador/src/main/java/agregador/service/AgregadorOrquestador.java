package agregador.service;

import agregador.domain.HechosYColecciones.Coleccion;
import agregador.domain.HechosYColecciones.Hecho;
import agregador.domain.fuente.Fuente;
import agregador.dto.Hechos.FuenteDTO;
import agregador.dto.Hechos.HechoDTO;
import agregador.dto.Hechos.UbicacionDTO;
import agregador.dto.Solicitudes.SolicitudDeEliminacionDTO;
import agregador.dto.Solicitudes.SolicitudDeModificacionDTO;
import agregador.repository.ColeccionRepositorio;
import agregador.repository.FuenteRepositorio;
import agregador.repository.HechoRepositorio;
import agregador.service.normalizacion.GeolocalizadorOffline;
import agregador.service.normalizacion.ServicioGeoref;
import agregador.service.normalizacion.ServicioNormalizacion;
import agregador.utils.BDUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class AgregadorOrquestador {

    private final HechoRepositorio hechoRepositorio;
    private final ColeccionRepositorio coleccionRepositorio;
    private final FuenteRepositorio fuenteRepositorio;

    private final ServicioNormalizacion servicioNormalizacion;
    private final MotorConsenso motorConsenso;
    private final GestorSolicitudes gestorSolicitudes;
    private final HechosCargadorService hechosCargadorService;
    private final ServicioGeoref servicioGeoref;

    public AgregadorOrquestador(
            HechoRepositorio hechoRepositorio,
            ColeccionRepositorio coleccionRepositorio,
            FuenteRepositorio fuenteRepositorio,
            ServicioNormalizacion servicioNormalizacion,
            MotorConsenso motorConsenso,
            GestorSolicitudes gestorSolicitudes,
            HechosCargadorService hechosCargadorService
    ) {
        this.hechoRepositorio = hechoRepositorio;
        this.coleccionRepositorio = coleccionRepositorio;
        this.fuenteRepositorio = fuenteRepositorio;
        this.servicioNormalizacion = servicioNormalizacion;
        this.motorConsenso = motorConsenso;
        this.gestorSolicitudes = gestorSolicitudes;
        this.hechosCargadorService = hechosCargadorService;
        this.servicioGeoref = new ServicioGeoref(null,null);
    }


    public void iniciarBusquedaAgregador() {
        System.out.println(">>> Iniciando ciclo de búsqueda del Agregador...");
        hechosCargadorService.obtenerSolicitudes();
        hechosCargadorService.obtenerHechosNuevos();
    }

    public void ejecutarAlgoritmoDeConsenso() {
        motorConsenso.ejecutar();
    }

    @Transactional
    public void procesarNuevosHechos(List<HechoDTO> hechosDTO) {
        System.out.println("Procesando lote de " + hechosDTO.size() + " hechos.");
        if (hechosDTO.isEmpty()) return;

        List<Hecho> hechosParaColecciones = new ArrayList<>();

        for (HechoDTO dto : hechosDTO) {
            enriquecerUbicacion(dto);
            try {
                System.out.println("Procesando DTO: " + new ObjectMapper().writeValueAsString(dto));
                gestionarFuente(dto);

                Hecho hechoNormalizado = servicioNormalizacion.normalizar(dto);

                hechoRepositorio.guardar(hechoNormalizado);
                hechosParaColecciones.add(hechoNormalizado);

            } catch (Exception e) {
                System.err.println("Error procesando hecho: " + e.getMessage());
                e.printStackTrace();
            }
        }

        actualizarColeccionesConNuevosHechos(hechosParaColecciones);
    }

    public void procesarSolicitudes(List<SolicitudDeModificacionDTO> mods, List<SolicitudDeEliminacionDTO> elims) {
        gestorSolicitudes.procesarSolicitudes(mods, elims);
    }

    private void gestionarFuente(HechoDTO dto) throws Exception {
        FuenteDTO fuenteDto = dto.getFuente();
        if (fuenteDto == null) return;

        Fuente fuentePersistida = fuenteRepositorio.buscarPorDescriptor(fuenteDto.getDescriptor());

        if (fuentePersistida == null) {
            System.out.println("Fuente nueva detectada: " + fuenteDto.getDescriptor() + ". Creándola en BD...");

            Fuente nuevaFuente = new Fuente();
            nuevaFuente.setDescriptor(fuenteDto.getDescriptor());

            if (fuenteDto.getTipoDeFuente() != null) {
                try {
                    nuevaFuente.setTipoDeFuente(fuenteDto.getTipoDeFuente());
                } catch (IllegalArgumentException e) {
                    System.err.println("Tipo de fuente desconocido: " + fuenteDto.getTipoDeFuente() + ". Se usará ESTATICA por defecto.");
                }
            }
            fuentePersistida = fuenteRepositorio.guardar(nuevaFuente);
        }

        if (fuentePersistida != null) {
            fuenteDto.setFuenteId(fuentePersistida.getId());
        }
    }

    public void actualizarColeccionesConNuevosHechos(List<Hecho> nuevosHechos) {
        int pageSize = 100;
        long totalColecciones = coleccionRepositorio.contarTodas();
        int totalPages = (int) Math.ceil(totalColecciones / (double) pageSize);

        List<Hecho> hechosProcesables = new ArrayList<>(nuevosHechos);

        for (int i = 1; i <= totalPages; i++) {
            List<Coleccion> paginaColecciones = coleccionRepositorio.obtenerPaginadas(i, pageSize);
            List<Coleccion> modificadas = new ArrayList<>();

            for (Coleccion c : paginaColecciones) {
                boolean cambio = false;
                for (Hecho h : hechosProcesables) {
                    if (c.cumpleCriterio(h)) {
                        if (!c.getHechos().contains(h)) {
                            c.agregarHecho(h);
                            cambio = true;
                        }
                    }
                }
                if (cambio) modificadas.add(c);
            }

            coleccionRepositorio.actualizarLoteYLimpiar(modificadas);

            if (i < totalPages) {
                hechosProcesables = hechoRepositorio.recargarLista(hechosProcesables);
            }
        }
    }

    private void enriquecerUbicacion(HechoDTO hecho) {
        UbicacionDTO ubicacion = hecho.getUbicacion();

        if (ubicacion == null) return;

        boolean tieneCoordenadas = ubicacion.getLatitud() != 0 && ubicacion.getLongitud() != 0;
        boolean faltaDescripcion = ubicacion.getDescripcion() == null || ubicacion.getDescripcion().trim().isEmpty();

        if (tieneCoordenadas && faltaDescripcion) {
            System.out.println("Buscando descripción para coord: " + ubicacion.getLatitud() + ", " + ubicacion.getLongitud());

            String descripcionEncontrada = servicioGeoref.obtenerDescripcionPorCoordenadas(
                    ubicacion.getLatitud(),
                    ubicacion.getLongitud()
            );

            if (descripcionEncontrada != null) {
                ubicacion.setDescripcion(descripcionEncontrada);
                System.out.println("Ubicación actualizada (API): " + descripcionEncontrada);
            } else {
                System.out.println("API falló, calculando ubicación aproximada offline...");

                String descripcionOffline = GeolocalizadorOffline.obtenerUbicacionAproximada(
                        ubicacion.getLatitud(),
                        ubicacion.getLongitud()
                );

                ubicacion.setDescripcion(descripcionOffline);
                System.out.println("Ubicación actualizada (Offline): " + descripcionOffline);
            }
        }
    }
}