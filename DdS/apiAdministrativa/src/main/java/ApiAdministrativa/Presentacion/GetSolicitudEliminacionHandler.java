package ApiAdministrativa.Presentacion;

import utils.Persistencia.SolicitudEliminacionRepositorio;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import utils.Dominio.Solicitudes.SolicitudDeEliminacion;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.UUID;

public class GetSolicitudEliminacionHandler implements Handler {
    private final SolicitudEliminacionRepositorio solicitudEliminacionRepositorio;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetSolicitudEliminacionHandler(SolicitudEliminacionRepositorio solicitudEliminacionRepositorio) {
        this.solicitudEliminacionRepositorio = solicitudEliminacionRepositorio;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        String idString = ctx.pathParam("id");

        try {
            UUID id = UUID.fromString(idString);

            // USAR EL REPOSITORIO DIRECTAMENTE
            Optional<SolicitudDeEliminacion> resultadoBusqueda = solicitudEliminacionRepositorio.buscarPorId(id);

            if (resultadoBusqueda.isPresent()) {
                System.out.println("✅ Solicitud encontrada: " + id);
                ctx.status(200).json(resultadoBusqueda.get());
            } else {
                System.out.println("❌ Solicitud no encontrada: " + id);
                ctx.status(404).result("Solicitud no encontrada");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("❌ ID inválido: " + idString);
            ctx.status(400).result("ID inválido");
        }
    }
}