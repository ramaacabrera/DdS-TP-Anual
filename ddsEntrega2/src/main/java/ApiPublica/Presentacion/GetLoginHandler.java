package ApiPublica.Presentacion;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GetLoginHandler implements Handler {
    public GetLoginHandler(){}

    @Override
    public void handle(@NotNull Context ctx){
        Map<String, Object> model = new HashMap<>();
        String error = ctx.sessionAttribute("error");
        if(error != null){
            model.put("error", error);
            ctx.sessionAttribute("error", null);
        }
        ctx.render("login.ftl", model);
    }
}
