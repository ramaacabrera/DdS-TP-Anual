package org.example.agregador;

import org.example.fuente.Fuente;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Coleccion {

    private List<Hecho> hechos = new ArrayList<>();
    private String titulo;
    private String descripcion;
    private List<Fuente> fuente = new ArrayList<>();
    private String handle;
    private List<Criterio> criteriosDePertenencia = new ArrayList<>();
    private ModosDeNavegacion modosDeNavegacion;
    private AlgoritmoConsenso algoritmoDeConsenso;
    private List<Hecho> hechosConsensuados = new ArrayList<>();


    public Coleccion(String titulo, String descripcion, String handle, ModosDeNavegacion modosDeNavegacion, AlgoritmoConsenso algoritmoDeConsenso) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.handle = handle;
        this.modosDeNavegacion = modosDeNavegacion;
        this.algoritmoDeConsenso = algoritmoDeConsenso;
    } //las listas las seteo antes con new

    public void setHechos(List<Hecho> hechos) {this.hechos = hechos;}
    public void setTitulo(String titulo) {this.titulo = titulo;}
    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}
    public void setFuente(List<Fuente> fuente) {this.fuente = fuente;}
    public void setCriteriosDePertenencia(List<Criterio> criteriosDePertenencia) {this.criteriosDePertenencia = criteriosDePertenenc


    public void agregarCriterio(Criterio criterio) {
        criteriosDePertenencia.add(criterio);
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

    public List<Hecho> getHechos() {
        return hechos;
    }

    public List<Hecho> obtenerHechosQueCumplen(List<Criterio> criteriosDeBusqueda, ModosDeNavegacion modosDeNavegacion) {

        //Filtramos hechos por criterios
        List<Hecho> hechosFiltrados = hechos.stream()
                .filter(h -> criteriosDeBusqueda.stream().allMatch(c -> c.cumpleConCriterio(h)))
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


    public String getHandle() {
        return handle;
    }

    public void ejecutarAlgoritmoDeConsenso() {
        if (algoritmoDeConsenso != null) {
            this.hechosConsensuados = algoritmoDeConsenso.obtenerHechosConsensuados(this);
        }
        else{
            this.hechosConsensuados = hechos;
        }
    }

    public List<Fuente> getFuente(){ return fuente; }
    
}

