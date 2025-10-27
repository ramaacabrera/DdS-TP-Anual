package ApiAdministrativa.Presentacion;

import utils.Persistencia.ColeccionRepositorio;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Handler;
import io.javalin.http.Context;
import utils.DTO.ColeccionDTO;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PostColeccionHandler implements Handler {
    private final ColeccionRepositorio coleccionRepositorio;

    public PostColeccionHandler(ColeccionRepositorio coleccionRepositorio) {
        this.coleccionRepositorio = coleccionRepositorio;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try{
            ColeccionDTO nueva = ctx.bodyAsClass(ColeccionDTO.class);

            coleccionRepositorio.guardar(nueva);

            ctx.status(201).result("Coleccion agregada exitosamente");
        } catch (Exception e) {
            ctx.status(500).result("Error interno: " + e.getMessage());
        }
    }
}

/*  //SIN OBJECT MAPPER NI BODYASCLASS
package ApiAdministrativa.Presentacion;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.rendering.template.TemplateUtil;
import org.jetbrains.annotations.NotNull;
import utils.DTO.ColeccionDTO;
import utils.Dominio.Criterios.*;
import utils.Dominio.HechosYColecciones.Coleccion;
import utils.Dominio.HechosYColecciones.TipoAlgoritmoConsenso;
import utils.Dominio.fuente.Fuente;
import utils.Persistencia.ColeccionRepositorio;

import java.util.*;
import java.util.stream.Collectors;

public class PostColeccionHandler implements Handler {

    private final ColeccionRepositorio coleccionRepositorio;

    public PostColeccionHandler(ColeccionRepositorio coleccionRepositorio) {
        this.coleccionRepositorio = coleccionRepositorio;
    }

    @Override
    public void handle(@NotNull Context ctx) {
        try {
            String titulo = ctx.formParam("titulo");
            String descripcion = ctx.formParam("descripcion");
            String algoritmoParam = ctx.formParam("algoritmo");
            TipoAlgoritmoConsenso algoritmo = TipoAlgoritmoConsenso.valueOf(algoritmoParam);

List<String> fuentesSeleccionadas = ctx.formParams("fuentes");
List<Fuente> fuentes = fuentesSeleccionadas.stream()
    .map(nombre -> new Fuente(TipoDeFuente.valueOf(nombre.toUpperCase()), "ruta/no-especificada"))
    .collect(Collectors.toList());

            }


            String tipoCriterio = ctx.formParam("criteriosDePertenencia");
            List<Criterio> criterios = new ArrayList<>();

            if (tipoCriterio != null) {
                switch (tipoCriterio) {
                    case "CriterioDeTexto":
                        String valorTexto = ctx.formParam("valorTexto");
                        criterios.add(new CriterioDeTexto(valorTexto));
                        break;

                    case "CriterioFecha":
                        String desde = ctx.formParam("fechaDesde");
                        String hasta = ctx.formParam("fechaHasta");
                        criterios.add(new CriterioFecha(desde, hasta));
                        break;

                    case "CriterioUbicacion":
                        String provincia = ctx.formParam("provincia");
                        criterios.add(new CriterioUbicacion(provincia));
                        break;

                    case "CriterioCategoria":
                        String categoria = ctx.formParam("categoria");
                        criterios.add(new CriterioCategoria(categoria));
                        break;

                    default:
                        // Otros tipos aún no implementados
                        break;
                }
            }


            Coleccion coleccion = new Coleccion(titulo, descripcion, criterios, algoritmo);
            coleccion.setFuentes(fuentes);


            coleccionRepositorio.guardar(coleccion);


            ctx.render("coleccion-creada.ftlh", TemplateUtil.model("coleccion", coleccion));

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Error al crear la colección: " + e.getMessage());
        }
    }
}
*/