package gestorPublico.domain.HechosYColecciones;

import gestorPublico.domain.Consenso.Absoluta;
import gestorPublico.domain.Consenso.AlgoritmoConsenso;
import gestorPublico.domain.Consenso.MayoriaSimple;
import gestorPublico.domain.Consenso.MultiplesMenciones;
import gestorPublico.domain.Criterios.Criterio;
import gestorPublico.domain.fuente.Fuente;
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


    public void agregarCriterio(Criterio criterio) {
        if (this.criteriosDePertenencia == null) this.criteriosDePertenencia = new ArrayList<>();
        this.criteriosDePertenencia.add(criterio);
    }

    public boolean tieneAlgoritmoDeConsenso(){
        return algoritmoDeConsenso != null;
    }

    public void agregarFuente(Fuente fuente){
        if (this.fuentes == null) this.fuentes = new ArrayList<>();
        this.fuentes.add(fuente);
    }

    public void eliminarFuente(Fuente fuente){
        if (this.fuentes != null) {
            this.fuentes.remove(fuente);
        }
    }

    public List<Hecho> obtenerHechosQueCumplen(List<Criterio> criteriosDeBusqueda, ModosDeNavegacion modosDeNavegacion) {
        if (this.hechos == null) return new ArrayList<>();

        List<Hecho> hechosFiltrados = hechos.stream()
                .filter(Hecho::estaActivo) // Primero descartar inactivos
                .filter(h -> criteriosDeBusqueda.stream().allMatch(c -> c.cumpleConCriterio(h)))
                .collect(Collectors.toList());

        if (modosDeNavegacion == ModosDeNavegacion.IRRESTRICTA || algoritmoDeConsenso == null ) {
            return hechosFiltrados;
        }

        if (this.hechosConsensuados == null) return new ArrayList<>();

        return hechosFiltrados.stream()
                .filter(hechosConsensuados::contains)
                .collect(Collectors.toList());
    }

    public boolean ejecutarAlgoritmoDeConsenso() {
        if (algoritmoDeConsenso != null) {
            AlgoritmoConsenso algoritmo;
            switch (algoritmoDeConsenso) {
                case ABSOLUTA:
                    algoritmo = new Absoluta();
                    break;
                case MAYORIASIMPLE:
                    algoritmo = new MayoriaSimple();
                    break;
                case MULTIPLESMENCIONES:
                    algoritmo = new MultiplesMenciones();
                    break;
                default:
                    throw new IllegalStateException("Algoritmo no soportado: " + algoritmoDeConsenso);
            }
            this.hechosConsensuados = algoritmo.obtenerHechosConsensuados(this);
            return true;
        } else {
            this.hechosConsensuados = new ArrayList<>(this.getHechos());
            return false;
        }
    }
}