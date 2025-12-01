package cargadorEstatico.domain.Criterios;

import cargadorEstatico.domain.Criterios.Criterio;
import cargadorEstatico.domain.HechosYColecciones.Etiqueta;
import cargadorEstatico.domain.HechosYColecciones.Hecho;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
public class CriterioEtiquetas extends Criterio {

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "CriterioEtiquetaXEtiqueta",
            joinColumns = @JoinColumn(name = "id_criterio"),
            inverseJoinColumns = @JoinColumn(name = "id_etiqueta")
    )
    private List<Etiqueta> etiquetas = new ArrayList<>();

    public CriterioEtiquetas() {}
    public CriterioEtiquetas(List<Etiqueta> listaEtiquetas) { etiquetas = listaEtiquetas; }

    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        if (etiquetas == null || etiquetas.isEmpty()) return true;
        return hecho.getEtiquetas().stream()
                .anyMatch(etiquetaHecho -> etiquetas.contains(etiquetaHecho));
    }

    public List<Etiqueta> getEtiquetas() { return etiquetas; }
    public void setEtiquetas(List<Etiqueta> etiquetas_) { etiquetas = etiquetas_; }

    @Override
    public String getQueryCondition() {
        if (etiquetas == null || etiquetas.isEmpty()) return "1=1";
        return "EXISTS (SELECT e FROM h.etiquetas e WHERE e.id IN :idsEtiquetas)";
    }

    @Override
    @Transient
    public Map<String, Object> getQueryParameters() {
        Map<String, Object> params = new HashMap<>();
        if (etiquetas != null && !etiquetas.isEmpty()) {
            List<java.util.UUID> ids = etiquetas.stream()
                    .map(Etiqueta::getId)
                    .collect(Collectors.toList());
            params.put("idsEtiquetas", ids);
        }
        return params;
    }
}