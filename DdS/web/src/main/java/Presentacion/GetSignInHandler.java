package Presentacion;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GetSignInHandler implements Handler {
    private String urlPublica;

    public GetSignInHandler(String urlPublica) {this.urlPublica = urlPublica;}

    @Override
    public void handle(@NotNull Context ctx){
        Map<String, Object> model = new HashMap<>();
        model.put("baseAPIUrl", urlPublica);
        String error = ctx.sessionAttribute("error");
        if(error != null){
            model.put("error", error);
            ctx.sessionAttribute("error", null);
        }
        ctx.render("sign-in.ftl", model);
    }
}
