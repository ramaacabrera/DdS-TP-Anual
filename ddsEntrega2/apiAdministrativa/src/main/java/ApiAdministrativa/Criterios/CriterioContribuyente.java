package ApiAdministrativa.Criterios;

import agregador.Criterios.Criterio;
import agregador.HechosYColecciones.Hecho;
import agregador.Usuario.Usuario;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class CriterioContribuyente extends Criterio {

    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario contribuyente;

    public CriterioContribuyente(Usuario contribuyenteNuevo) {contribuyente = contribuyenteNuevo;}
    public CriterioContribuyente() {}
    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        return hecho.getContribuyente()==contribuyente;
    }

    @Override
    public String getQueryCondition() {return "h.contribuyente = " + contribuyente.getId_usuario();}
}
