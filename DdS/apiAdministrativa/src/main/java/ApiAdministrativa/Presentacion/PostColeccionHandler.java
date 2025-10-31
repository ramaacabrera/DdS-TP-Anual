package ApiAdministrativa.Presentacion;

import utils.Dominio.Criterios.Criterio;
import utils.Dominio.HechosYColecciones.Coleccion;
import utils.Dominio.HechosYColecciones.Hecho;
import utils.Persistencia.ColeccionRepositorio;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Handler;
import io.javalin.http.Context;
import utils.DTO.ColeccionDTO;
import org.jetbrains.annotations.NotNull;
import utils.Persistencia.HechoRepositorio;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class PostColeccionHandler implements Handler {
    private final ColeccionRepositorio coleccionRepositorio;
    private final HechoRepositorio hechoRepositorio;

    public PostColeccionHandler(ColeccionRepositorio coleccionRepositorio, HechoRepositorio hechoRepositorioNuevo) {
        this.coleccionRepositorio = coleccionRepositorio;
        this.hechoRepositorio = hechoRepositorioNuevo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {

        System.out.println("Iniciando creacion de coleccion");
        try{

            ColeccionDTO nueva = ctx.bodyAsClass(ColeccionDTO.class);

            Coleccion coleccion = new Coleccion(nueva);
            List<Criterio> criterios = coleccion.getCriteriosDePertenencia();
            List<Hecho> hechos = hechoRepositorio.getHechos();
            List<Hecho> hechosQueCumplen = new ArrayList<>();

            for(Hecho hecho : hechos){
                boolean cumpleTodosLosCriterios = true;
                for(Criterio criterio : criterios) {
                    if (!criterio.cumpleConCriterio(hecho)) {
                        cumpleTodosLosCriterios = false;
                        break;
                    }
                }
                if (cumpleTodosLosCriterios) {
                    hechosQueCumplen.add(hecho);
                }
            }
            coleccion.setHechos(hechosQueCumplen);
            coleccionRepositorio.guardar(coleccion);

            System.out.println("Coleccion guardado: " + nueva);

            ctx.status(201).result("Coleccion agregada exitosamente");
        } catch (Exception e) {
            ctx.status(500).result("Error interno: " + e.getMessage());
            System.out.println("Error interno: " + e.getMessage());
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