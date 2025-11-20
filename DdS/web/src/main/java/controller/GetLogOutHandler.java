package controller;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class GetLogOutHandler implements Handler {
    public GetLogOutHandler() {}

    @Override
    public void handle(@NotNull Context ctx) {
        ctx.sessionAttribute("username", null);
        ctx.sessionAttribute("access_token", null);
        ctx.status(200);
    }
}
