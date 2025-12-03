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
import agregador.utils.BDUtils;

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
        this.servicioGeoref = new ServicioGeoref();
    }


    public void iniciarBusquedaAgregador() {
        System.out.println(">>> Iniciando ciclo de búsqueda del Agregador...");
        hechosCargadorService.obtenerHechosNuevos();
        hechosCargadorService.obtenerSolicitudes();
    }

    public void ejecutarAlgoritmoDeConsenso() {
        motorConsenso.ejecutar();
    }

    public void procesarNuevosHechos(List<HechoDTO> hechosDTO) {
        System.out.println("Procesando lote de " + hechosDTO.size() + " hechos.");
        if (hechosDTO.isEmpty()) return;

        List<Hecho> hechosParaColecciones = new ArrayList<>();

        for (HechoDTO dto : hechosDTO) {
            enriquecerUbicacion(dto);
            try {
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
            System.out.println("🆕 Fuente nueva detectada: " + fuenteDto.getDescriptor() + ". Creándola en BD...");

            Fuente nuevaFuente = new Fuente();
            nuevaFuente.setDescriptor(fuenteDto.getDescriptor());

            if (fuenteDto.getTipoDeFuente() != null) {
                try {
                    nuevaFuente.setTipoDeFuente(fuenteDto.getTipoDeFuente());
                } catch (IllegalArgumentException e) {
                    System.err.println("⚠️ Tipo de fuente desconocido: " + fuenteDto.getTipoDeFuente() + ". Se usará ESTATICA por defecto.");
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

        for (int i = 1; i <= totalPages; i++) {
            List<Coleccion> paginaColecciones = coleccionRepositorio.obtenerPaginadas(i, pageSize);

            List<Coleccion> modificadas = new ArrayList<>();

            for (Coleccion c : paginaColecciones) {
                boolean cambio = false;
                for (Hecho h : nuevosHechos) {
                    if (c.cumpleCriterio(h)) {
                        c.agregarHecho(h);
                        cambio = true;
                    }
                }
                if (cambio) modificadas.add(c);
            }

            coleccionRepositorio.actualizarTodas(modificadas);

            BDUtils.getEntityManager().clear();
        }
    }

    private void enriquecerUbicacion(HechoDTO hecho) {
        UbicacionDTO ubicacion = hecho.getUbicacion();

        if (ubicacion == null) return;

        boolean tieneCoordenadas = ubicacion.getLatitud() != 0 && ubicacion.getLongitud() != 0;
        boolean faltaDescripcion = ubicacion.getDescripcion() == null || ubicacion.getDescripcion().trim().isEmpty();

        if (tieneCoordenadas && faltaDescripcion) {
            System.out.println("📍 Buscando descripción para coord: " + ubicacion.getLatitud() + ", " + ubicacion.getLongitud());

            String descripcionEncontrada = servicioGeoref.obtenerDescripcionPorCoordenadas(
                    ubicacion.getLatitud(),
                    ubicacion.getLongitud()
            );

            if (descripcionEncontrada != null) {
                ubicacion.setDescripcion(descripcionEncontrada);
                System.out.println("✅ Ubicación actualizada: " + descripcionEncontrada);
            }
        }
    }
}