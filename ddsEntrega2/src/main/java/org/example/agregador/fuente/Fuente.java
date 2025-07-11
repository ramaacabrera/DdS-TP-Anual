package org.example.agregador.fuente;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.example.agregador.DTO.HechoDTO;
import org.example.agregador.Criterios.Criterio;
import org.example.fuenteDinamica.FuenteDinamica;
import org.example.fuenteEstatica.FuenteEstatica;
import org.example.fuenteProxy.FuenteProxy;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipoDeFuente"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FuenteEstatica.class, name = "ESTATICA"),
        @JsonSubTypes.Type(value = FuenteDinamica.class, name = "DINAMICA"),
        @JsonSubTypes.Type(value = FuenteProxy.class, name = "PROXY")
})

public abstract class Fuente {
    protected TipoDeFuente tipoDeFuente;
    protected Conexion conexion;

    public Fuente() {}

    public Fuente(TipoDeFuente tipoDeFuente, Conexion conexion) {
        this.tipoDeFuente = tipoDeFuente;
        this.conexion = conexion;
    }

    public Conexion getConexion() {
        return conexion;
    }

    public TipoDeFuente getTipoDeFuente(){return tipoDeFuente;}

    public List<HechoDTO> obtenerHechos(List<Criterio> criterios){
        List<HechoDTO> hechos = conexion.obtenerHechos(criterios);
        hechos.forEach(hecho -> hecho.setFuente(this));
        return hechos;
    }
}