package web.utils;

import io.javalin.http.Context;
import java.util.HashMap;
import java.util.Map;

public class ViewUtil {

    public static Map<String, Object> baseModel(Context ctx) {

        String rol = ctx.sessionAttribute("rolUsuario");
        if(rol == null){
            rol = "VISITANTE";
        }
        Map<String, Object> model = new HashMap<>();
        model.put("username", ctx.sessionAttribute("username"));
        model.put("accessToken", ctx.sessionAttribute("accessToken"));
        model.put("rolUsuario", rol);

        System.out.println("baseModel: " + rol);
        return model;
    }
}