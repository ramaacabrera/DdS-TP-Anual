package Agregador.fuente;

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
    protected TipoDeFuente tipoDeFuente;
    String ruta;

    public Fuente(TipoDeFuente tipoDeFuente, String rutaNueva){// Conexion conexion) {
        this.tipoDeFuente = tipoDeFuente;
        this.ruta = rutaNueva;
    }

    public TipoDeFuente getTipoDeFuente(){return tipoDeFuente;}
}