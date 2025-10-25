package ApiAdministrativa.Criterios;

import agregador.HechosYColecciones.Etiqueta;
import agregador.HechosYColecciones.Hecho;

import javax.persistence.*;
import java.util.List;

@Entity
public class CriterioEtiquetas extends Criterio {

    @ManyToMany (cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "CriterioEtiquetaXEtiqueta",
            joinColumns = @JoinColumn(name = "id_criterio"),
            inverseJoinColumns = @JoinColumn(name = "id_etiqueta")
    )
    private List<Etiqueta> etiquetas;

    public CriterioEtiquetas() {}
    public CriterioEtiquetas(List<Etiqueta> listaEtiquetas) {etiquetas = listaEtiquetas;}

    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        boolean existeEnHecho = false;
        for(Etiqueta etiqueta : etiquetas){
            existeEnHecho = hecho.getEtiquetas().contains(etiqueta);
        }
        return existeEnHecho;
    }

    public List<Etiqueta> getEtiquetas() { return etiquetas; }
    public void setEtiquetas(List<Etiqueta> etiquetas_) {etiquetas = etiquetas_;}

    @Override
    public String getQueryCondition() {
        return "";
    }
//        StringBuilder retorno = new StringBuilder("( 1=1");
//        for(String etiqueta : etiquetas){
//        retorno.append(" or h.descripcion like '%").append(etiqueta).append("%'");
//
//        }
//        return retorno.toString();}
}
