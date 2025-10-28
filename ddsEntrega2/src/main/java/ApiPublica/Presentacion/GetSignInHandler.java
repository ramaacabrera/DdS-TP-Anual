package ApiPublica.Presentacion;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GetSignInHandler implements Handler {
    public GetSignInHandler(){}

    @Override
    public void handle(@NotNull Context ctx){
        Map<String, Object> model = new HashMap<>();
        model.put("error", ctx.queryParam("error"));
        ctx.render("sign-in.ftl", model);
    }
}
