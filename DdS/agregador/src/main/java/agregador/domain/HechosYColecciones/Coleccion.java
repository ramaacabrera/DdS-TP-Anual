package agregador.domain.HechosYColecciones;

import agregador.domain.Consenso.Absoluta;
import agregador.domain.Consenso.AlgoritmoConsenso;
import agregador.domain.Consenso.MayoriaSimple;
import agregador.domain.Consenso.MultiplesMenciones;
import agregador.domain.Criterios.Criterio;
import agregador.domain.HechosYColecciones.Hecho;
import agregador.domain.HechosYColecciones.ModosDeNavegacion;
import agregador.domain.fuente.Fuente;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
public class Coleccion {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Type(type = "uuid-char")
    @Column(name = "handle", length = 36 , updatable = false, nullable = false)
    private UUID handle;

    private String titulo;
    private String descripcion;

    @Enumerated(EnumType.ORDINAL)
    private TipoAlgoritmoConsenso algoritmoDeConsenso;


    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "ColeccionXHecho", // Tabla 1
            joinColumns = @JoinColumn(name = "handle"),
            inverseJoinColumns = @JoinColumn(name = "hecho_id")
    )
    private List<Hecho> hechos = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "ColeccionXFuente",
            joinColumns = @JoinColumn(name = "handle"),
            inverseJoinColumns = @JoinColumn(name = "id_fuente")
    )
    private List<Fuente> fuentes = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "ColeccionXCriterio",
            joinColumns = @JoinColumn(name = "handle"),
            inverseJoinColumns = @JoinColumn(name = "id_criterio")
    )
    private List<Criterio> criteriosDePertenencia = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "ColeccionXHechoConsensuado", // Tabla 2 (Diferente a la de arriba)
            joinColumns = @JoinColumn(name = "handle"),
            inverseJoinColumns = @JoinColumn(name = "hecho_id")
    )
    private List<Hecho> hechosConsensuados = new ArrayList<>();

    public Coleccion(){}

    public Coleccion(String titulo, String descripcion, List<Criterio> criterios, TipoAlgoritmoConsenso algoritmoDeConsenso) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.criteriosDePertenencia = criterios;
        this.algoritmoDeConsenso = algoritmoDeConsenso;
    }


    public UUID getHandle() { return handle; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public TipoAlgoritmoConsenso getAlgoritmoDeConsenso() { return algoritmoDeConsenso; }
    public void setAlgoritmoDeConsenso(TipoAlgoritmoConsenso algoritmoDeConsenso) { this.algoritmoDeConsenso = algoritmoDeConsenso; }

    public List<Criterio> getCriteriosDePertenencia() { return criteriosDePertenencia; }
    public void setCriteriosDePertenencia(List<Criterio> criteriosDePertenencia) { this.criteriosDePertenencia = criteriosDePertenencia; }

    public List<Fuente> getFuente() { return fuentes; }
    public void setFuente(List<Fuente> fuente) { this.fuentes = fuente; }

    public List<Hecho> getHechos() {
        if (hechos == null) return new ArrayList<>();
        return hechos.stream().filter(Hecho::estaActivo).collect(Collectors.toList());
    }
    public void setHechos(List<Hecho> hechos) { this.hechos = hechos; }

    public List<Hecho> getHechosConsensuados() { return hechosConsensuados; }
    public void setHechosConsensuados(List<Hecho> hechosConsensuados) { this.hechosConsensuados = hechosConsensuados; }

    public void agregarHecho(Hecho hecho) {
        if (this.hechos == null) {
            this.hechos = new ArrayList<>();
        }
        if (!this.hechos.contains(hecho)) {
            this.hechos.add(hecho);
        }
    }

    public boolean cumpleCriterio(Hecho hecho) {
        if (this.criteriosDePertenencia == null || this.criteriosDePertenencia.isEmpty()) {
            return true;
        }

        return this.criteriosDePertenencia.stream().allMatch(criterio -> criterio.cumpleConCriterio(hecho));
    }

    public boolean ejecutarAlgoritmoDeConsenso() {
        if (algoritmoDeConsenso != null) {
            AlgoritmoConsenso algoritmo;
            switch (algoritmoDeConsenso) {
                case ABSOLUTA:
                    algoritmo = new Absoluta();
                    break;
                case MAYORIA_SIMPLE:
                    algoritmo = new MayoriaSimple();
                    break;
                case MULTIPLES_MENCIONES:
                    algoritmo = new MultiplesMenciones();
                    break;
                default:
                    throw new IllegalStateException("Algoritmo no soportado: " + algoritmoDeConsenso);
            }
            System.out.println("Aplicando algoritmo de consenso");
            this.hechosConsensuados = algoritmo.obtenerHechosConsensuados(this);
            return true;
        } else {
            this.hechosConsensuados = new ArrayList<>(this.getHechos());
            return false;
        }
    }
}