package web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.javalin.http.Handler;
import web.dto.Hechos.HechoDTO;
import web.dto.PageDTO;
import web.domain.Usuario.Usuario;
import web.utils.ViewUtil;

public class GetHomeHandler implements Handler {
    private String urlPublica;
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private static final int PAGE_DEFAULT = 1;
    private static final int SIZE_MAP_LIMIT = 100;

    public GetHomeHandler(String urlPublica) {this.urlPublica = urlPublica;}

    @Override
    public void handle(@NotNull Context ctx) {
        String urlBackend= urlPublica+"/hechos?page="+PAGE_DEFAULT+"&size="+SIZE_MAP_LIMIT;

        List<HechoDTO> hechos = Collections.emptyList();
        PageDTO<HechoDTO> pageDto = null;

        try {
            Request request = new Request.Builder().url(urlBackend).get().build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Error al llamar al backend: " + response.code() + " " + response.message());
            }

            pageDto = mapper.readValue(response.body().string(), new TypeReference<PageDTO<HechoDTO>>() {});
            hechos = pageDto.content != null ? pageDto.content : Collections.emptyList();
            System.out.println(pageDto.content.size());

        } catch (Exception e) {
            System.err.println("Error de red/IO al consultar el backend: " + e.getMessage());
        }

        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("hechos", hechos);

        model.put("total", pageDto != null ? pageDto.totalElements : 0);
        model.put("page", PAGE_DEFAULT);
        model.put("size", SIZE_MAP_LIMIT);
        model.put("totalPages", pageDto != null ? pageDto.totalPages : 1);

        ctx.render("inicio.ftl", model);
    }
}
