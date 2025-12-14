package agregador.domain.Criterios;

import agregador.domain.Criterios.Criterio;
import agregador.domain.HechosYColecciones.Etiqueta;
import agregador.domain.HechosYColecciones.Hecho;

import javax.persistence.*;
import java.util.*;
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
        if (hecho.getEtiquetas() == null || hecho.getEtiquetas().isEmpty()) return false;

        Set<String> nombresCriterio = etiquetas.stream()
                .map(et -> et.getNombre() != null ? et.getNombre().toLowerCase() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Verificar si alguna etiqueta del hecho coincide
        return hecho.getEtiquetas().stream()
                .anyMatch(etiquetaHecho ->
                        etiquetaHecho.getNombre() != null &&
                                nombresCriterio.contains(etiquetaHecho.getNombre().toLowerCase())
                );
    }

    public List<Etiqueta> getEtiquetas() { return etiquetas; }
    public void setEtiquetas(List<Etiqueta> etiquetas_) { etiquetas = etiquetas_; }

    @Override
    public String getQueryCondition() {
        if (etiquetas == null || etiquetas.isEmpty()) return "1=1";
        return "EXISTS (SELECT e FROM h.etiquetas e WHERE LOWER(e.nombre) IN :nombresEtiquetas)";
    }

    @Override
    @Transient
    public Map<String, Object> getQueryParameters() {
        Map<String, Object> params = new HashMap<>();
        if (etiquetas != null && !etiquetas.isEmpty()) {
            List<String> nombres = etiquetas.stream()
                    .map(Etiqueta::getNombre)
                    .filter(Objects::nonNull)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            params.put("nombresEtiquetas", nombres);
        }
        return params;
    }
}