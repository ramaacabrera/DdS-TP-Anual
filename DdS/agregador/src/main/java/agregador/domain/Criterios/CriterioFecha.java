package agregador.domain.Criterios;

import agregador.domain.HechosYColecciones.Hecho;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
public class CriterioFecha extends Criterio {
    private Date fechaInicio;
    private Date fechaFin;
    private String tipoFecha;

    public CriterioFecha(Date fechaInicioNuevo, Date fechaFinNuevo, String tipoNuevo) {
        fechaInicio = fechaInicioNuevo;
        fechaFin = fechaFinNuevo;
        tipoFecha = tipoNuevo;
    }
    public CriterioFecha(){}
    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        Date fecha = tipoFecha.equals("fechaDeCarga") ? hecho.getFechaDeCarga() : hecho.getFechaDeAcontecimiento();
        return (fechaInicio == null || !fecha.before(fechaInicio)) &&
                (fechaFin == null || !fecha.after(fechaFin));
    }

    public Date getFechaInicio() { return fechaInicio; }
    public Date getFechaFin() { return fechaFin; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio;  }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin;  }

    public String getTipoFecha() { return tipoFecha; }
    public void setTipoFecha(String tipo){this.tipoFecha = tipo;}

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

@Transient
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
