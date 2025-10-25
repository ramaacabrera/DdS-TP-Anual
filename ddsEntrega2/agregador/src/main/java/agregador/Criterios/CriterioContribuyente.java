<<<<<<<< HEAD:DdS/utils/src/main/java/utils/Dominio/Criterios/CriterioContribuyente.java
package utils.Dominio.Criterios;

import utils.Dominio.Usuario.Usuario;
import utils.Dominio.HechosYColecciones.Hecho;
========
package agregador.Criterios;

import agregador.Usuario.Usuario;
import agregador.HechosYColecciones.Hecho;
>>>>>>>> 198c43e (Pruebas):ddsEntrega2/agregador/src/main/java/agregador/Criterios/CriterioContribuyente.java

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
