package web.controller;

import io.javalin.http.Handler;
import web.service.SolicitudService;
import web.utils.ViewUtil;
import java.util.Map;

public class AdministradorController {

    public SolicitudService solicitudService;

    public AdministradorController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    public Handler obtenerPagePanel = ctx -> {

        String accessToken = ctx.sessionAttribute("accessToken");
        if(accessToken == null) {
            System.out.println("Acceso no valido");
            ctx.redirect("/login");
            return;
        }

        String rolUsuario = ctx.sessionAttribute("rolUsuario");
        if(!rolUsuario.equals("ADMINISTRADOR")){
            ctx.status(401);
            ctx.redirect("/home");
        }

        Map<String, Object> modelo = ViewUtil.baseModel(ctx);

        int pendientesEliminacion = solicitudService.contarPendientesEliminacion(ctx.sessionAttribute("username"),rolUsuario, accessToken);
        int pendientesModificacion = solicitudService.contarPendientesModificacion(ctx.sessionAttribute("username"),rolUsuario, accessToken);

        modelo.put("pendientesEliminacion", pendientesEliminacion);
        modelo.put("pendientesModificacion", pendientesModificacion);

        ctx.render("panel-administrador.ftl", modelo);
    };
}
