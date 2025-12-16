package cargadorDinamico.domain.DinamicaDto;

import cargadorDinamico.domain.HechosYColeccionesD.HechoModificado_D;
import cargadorDinamico.domain.Solicitudes.SolicitudDeModificacion_D;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class SolicitudModificacionSalienteDTO {

    private UUID id;
    private UUID hechoId;
    private UUID usuarioId;
    private String justificacion;
    private HechoModificadoDTO hechoModificado;
    private String estado;

    public SolicitudModificacionSalienteDTO(SolicitudDeModificacion_D entidad) {
        this.id = entidad.getId();
        this.hechoId = entidad.getID_HechoAsociado();
        this.justificacion = entidad.getJustificacion();

        if (entidad.getUsuario() != null) {
            this.usuarioId = entidad.getUsuario().getId_usuario();
        }

        this.hechoModificado = new HechoModificadoDTO(entidad.getHechoModificado());

        if (entidad.getEstadoSolicitudModificacion() != null) {
            this.estado = entidad.getEstadoSolicitudModificacion().name();
        }
    }

    public UUID getId() { return id; }
    public UUID getHechoId() { return hechoId; }
    public UUID getUsuarioId() { return usuarioId; } // Esto generará "usuarioId": "uuid..."
    public String getJustificacion() { return justificacion; }
    public HechoModificadoDTO getHechoModificado() { return hechoModificado; }
    public String getEstado() { return estado; }
}