package utils.Dominio.Criterios;

import utils.Dominio.Usuario.Usuario;
import utils.Dominio.HechosYColecciones.Hecho;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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

    @Override
    public String getQueryCondition() {
        if (nombreContribuyente == null || nombreContribuyente.trim().isEmpty()) {
            return "1=1";
        }
        return "h.contribuyente in (SELECT u.id_usuario FROM Usuario u WHERE u.nombre LIKE '%" +
                nombreContribuyente + "%')";
    }
}
