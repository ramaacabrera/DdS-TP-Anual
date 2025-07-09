package presentacion;

import Persistencia.SolicitudEliminacionRepositorio;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.example.agregador.Solicitudes.EstadoSolicitudEliminacion;
import org.example.agregador.Solicitudes.SolicitudDeEliminacion;

import java.util.Optional;

public class PutSolicitudEliminacionHandler implements Handler{
    private final SolicitudEliminacionRepositorio repositorio;

    public PutSolicitudEliminacionHandler(SolicitudEliminacionRepositorio repositorio) {this.repositorio = repositorio;}

    @Override
    public void handle(Context context) {
        String id = context.pathParam("id");
        String body = context.body();
        Optional<SolicitudDeEliminacion> resultadoBusqueda = repositorio.buscarPorId(id);
        if(resultadoBusqueda.isPresent()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode jsonNode = mapper.readTree(body);
                EstadoSolicitudEliminacion estado = EstadoSolicitudEliminacion
                        .valueOf(jsonNode.get("estadoSolicitud").asText());
                if(estado == EstadoSolicitudEliminacion.ACEPTADA){
                    resultadoBusqueda.get().aceptarSolicitud();
                }
                else{
                    resultadoBusqueda.get().rechazarSolicitud();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
