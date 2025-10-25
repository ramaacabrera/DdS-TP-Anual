package agregador.Handlers;

import agregador.Cargador.ConexionCargador;
import agregador.Persistencia.FuenteRepositorio;
import agregador.fuente.Fuente;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import org.jetbrains.annotations.NotNull;
import utils.DTO.ModelosMensajesDTO.IdCargadorPayload;
import utils.DTO.ModelosMensajesDTO.WsMessage;

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
            Fuente nuevo = mapper.readValue(header, Fuente.class);
            Fuente fuentePersistida = fuenteRepositorio.buscarPorRuta(nuevo.getRuta());

            if(fuentePersistida == null) {
                fuentePersistida = fuenteRepositorio.guardar(nuevo);
            }
            conexionCargador.agregarFuente(fuentePersistida.getId(), ctx);

            WsMessage<IdCargadorPayload> mensaje = new WsMessage<IdCargadorPayload>("idCargador", new IdCargadorPayload(fuentePersistida.getId()));

            ctx.send(mapper.writeValueAsString(mensaje));
        } catch (Exception e) {
            ctx.closeSession(400, "Header FuenteDTO invalido "+e.getMessage());
        }


    }
}