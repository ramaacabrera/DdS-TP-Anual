package gestorPublico.controller;

import gestorPublico.dto.LoginDTO;
import gestorPublico.dto.RegistroUsuarioDTO;
import gestorPublico.service.UsuarioService;
import io.javalin.http.Handler;

import java.util.Map;

public class UsuarioController {

    private UsuarioService usuarioService=null;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public Handler login = ctx -> {
        try {
            LoginDTO loginDto = ctx.bodyAsClass(LoginDTO.class);

            Map<String, Object> resultado = usuarioService.login(loginDto);
            ctx.status(200).json(resultado);

        } catch (SecurityException e) {
            ctx.status(401).json(Map.of("error", "Credenciales incorrectas"));
        } catch (IllegalArgumentException e) {
            ctx.status(404).json(Map.of("error", e.getMessage())); // Usuario no encontrado
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", "Error interno de servidor"));
        }
    };

    public Handler registrar = ctx -> {
        try {
            RegistroUsuarioDTO registroDto = ctx.bodyAsClass(RegistroUsuarioDTO.class);

            usuarioService.registrarUsuario(registroDto);

            ctx.status(201).json(Map.of("mensaje", "Usuario creado exitosamente"));

        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", "No se pudo registrar el usuario"));
        }
    };

    public Handler obtenerUsuario = ctx -> {
        String username = ctx.pathParam("username");
        var usuario = usuarioService.obtenerPorUsername(username);

        if (usuario != null) {
            ctx.json(usuario);
        } else {
            ctx.status(404).json(Map.of("error", "Usuario no encontrado"));
        }
    };
}