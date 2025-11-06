package DominioGestorPublico.Criterios;

import DominioGestorPublico.HechosYColecciones.Etiqueta;
import DominioGestorPublico.HechosYColecciones.Hecho;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

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

    @Override
    @Transient
    public Map<String, Object> getQueryParameters() {
        return Map.of();
    }

}
