package web.domain.Criterios;

import web.domain.HechosYColecciones.Hecho;

public class CriterioUbicacion extends Criterio {
    private String descripcion;

    public CriterioUbicacion() {}

    public CriterioUbicacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        if (hecho.getUbicacion() == null || this.descripcion == null) return false;
        return hecho.getUbicacion().getDescripcion().toLowerCase().contains(this.descripcion.toLowerCase());
    }

    @Override
    public String getQueryCondition() {
        return "";
    }

    @Override
    public java.util.Map<String, Object> getQueryParameters() {
        return new java.util.HashMap<>();
    }
}