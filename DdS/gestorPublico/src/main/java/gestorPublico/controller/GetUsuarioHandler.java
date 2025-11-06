package gestorPublico.controller;

import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import utils.Dominio.Usuario.Usuario;
import utils.Persistencia.UsuarioRepositorio;
import io.javalin.http.Handler;

public class GetUsuarioHandler implements Handler {
    private UsuarioRepositorio usuarioRepositorio;

    public GetUsuarioHandler(UsuarioRepositorio repo){this.usuarioRepositorio= repo;}

    @Override
    public void handle(@NotNull Context ctx){
        String username = ctx.pathParam("username");

        Usuario user = usuarioRepositorio.buscarPorUsername(username);

        ctx.json(user);
    }

}
