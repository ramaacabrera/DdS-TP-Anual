package cargadorDinamico.domain.DinamicaDto;

import cargadorDinamico.domain.HechosYColeccionesD.HechoModificado_D; // Usar la nueva clase
import cargadorDinamico.domain.Usuario.Usuario_D;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class SolicitudModificacion_D_DTO {

    @JsonProperty("ID_HechoAsociado")
    private UUID ID_HechoAsociado;

    @JsonProperty("Justificacion")
    private String Justificacion;

    @JsonProperty("Usuario")
    private Usuario_D Usuario;

    @JsonProperty("HechoModificado")
    private HechoModificado_D HechoModificado;

    public SolicitudModificacion_D_DTO() {}

    // Getters y Setters
    public UUID getID_HechoAsociado() { return ID_HechoAsociado; }
    public void setID_HechoAsociado(UUID ID_HechoAsociado) { this.ID_HechoAsociado = ID_HechoAsociado; }

    public String getJustificacion() { return Justificacion; }
    public void setJustificacion(String Justificacion) { this.Justificacion = Justificacion; }

    public Usuario_D getUsuario() { return Usuario; }
    public void setUsuario(Usuario_D Usuario) { this.Usuario = Usuario; }

    public HechoModificado_D getHechoModificado() { return HechoModificado; }
    public void setHechoModificado(HechoModificado_D HechoModificado) { this.HechoModificado = HechoModificado; }
}