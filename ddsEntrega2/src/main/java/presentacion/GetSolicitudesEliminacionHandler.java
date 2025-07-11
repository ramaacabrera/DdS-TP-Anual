package presentacion;

import Persistencia.SolicitudEliminacionRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class GetSolicitudesEliminacionHandler implements Handler {
    private final SolicitudEliminacionRepositorio repositorio;

    public GetSolicitudesEliminacionHandler(SolicitudEliminacionRepositorio repositorio) {this.repositorio = repositorio;}

    @Override
    public void handle(@NotNull Context context){
        context.json(repositorio.buscarTodas());
    }

}
