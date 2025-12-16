package cargadorDinamico.domain.Solicitudes;

import cargadorDinamico.domain.DinamicaDto.SolicitudDeModificacionDTO;
import cargadorDinamico.domain.HechosYColecciones.Hecho;
import cargadorDinamico.domain.HechosYColecciones.HechoModificado;
import cargadorDinamico.repository.HechoRepositorio;

import javax.persistence.*;
import java.util.UUID;

@Entity
@DiscriminatorValue("MODIFICACION")
public class SolicitudDeModificacion extends Solicitud {

    @Enumerated(EnumType.STRING)
    private EstadoSolicitudModificacion estadoSolicitudModificacion;


    @OneToOne
    @JoinColumn(name = "hecho_modificado_id")
    private HechoModificado hechoModificado;

    public SolicitudDeModificacion(){}

    @Override
    public void aceptarSolicitud() {
        this.estadoSolicitudModificacion = EstadoSolicitudModificacion.ACEPTADA;
    }

    @Override
    public void rechazarSolicitud() {
        this.estadoSolicitudModificacion = EstadoSolicitudModificacion.RECHAZADA;
    }

    // CORRECCIÓN: Getters y setters necesarios
    public EstadoSolicitudModificacion getEstadoSolicitudModificacion() {
        return estadoSolicitudModificacion;
    }

    public void setEstadoSolicitudModificacion(EstadoSolicitudModificacion estadoSolicitudModificacion) {
        this.estadoSolicitudModificacion = estadoSolicitudModificacion;
    }


    public HechoModificado getHechoModificado() {
        return hechoModificado;
    }

    public void setHechoModificado(HechoModificado hechoModificado) {
        this.hechoModificado = hechoModificado;
    }

}