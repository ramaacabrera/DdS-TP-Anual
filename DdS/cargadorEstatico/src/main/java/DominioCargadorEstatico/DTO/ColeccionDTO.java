package DominioCargadorEstatico.DTO;

import utils.Dominio.Criterios.Criterio;
import utils.Dominio.HechosYColecciones.TipoAlgoritmoConsenso;
import utils.Dominio.fuente.Fuente;

import java.util.ArrayList;
import java.util.List;

public class ColeccionDTO{
    private String titulo;
    private String descripcion;
    private List<Fuente> fuentes = new ArrayList<>();
    private List<Criterio> criteriosDePertenencia = new ArrayList<>();
    private TipoAlgoritmoConsenso algoritmoDeConsenso;

    public ColeccionDTO(){}

    public ColeccionDTO(String titulo, String descripcion,List<Fuente> fuentes, List<Criterio> criteriosDePertenencia, TipoAlgoritmoConsenso algoritmoDeConsenso){
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fuentes = fuentes;
        this.criteriosDePertenencia = criteriosDePertenencia;
        this.algoritmoDeConsenso = algoritmoDeConsenso;
    }

    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public List<Criterio> getCriteriosDePertenencia() {return criteriosDePertenencia;}
    public List<Fuente> getFuentes() { return fuentes; }
    public TipoAlgoritmoConsenso getAlgoritmoDeConsenso() { return algoritmoDeConsenso; }

    public void setTitulo(String titulo) { this.titulo = titulo; }

    public void setAlgoritmoDeConsenso(TipoAlgoritmoConsenso algoritmoDeConsenso) {this.algoritmoDeConsenso = algoritmoDeConsenso;}
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setFuentes(List<Fuente> fuentes) {this.fuentes = fuentes;}

    public void setCriteriosDePertenencia(List<Criterio> criteriosDePertenencia) {this.criteriosDePertenencia = criteriosDePertenencia;}
}
