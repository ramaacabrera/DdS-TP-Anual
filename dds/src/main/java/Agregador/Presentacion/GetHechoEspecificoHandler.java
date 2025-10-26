package Agregador.Presentacion;

import Agregador.HechosYColecciones.Hecho;
import Agregador.Persistencia.HechoRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.rendering.template.TemplateUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class GetHechoEspecificoHandler implements Handler {

    private final HechoRepositorio hechoRepositorio;

    public GetHechoEspecificoHandler(HechoRepositorio repo) {
        this.hechoRepositorio = repo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        // 1. Obtener el ID del hecho de la URL (ruta: /hechos/{id})
        String hechoIdString = ctx.pathParam("id");

        // 2. Buscar la entidad Hecho en la base de datos
        Hecho hecho = hechoRepositorio.buscarPorId(hechoIdString);

        if (hecho == null) {
            // Si no se encuentra el hecho, devolver un error 404
            ctx.status(404).result("Error 404: Hecho con ID " + hechoIdString + " no encontrado.");
            return;
        }

        //  Crear el modelo de datos para FreeMarker
        Map<String, Object> modelo = TemplateUtil.model("hecho", hecho);

        //  Renderizar la plantilla (SSR)
        ctx.render("hecho-especifico.ftlh", modelo);
    }
}
/*function handleAction(actionType, hechoId) {
            switch (actionType) {
                case 'MODIFICAR'://chau?
                    // Redirigir a una página o modal de formulario de solicitud
                    alert(`Iniciando solicitud de modificación para el Hecho: ${hechoId}`);
                    // window.location.href = '/solicitudes/modificar/' + hechoId;
                    break;
                case 'REPORTAR':
                    // Redirigir a una páginaeliminación
                    alert(`Iniciando solicitud de ELIMINACIÓN para el Hecho: ${hechoId}`);
                    // window.location.href = '/solicitudes/eliminar/' + hechoId;
                    break;
                case 'COMPARTIR':
                    console.log('Compartir presionado');
                    const urlToCopy = window.location.href;
                    alert('Enlace copiado al portapapeles!');
                    navigator.clipboard.writeText(urlToCopy).then(() => {
                        alert('✅ Enlace copiado al portapapeles: ' + urlToCopy);
                        console.log('Enlace copiado: ' + urlToCopy);
                    }).catch(err => {
                        alert('❌ Error al copiar el enlace. ¡Intenta copiarlo manualmente!');
                        console.error('Error al copiar: ', err);
                    });
                    break;
            }
        }*/