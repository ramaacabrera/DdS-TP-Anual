package Agregador.fuente;

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

public abstract class Fuente {
    protected TipoDeFuente tipoDeFuente;
    String ruta;

    public Fuente() {}

    public Fuente(TipoDeFuente tipoDeFuente, String rutaNueva){// Conexion conexion) {
        this.tipoDeFuente = tipoDeFuente;
        this.ruta = rutaNueva;
    }

    public TipoDeFuente getTipoDeFuente(){return tipoDeFuente;}

    public abstract List<HechoDTO> getHechoes();

}