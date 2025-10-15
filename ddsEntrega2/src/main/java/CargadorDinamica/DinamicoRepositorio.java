package CargadorDinamica;

import Agregador.HechosYColecciones.ContenidoMultimedia;
import Agregador.HechosYColecciones.EstadoHecho;
import Agregador.HechosYColecciones.Etiqueta;
import Agregador.HechosYColecciones.Ubicacion;
import Agregador.Usuario.RolUsuario;
import Agregador.Usuario.Usuario;
import Agregador.fuente.Fuente;
import Agregador.fuente.TipoDeFuente;
import CargadorDinamica.DinamicaDto.Hecho_D_DTO;
import CargadorDinamica.Dominio.HechosYColecciones.*;

import CargadorDinamica.Dominio.Solicitudes.SolicitudDeEliminacion_D;
import CargadorDinamica.Dominio.Solicitudes.SolicitudDeModificacion_D;
import CargadorDinamica.Dominio.Usuario.Usuario_D;
import utils.DTO.HechoDTO;
import utils.DTO.SolicitudDeModificacionDTO;
import utils.DTO.SolicitudDeEliminacionDTO;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class DinamicoRepositorio {
    private final EntityManager em;

    public DinamicoRepositorio(EntityManager emNuevo) {
        this.em = emNuevo;
    }

    public void guardarHecho(Hecho_D hecho) {
        try {
            BDUtilsDinamico.comenzarTransaccion(em);
            em.persist(hecho); // Usamos persist para insertar nuevo
            BDUtilsDinamico.commit(em);
            System.out.println("Hecho_D guardado: " + hecho.getTitulo());
        } catch (Exception e) {
            BDUtilsDinamico.rollback(em);
            System.err.println("ERROR al guardar Hecho_D: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Hecho_D> buscarHechosEntidades() {
        try {
            TypedQuery<Hecho_D> query = em.createQuery("SELECT h FROM Hecho_D h", Hecho_D.class);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error al obtener hechos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<HechoDTO> buscarHechos() {
        List<Hecho_D> entidades = this.buscarHechosEntidades();
        List<HechoDTO> dtos = new ArrayList<>();

        for (Hecho_D entidad : entidades) {
            HechoDTO dto = convertirEntidadADTO(entidad);
            dtos.add(dto);
        }
        return dtos;
    }

    private HechoDTO convertirEntidadADTO(Hecho_D entidad) {
        return new HechoDTO(
                entidad.getTitulo(),
                entidad.getDescripcion(),
                entidad.getCategoria(),
                convertirUbicacion(entidad.getUbicacion()),
                entidad.getFechaDeAcontecimiento(),
                entidad.getFechaDeCarga(),
                crearFuenteDinamica(),
                convertirEstado(entidad.getEstadoHecho()),
                convertirUsuario(entidad.getContribuyente()),
                convertirEtiquetas(entidad.getEtiquetas()),
                entidad.getEsEditable(),
                convertirMultimedia(entidad.getContenidoMultimedia())
        );
    }

    private Ubicacion convertirUbicacion(Ubicacion_D ubicacionD) {
        if (ubicacionD == null) return null;

        // Si tiene latitud y longitud válidas, usar constructor con coordenadas
        if (ubicacionD.getLatitud() != -999.0 && ubicacionD.getLongitud() != -999.0) {
            return new Ubicacion(
                    ubicacionD.getLatitud(),
                    ubicacionD.getLongitud(),
                    ubicacionD.getDescripcion()
            );
        }
        // Si solo tiene descripción, usar constructor con String
        else if (ubicacionD.getDescripcion() != null) {
            return new Ubicacion(ubicacionD.getDescripcion());
        }
        // Si no tiene nada, ubicación desconocida
        else {
            return new Ubicacion();
        }
    }

    private EstadoHecho convertirEstado(EstadoHecho_D estadoD) {
        return EstadoHecho.valueOf(estadoD.name());
    }

    private Fuente crearFuenteDinamica() {
        return new Fuente(TipoDeFuente.DINAMICA, "http://localhost:puerto-dinamico");
    }

    private Usuario convertirUsuario(Usuario_D usuarioD) {
        if (usuarioD == null) return null;

        Usuario usuario = new Usuario();
        usuario.setEdad(usuarioD.getEdad());
        usuario.setNombre(usuarioD.getNombre());
        usuario.setApellido(usuarioD.getApellido());
        usuario.setRol(RolUsuario.CONTRIBUYENTE);

        return usuario;
    }

    private List<Etiqueta> convertirEtiquetas(List<Etiqueta_D> etiquetasD) {
        if (etiquetasD == null) return new ArrayList<>();

        List<Etiqueta> etiquetas = new ArrayList<>();
        for (Etiqueta_D etiquetaD : etiquetasD) {
            Etiqueta etiqueta = convertirEtiqueta(etiquetaD);
            if (etiqueta != null) {
                etiquetas.add(etiqueta);
            }
        }
        return etiquetas;
    }

    private Etiqueta convertirEtiqueta(Etiqueta_D etiquetaD) {
        if (etiquetaD == null) return null;

        Etiqueta etiqueta = new Etiqueta();
        etiqueta.setNombre(etiquetaD.getNombre());

        return etiqueta;
    }

    private List<ContenidoMultimedia> convertirMultimedia(List<ContenidoMultimedia_D> multimediaD) {
        if (multimediaD == null) return new ArrayList<>();

        List<ContenidoMultimedia> multimedia = new ArrayList<>();
        for (ContenidoMultimedia_D contenidoD : multimediaD) {
            ContenidoMultimedia contenido = convertirContenidoMultimedia(contenidoD);
            if (contenido != null) {
                multimedia.add(contenido);
            }
        }
        return multimedia;
    }

    private ContenidoMultimedia convertirContenidoMultimedia(ContenidoMultimedia_D contenidoD) {
        if (contenidoD == null) return null;

        ContenidoMultimedia contenido = new ContenidoMultimedia(
                contenidoD.getTipoContenido(), // Asumiendo que tienen el mismo enum
                contenidoD.getContenido()
        );

        return contenido;
    }


    public void resetearHechos() {
        try {
            BDUtilsDinamico.comenzarTransaccion(em);
            em.createQuery("DELETE FROM Hecho_D").executeUpdate();
            BDUtilsDinamico.commit(em);
            System.out.println("Todos los hechos han sido reseteados");
        } catch (Exception e) {
            BDUtilsDinamico.rollback(em);
            System.err.println("ERROR al resetear hechos: " + e.getMessage());
        }
    }

    //-------------------------SOLICITUDES---------------------------------------

    public void guardarSolicitudModificacion(SolicitudDeModificacion_D solicitud){
        try {
            BDUtilsDinamico.comenzarTransaccion(em);
            em.persist(solicitud); // Usamos persist para insertar nuevo
            BDUtilsDinamico.commit(em);
            System.out.println("SolicitudDeModificacion_D guardado: " + solicitud.getJustificacion());
        } catch (Exception e) {
            BDUtilsDinamico.rollback(em);
            System.err.println("ERROR al guardar SolicitudDeModificacion_D: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void guardarSolicitudEliminacion(SolicitudDeEliminacion_D solicitud) {
        try {
            BDUtilsDinamico.comenzarTransaccion(em);
            em.persist(solicitud); // Usamos persist para insertar nuevo
            BDUtilsDinamico.commit(em);
            System.out.println("SolicitudDeEliminacion_D guardado: " + solicitud.getJustificacion());
        } catch (Exception e) {
            BDUtilsDinamico.rollback(em);
            System.err.println("ERROR al guardar SolicitudDeEliminacion_D: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void resetearSolicitudesModificacion() {
        try {
            BDUtilsDinamico.comenzarTransaccion(em);
            em.createQuery("DELETE FROM SolicitudDeModificacion_D").executeUpdate();
            BDUtilsDinamico.commit(em);
            System.out.println("Todos las Solicitudes De Modificacion han sido reseteados");
        } catch (Exception e) {
            BDUtilsDinamico.rollback(em);
            System.err.println("ERROR al resetear Solicitudes De Modificacion: " + e.getMessage());
        }
    }

    public void resetearSolicitudesEliminacion() {
        try {
            BDUtilsDinamico.comenzarTransaccion(em);
            em.createQuery("DELETE FROM SolicitudDeEliminacion_D").executeUpdate();
            BDUtilsDinamico.commit(em);
            System.out.println("Todos las Solicitudes De Eliminacion han sido reseteados");
        } catch (Exception e) {
            BDUtilsDinamico.rollback(em);
            System.err.println("ERROR al resetear Solicitudes De Eliminacion: " + e.getMessage());
        }
    }

    public List<SolicitudDeModificacionDTO> buscarSolicitudesModificacion(){return solicitudesModificacion;}
    public List<SolicitudDeEliminacionDTO> buscarSolicitudesEliminacion() {
        return solicitudesEliminacion;
    }
}
