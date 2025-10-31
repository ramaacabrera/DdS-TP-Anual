package ApiAdministrativa.Presentacion;

import utils.Persistencia.SolicitudEliminacionRepositorio;
import utils.Dominio.Solicitudes.SolicitudDeEliminacion;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class GetSolicitudesEliminacionHandler implements Handler {
    private final SolicitudEliminacionRepositorio solicitudEliminacionRepositorio;

    public GetSolicitudesEliminacionHandler(SolicitudEliminacionRepositorio solicitudEliminacionRepositorio) {
        this.solicitudEliminacionRepositorio = solicitudEliminacionRepositorio;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        // USAR EL REPOSITORIO DIRECTAMENTE - eliminar las llamadas HTTP
        List<SolicitudDeEliminacion> solicitudes = solicitudEliminacionRepositorio.buscarTodas();
        System.out.println("📋 Solicitudes encontradas en BD: " + solicitudes.size());
        ctx.status(200).json(solicitudes);
    }
}
