package agregador.HechosYColecciones;

import agregador.Consenso.Absoluta;
import agregador.Consenso.AlgoritmoConsenso;
import agregador.Consenso.MayoriaSimple;
import agregador.Consenso.MultiplesMenciones;
import agregador.Criterios.Criterio;
import agregador.fuente.Fuente;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import utils.DTO.ColeccionDTO;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import javax.persistence.*;

@Entity
public class Coleccion {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type = "uuid-char")
    @Column(name = "handle", length = 36 , updatable = false, nullable = false)
    private UUID handle;

    @ManyToMany (cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "ColeccionXHecho",
            joinColumns = @JoinColumn(name = "handle"),
            inverseJoinColumns = @JoinColumn(name = "hecho_id")
    )
    private List<Hecho> hechos;

    private String titulo;
    private String descripcion;

    @ManyToMany (cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "ColeccionXFuente",
            joinColumns = @JoinColumn(name = "handle"),
            inverseJoinColumns = @JoinColumn(name = "id_fuente")
    )
    private List<Fuente> fuentes = new ArrayList<>();

    @ManyToMany (cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "ColeccionXCriterio",
            joinColumns = @JoinColumn(name = "handle"),
            inverseJoinColumns = @JoinColumn(name = "id_criterio")
    )
    private List<Criterio> criteriosDePertenencia;

    private TipoAlgoritmoConsenso algoritmoDeConsenso;

    @ManyToMany (cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "ColeccionXHecho",
            joinColumns = @JoinColumn(name = "handle"),
            inverseJoinColumns = @JoinColumn(name = "hecho_id")
    )
    private List<Hecho> hechosConsensuados;

    public Coleccion(){}

    public Coleccion(String titulo, String descripcion, List<Criterio> criterios, TipoAlgoritmoConsenso algoritmoDeConsenso) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.criteriosDePertenencia = criterios;
        this.algoritmoDeConsenso = algoritmoDeConsenso;

        this.generarHandle();

    } //las listas las seteo antes con new

    public Coleccion(ColeccionDTO coleccionDTO) {
        titulo = coleccionDTO.getTitulo();
        descripcion = coleccionDTO.getDescripcion();
        fuentes = coleccionDTO.getFuentes();
        criteriosDePertenencia = coleccionDTO.getCriteriosDePertenencia();
        algoritmoDeConsenso = coleccionDTO.getAlgoritmoDeConsenso();
        this.generarHandle();
    }

    public String getTitulo() {return titulo;}
    public String getDescripcion() {return descripcion;}
    public TipoAlgoritmoConsenso getAlgoritmoDeConsenso() {return algoritmoDeConsenso;}
    public List<Criterio> getCriteriosDePertenencia() {return criteriosDePertenencia; }
    public List<Fuente> getFuente(){ return fuentes;}
    public List<Hecho> getHechos() { return hechos.stream().filter(Hecho::estaActivo).collect(Collectors.toList());}
    public UUID getHandle() {return handle;}
    public List<Hecho> getHechosConsensuados() {return hechosConsensuados;}

    public void setHechos(List<Hecho> hechos) {this.hechos = hechos;}
    public void setTitulo(String titulo) {this.titulo = titulo;}
    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}
    public void setFuente(List<Fuente> fuente) {this.fuentes = fuente;}
    public void setCriteriosDePertenencia(List<Criterio> criteriosDePertenencia) {this.criteriosDePertenencia = criteriosDePertenencia;}
    public void setAlgoritmoDeConsenso(TipoAlgoritmoConsenso algoritmoDeConsenso) {this.algoritmoDeConsenso = algoritmoDeConsenso; }

    public void generarHandle() {
        if(titulo != null || !titulo.isEmpty()) {
            handle = UUID.randomUUID();
        }
        else {
            String contenido = titulo + descripcion;
               // genera UUID basado en el contenido
            handle = UUID.nameUUIDFromBytes(contenido.getBytes());
        }
    }

    public void agregarCriterio(Criterio criterio) {
        criteriosDePertenencia.add(criterio);
    }

    public boolean tieneAlgoritmoDeConsenso(){
        return algoritmoDeConsenso != null;
    }

    public boolean agregarHecho(Hecho otroHecho) {
        for (Hecho hechoExistente : hechos) {
            if (hechoExistente.esIgualAotro(otroHecho)) {
                hechoExistente.actualizarCon(otroHecho);
                return false; // false → no es nuevo, lo actualicé
            }
        }
        hechos.add(otroHecho);
        return true; // true → era nuevo, lo agregué
    }

    public void agregarFuente(Fuente fuente){
        fuentes.add(fuente);
    }

    public void eliminarFuente(Fuente fuente){
        fuentes.remove(fuente);
    }

    public List<Hecho> obtenerHechosQueCumplen(List<Criterio> criteriosDeBusqueda, ModosDeNavegacion modosDeNavegacion) {

        //Filtramos hechos por criterios
        List<Hecho> hechosFiltrados = hechos.stream()
                .filter(h -> criteriosDeBusqueda.stream().allMatch(c -> c.cumpleConCriterio(h)))
                .filter(Hecho::estaActivo)
                .collect(Collectors.toList());

        //Si es IRRESTRICTA devolvemos los hechos filtrados tal cual
        if (modosDeNavegacion == ModosDeNavegacion.IRRESTRICTA || algoritmoDeConsenso == null ) {
            return hechosFiltrados;
        }

        //Si es CURADA devolvemos la intersección entre hechos filtrados y consensuados
        return hechosFiltrados.stream()
                .filter(hechosConsensuados::contains)
                .collect(Collectors.toList());
    }

    public boolean cumpleCriterio(Hecho hecho) {
        if (criteriosDePertenencia == null || criteriosDePertenencia.isEmpty()) {
            return true;
        }

        // Evaluamos TODOS los criterios
        for (Criterio criterio : criteriosDePertenencia) {
            if (!criterio.cumpleConCriterio(hecho)) {
                return false;
            }
        }

        return true;
    }

    public void ejecutarAlgoritmoDeConsenso() {
        AlgoritmoConsenso algoritmo;
        if (algoritmoDeConsenso != null) {
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
                        throw new IllegalStateException("Algoritmo no existe");
            }
            this.hechosConsensuados = algoritmo.obtenerHechosConsensuados(this);
        }
        else{
            this.hechosConsensuados = hechos;
        }
    }
    
}

