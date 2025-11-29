package agregador.dto.Criterios;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CriterioFechaDTO extends CriterioDTO {
    private Date fechaInicio;
    private Date fechaFin;
    private String tipoFecha; // "fechaDeCarga" o "fechaDeAcontecimiento"

    public CriterioFechaDTO() {
        super(null, "CRITERIO_FECHA");
    }

    public CriterioFechaDTO(UUID criterioId, Date fechaInicio, Date fechaFin, String tipoFecha) {
        super(criterioId, "CRITERIO_FECHA");
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.tipoFecha = tipoFecha;
    }

    // Getters y Setters
    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }

    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }

    public String getTipoFecha() { return tipoFecha; }
    public void setTipoFecha(String tipoFecha) { this.tipoFecha = tipoFecha; }

    @Override
    public String getQueryCondition() {
        StringBuilder retorno = new StringBuilder();

        if (fechaInicio != null) {
            retorno.append("(h.");
            if (tipoFecha.equals("fechaDeCarga")) {
                retorno.append("fechaDeCarga");
            } else {
                retorno.append("fechaDeAcontecimiento");
            }
            retorno.append(" >= :fechaInicio)");
        }

        if (fechaFin != null) {
            if (fechaInicio != null) {
                retorno.append(" AND ");
            }
            retorno.append("(h.");
            if (tipoFecha.equals("fechaDeCarga")) {
                retorno.append("fechaDeCarga");
            } else {
                retorno.append("fechaDeAcontecimiento");
            }
            retorno.append(" <= :fechaFin)");
        }

        return retorno.toString();
    }

    @Override
    public Map<String, Object> getQueryParameters() {
        Map<String, Object> params = new HashMap<>();
        if (fechaInicio != null) {
            params.put("fechaInicio", fechaInicio);
        }
        if (fechaFin != null) {
            params.put("fechaFin", fechaFin);
        }
        return params;
    }
}