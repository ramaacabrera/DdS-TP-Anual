package ApiAdministrativa.Presentacion;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import utils.Keycloak.TokenValidator;
import utils.Persistencia.UsuarioRepositorio;

public class ValidarAdminHandler implements Handler {
    private UsuarioRepositorio usuarioRepositorio;

    public ValidarAdminHandler (UsuarioRepositorio usuarioRepositorio) {this.usuarioRepositorio = usuarioRepositorio;}

    @Override
    public void handle(@NotNull Context ctx){
        String username = ctx.header("username");
        String access_token = ctx.header("access_token");

        System.out.println("access_token: "  + access_token);

        TokenValidator validador = new TokenValidator();
        try{
            validador.validar(access_token);
        } catch (Exception e) {
            System.err.println("Error al validar el administrador: " + e.getMessage());
            ctx.status(400).result("Error al validar administrador");
            return;
        }

        if(usuarioRepositorio.buscarAdmin(username) == null){
            ctx.status(400).result("Administrador no encontrado");
            System.err.println("Administrador no encontrado");
            return;
        }

        System.out.println("Administrador valido: " + username);

    }
}
