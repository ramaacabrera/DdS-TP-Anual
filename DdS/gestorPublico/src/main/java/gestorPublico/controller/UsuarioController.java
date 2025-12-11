package gestorPublico.controller;

import gestorPublico.domain.Usuario.Usuario;
import gestorPublico.dto.Hechos.UsuarioDTO;
import gestorPublico.dto.LoginDTO;
import gestorPublico.dto.RegistroUsuarioDTO;
import gestorPublico.service.UsuarioService;
import io.javalin.http.Handler;

import java.util.Map;

public class UsuarioController {

    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public Handler sincronizar = ctx ->{
        UsuarioDTO usuario = ctx.bodyAsClass(UsuarioDTO.class);
        usuarioService.sincronizar(usuario);
        ctx.status(200);
    };

    public Handler login = ctx -> {
        try {
            LoginDTO loginDto;
            String contentType = ctx.contentType();

            if (contentType != null && contentType.contains("application/x-www-form-urlencoded")) {
                String usuario = ctx.formParam("usuario");
                String password = ctx.formParam("password");

                if (usuario == null || password == null) {
                    ctx.status(400).json(Map.of("error", "Se requieren usuario y password"));
                    return;
                }

                loginDto = new LoginDTO(usuario, password);
            } else {
                loginDto = ctx.bodyAsClass(LoginDTO.class);
            }

            Map<String, Object> resultado = usuarioService.login(loginDto);
            ctx.status(200).json(resultado);

        } catch (SecurityException e) {
            ctx.status(401).json(Map.of("error", "Credenciales incorrectas"));
        } catch (IllegalArgumentException e) {
            ctx.status(404).json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", "Error interno de servidor"));
        }
    };

    public Handler registrar = ctx -> {
        try {
            String contentType = ctx.contentType();

            System.out.println("Content-Type recibido: " + contentType);
            System.out.println("Cuerpo recibido: " + ctx.body());

            if (contentType != null && contentType.contains("application/x-www-form-urlencoded")) {
                // Leer como form parameters
                String nombre = ctx.formParam("nombre");
                String apellido = ctx.formParam("apellido");
                String edadStr = ctx.formParam("edad");
                String email = ctx.formParam("email");
                String usuario = ctx.formParam("usuario");
                String password = ctx.formParam("password");
                String confirmPassword = ctx.formParam("confirmPassword");

                System.out.println("Parámetros recibidos:");
                System.out.println("nombre: " + nombre);
                System.out.println("apellido: " + apellido);
                System.out.println("edad: " + edadStr);
                System.out.println("email: " + email);
                System.out.println("usuario: " + usuario);
                System.out.println("password: " + password);
                System.out.println("confirmPassword: " + confirmPassword);

                // Validar campos requeridos
                if (nombre == null || nombre.isEmpty() ||
                        apellido == null || apellido.isEmpty() ||
                        email == null || email.isEmpty() ||
                        usuario == null || usuario.isEmpty() ||
                        password == null || password.isEmpty() ||
                        confirmPassword == null || confirmPassword.isEmpty()) {

                    ctx.status(400).json(Map.of("error", "Todos los campos son requeridos"));
                    return;
                }

                // Validar contraseñas
                if (!password.equals(confirmPassword)) {
                    ctx.status(400).json(Map.of("error", "Las contraseñas no coinciden"));
                    return;
                }

                // Convertir edad
                int edad;
                try {
                    edad = Integer.parseInt(edadStr);
                } catch (NumberFormatException e) {
                    ctx.status(400).json(Map.of("error", "La edad debe ser un número"));
                    return;
                }

                // Crear DTO
                RegistroUsuarioDTO registroDto = new RegistroUsuarioDTO();
                registroDto.nombre = nombre;
                registroDto.apellido = apellido;
                registroDto.edad = edad;
                registroDto.email = email;
                registroDto.usuario = usuario;
                registroDto.password = password;
                registroDto.confirmPassword = confirmPassword;

                usuarioService.registrarUsuario(registroDto);

            } else {
                // Leer como JSON
                RegistroUsuarioDTO registroDto = ctx.bodyAsClass(RegistroUsuarioDTO.class);

                // Para JSON, no podemos validar confirmPassword porque no está en el DTO
                // Podrías pedir que se envíe como campo adicional en el JSON
                // O aceptar que la validación se haga solo en el formulario
                usuarioService.registrarUsuario(registroDto);
            }

            ctx.status(201).json(Map.of("mensaje", "Usuario creado exitosamente"));

        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", "No se pudo registrar el usuario: " + e.getMessage()));
        }
    };

    public Handler obtenerUsuario = ctx -> {
        String username = ctx.pathParam("username");
        System.out.println("===== Buscando usuario: " + username + " =====");
        Usuario usuario = usuarioService.obtenerPorUsername(username);

        if (usuario != null) {
            System.out.println("Devolviendo usuario");
            ctx.json(usuario);
        } else {
            ctx.status(404).json(Map.of("error", "Usuario no encontrado"));
        }
    };
}