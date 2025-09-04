package Agregador.fuente;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import utils.DTO.HechoDTO;

import java.util.List;

//@JsonTypeInfo(
//        use = JsonTypeInfo.Id.NAME,
//        include = JsonTypeInfo.As.PROPERTY,
//        property = "tipoDeFuente"
//)
//@JsonSubTypes({
//        @JsonSubTypes.Type(value = FuenteEstatica.class, name = "ESTATICA"),
//        @JsonSubTypes.Type(value = FuenteDinamica.class, name = "DINAMICA"),
//        @JsonSubTypes.Type(value = FuenteProxy.class, name = "PROXY")
//})

public class Fuente {
    private TipoDeFuente tipoDeFuente;
    private String ruta;

    public Fuente() {}

    public Fuente(TipoDeFuente tipoDeFuente, String ruta) {
        this.tipoDeFuente = tipoDeFuente;
        this.ruta = ruta;
    }

    public TipoDeFuente getTipoDeFuente() {
        return tipoDeFuente;
    }

    public void setTipoDeFuente(TipoDeFuente tipoDeFuente) {
        this.tipoDeFuente = tipoDeFuente;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String toString() {
        return "Fuente{" +
                "tipoDeFuente=" + tipoDeFuente +
                ", ruta='" + ruta +
                '}';
    }
}
/*public class Fuente {
    private TipoDeFuente tipoDeFuente;
    private String ruta;
    
    public Fuente(){}
    
    public Fuente(TipoDeFuente tipoDeFuente, String rutaNueva){// Conexion conexion) {
        this.tipoDeFuente = tipoDeFuente;
        this.ruta = rutaNueva;
    }

    public void setTipoDeFuente(TipoDeFuente tipoDeFuente) {this.tipoDeFuente = tipoDeFuente;}
    public void setRuta(String ruta){this.ruta = ruta;}
    public TipoDeFuente getTipoDeFuente(){return tipoDeFuente;}
    public String getRuta(){return ruta;}
/*/