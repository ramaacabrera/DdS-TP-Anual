package gestorPublico.domain.Criterios;

import gestorPublico.domain.HechosYColecciones.Etiqueta;
import gestorPublico.domain.HechosYColecciones.Hecho;

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

        // Obtener nombres de las etiquetas del criterio (case-insensitive)
        Set<String> nombresCriterio = etiquetas.stream()
                .map(et -> et.getNombre() != null ? et.getNombre().toLowerCase() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Obtener nombres de las etiquetas del hecho (case-insensitive)
        Set<String> nombresHecho = hecho.getEtiquetas().stream()
                .map(et -> et.getNombre() != null ? et.getNombre().toLowerCase() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Verificar que TODAS las etiquetas del criterio estén en el hecho
        return nombresHecho.containsAll(nombresCriterio);
    }

    public List<Etiqueta> getEtiquetas() { return etiquetas; }
    public void setEtiquetas(List<Etiqueta> etiquetas_) { etiquetas = etiquetas_; }

    @Override
    public String getQueryCondition() {
        if (etiquetas == null || etiquetas.isEmpty()) return "1=1";

        // Para requerir TODAS las etiquetas, necesitamos una condición más compleja
        return buildAllEtiquetasCondition();
    }

    private String buildAllEtiquetasCondition() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < etiquetas.size(); i++) {
            if (i > 0) sb.append(" AND ");
            sb.append("EXISTS (SELECT 1 FROM h.etiquetas e")
                    .append(i).append(" WHERE LOWER(e").append(i).append(".nombre) = :nombreEtiqueta").append(i).append(")");
        }
        return sb.toString();
    }

    @Override
    @Transient
    public Map<String, Object> getQueryParameters() {
        Map<String, Object> params = new HashMap<>();
        if (etiquetas != null && !etiquetas.isEmpty()) {
            for (int i = 0; i < etiquetas.size(); i++) {
                Etiqueta et = etiquetas.get(i);
                if (et.getNombre() != null) {
                    params.put("nombreEtiqueta" + i, et.getNombre().toLowerCase());
                }
            }
        }
        return params;
    }
}