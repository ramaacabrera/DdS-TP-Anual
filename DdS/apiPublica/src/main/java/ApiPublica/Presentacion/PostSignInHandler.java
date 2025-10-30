package ApiPublica.Presentacion;

import Keycloak.UserCreator;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import utils.Persistencia.UsuarioRepositorio;
import utils.Dominio.Usuario.*;


public class PostSignInHandler implements Handler {
    private UsuarioRepositorio usuarioRepositorio;
    private String urlWeb;

    public PostSignInHandler(UsuarioRepositorio usuarioRepositorio, String urlWeb) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.urlWeb = urlWeb;
    }

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

        ctx.redirect("http://localhost:7070/login");

    }
}

