package gestorAdministrativo.domain.Criterios;

import gestorAdministrativo.domain.HechosYColecciones.Hecho;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Map;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Criterio {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type = "uuid-char")
    @Column(name = "id_criterio", length = 36, updatable = false, nullable = false)
    private UUID id;

    public Criterio() {}


    public abstract boolean cumpleConCriterio(Hecho hecho);

    public abstract String getQueryCondition();
    public abstract Map<String, Object> getQueryParameters();


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}