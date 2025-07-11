package presentacion;

import Persistencia.SolicitudEliminacionRepositorio;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.example.agregador.Solicitudes.EstadoSolicitudEliminacion;
import org.example.agregador.Solicitudes.SolicitudDeEliminacion;
import org.example.fuenteDinamica.ControllerSolicitud;

import java.util.Optional;

public class PutSolicitudEliminacionHandler implements Handler{
    private final ControllerSolicitud controllerSolicitudEliminacion;

    public PutSolicitudEliminacionHandler(ControllerSolicitud controllerSolicitudNuevo) {controllerSolicitudEliminacion = controllerSolicitudNuevo;}

    @Override
    public void handle(Context context) {
        String id = context.pathParam("id");
        String body = context.body();
        boolean resultado = controllerSolicitudEliminacion.actualizarSolicitudEliminacion(body,id);
        if(resultado){
            context.status(200);
        } else {
            context.status(404);
        }
    }

}
