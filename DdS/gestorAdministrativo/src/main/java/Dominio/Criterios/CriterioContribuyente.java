package Dominio.Criterios;

import Dominio.HechosYColecciones.Hecho;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Map;
import java.util.Objects;

@Entity
public class CriterioContribuyente extends Criterio {

    private String nombreContribuyente; // Campo separado para el nombre

    public CriterioContribuyente(String nombreContribuyente) {
        this.nombreContribuyente = nombreContribuyente;
    }

    public CriterioContribuyente() {}

    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        if (hecho.getContribuyente() == null || this.nombreContribuyente == null) {
            return false;
        }
        return Objects.equals(hecho.getContribuyente().getNombre(), this.nombreContribuyente);
    }

    public String getNombreContribuyente() {
        return this.nombreContribuyente;
    };
    
    public void setNombreContribuyente(String nombreContribuyente) {
        this.nombreContribuyente = nombreContribuyente;
    }

    @Override
    public String getQueryCondition() {
        if (nombreContribuyente == null || nombreContribuyente.trim().isEmpty()) {
            return "1=1";
        }
        return "h.contribuyente in (SELECT u.id_usuario FROM Usuario u WHERE u.nombre LIKE '%" +
                nombreContribuyente + "%')";
    }

    @Override
    @Transient
    public Map<String, Object> getQueryParameters() {
        return Map.of();
    }
}
