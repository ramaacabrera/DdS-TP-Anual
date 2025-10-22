package Agregador.Handlers;

import Agregador.Cargador.ConexionCargador;
import Agregador.Persistencia.FuenteRepositorio;
import Agregador.fuente.Fuente;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.validation.Validator;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import org.jetbrains.annotations.NotNull;
import utils.DTO.FuenteDTO;
import utils.DTO.IdMensajeDTO;

public class OnConnectHandler implements WsConnectHandler {
    private final FuenteRepositorio fuenteRepositorio;
    private final ConexionCargador conexionCargador;


    public OnConnectHandler(ConexionCargador conCargador, FuenteRepositorio fuenteRepo) {
        fuenteRepositorio = fuenteRepo;
        conexionCargador = conCargador;
    }

    private static final ObjectMapper mapper = new ObjectMapper();
    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) throws Exception {
        System.out.println("Conexion Recibida");
        try {

            String header = ctx.header("fuenteDTO");
            FuenteDTO nuevo = mapper.readValue(header, FuenteDTO.class);
            Fuente fuentePersistida = fuenteRepositorio.buscarPorRuta(nuevo.getRuta());

            if(fuentePersistida == null) {
                fuentePersistida = fuenteRepositorio.guardar(new Fuente(nuevo));
            }
            conexionCargador.agregarFuente(fuentePersistida.getId(), ctx);

            ctx.send(mapper.writeValueAsString(new IdMensajeDTO("idCargador", fuentePersistida.getId())));
        } catch (Exception e) {
            ctx.closeSession(400, "Header FuenteDTO invalido "+e.getMessage());
        }


    }
}