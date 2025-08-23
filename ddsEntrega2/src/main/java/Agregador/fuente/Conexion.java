package Agregador.fuente;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import Agregador.DTO.HechoDTO;
import Agregador.Criterios.Criterio;
import FuenteEstatica.ConexionEstatica;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipoDeConexion"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConexionEstatica.class, name = "ESTATICA")
})

public abstract class Conexion {
    //public Fuente fuente;

    public abstract List<HechoDTO> obtenerHechos(List<Criterio> criterios);

    //public Fuente getFuente() { return fuente; }
    //public void setFuente(Fuente fuente) { this.fuente = fuente; }
}
