package ApiPublica.Presentacion;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import utils.KeyCloak.TokenValidator;

import java.util.HashMap;
import java.util.Map;

public class GetHomeHandler implements Handler {
    public GetHomeHandler(){}

    @Override
    public void handle(@NotNull Context ctx){
        Map<String, Object> model = new HashMap<>();
        String access_token = ctx.sessionAttribute("access_token");
        if(access_token != null){
            try {
                TokenValidator.validar(access_token); // si es válido, sigue
                model.put("access_token", access_token);
            } catch (io.javalin.http.UnauthorizedResponse e) {
                ctx.status(401).result("Token inválido");
            } catch (Exception e) {
                ctx.status(500).result("Error interno");
            }
        }
        ctx.render("home.ftl", model);
    }
}
