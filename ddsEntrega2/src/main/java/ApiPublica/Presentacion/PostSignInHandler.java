package ApiPublica.Presentacion;

import Agregador.Persistencia.UsuarioRepositorio;
import Agregador.Usuario.RolUsuario;
import Agregador.Usuario.Usuario;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import utils.KeyCloak.UserCreator;


public class PostSignInHandler implements Handler {
    private UsuarioRepositorio usuarioRepositorio;
    public PostSignInHandler(UsuarioRepositorio usuarioRepositorio){this.usuarioRepositorio = usuarioRepositorio;}

    @Override
    public void handle(@NotNull Context ctx) {
        String usuario = ctx.formParam("usuario");
        if(usuarioRepositorio.buscarPorUsername(usuario) != null){
            ctx.sessionAttribute("error", "Nombre de usuario invalido");
            ctx.redirect("/api/sign-in");
            return;
        }
        String nombre = ctx.formParam("nombre");
        String apellido = ctx.formParam("apellido");
        int edad = Integer.parseInt(ctx.formParam("edad"));
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        UserCreator creador = new UserCreator();
        int codigo = creador.crearUsuario(usuario, password, nombre, apellido, email);
        if(codigo != 204){
            ctx.sessionAttribute("error", "No se pudo crear el usuario");
            ctx.redirect("/api/sign-in");
            return;
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setEdad(edad);
        nuevoUsuario.setUsername(usuario);
        nuevoUsuario.setRol(RolUsuario.CONTRIBUYENTE);

        usuarioRepositorio.guardar(nuevoUsuario);

        ctx.redirect("/api/login");

    }
}
