package web.service.Normalizador;
import utils.Dominio.HechosYColecciones.Hecho;

public class MockNormalizador {

    public MockNormalizador(){}

    public Hecho normalizar(Hecho hecho) {
        if (hecho == null) {
            return null;
        }

        Hecho hechoNormalizado = new Hecho();
        hechoNormalizado.setTitulo(hecho.getTitulo());
        hechoNormalizado.setDescripcion(hecho.getDescripcion());
        hechoNormalizado.setCategoria(hecho.getCategoria());
        hechoNormalizado.setUbicacion(hecho.getUbicacion());
        hechoNormalizado.setFechaDeAcontecimiento(hecho.getFechaDeAcontecimiento());
        hechoNormalizado.setFechaDeCarga(hecho.getFechaDeCarga());
        hechoNormalizado.setFuente(hecho.getFuente());
        hechoNormalizado.setEstadoHecho(hecho.getEstadoHecho());
        hechoNormalizado.setContribuyente(hecho.getContribuyente());
        hechoNormalizado.setEtiquetas(hecho.getEtiquetas());
        hechoNormalizado.setEsEditable(hecho.esEditable());
        hechoNormalizado.setContenidoMultimedia(hecho.getContenidoMultimedia());

        String categoriaNormalizada = NormalizadorCategorias.normalizar(hecho.getCategoria());
        hechoNormalizado.setCategoria(categoriaNormalizada);

        if (hecho.getTitulo() != null) {
            String tituloNormalizado = hecho.getTitulo().trim()
                    .replaceAll("\\s+", " ");
            hechoNormalizado.setTitulo(tituloNormalizado);
        }

        if (hecho.getDescripcion() != null) {
            String descripcionNormalizada = hecho.getDescripcion().trim()
                    .replaceAll("\\s+", " ");
            hechoNormalizado.setDescripcion(descripcionNormalizada);
        }

        return hechoNormalizado;
    }
}

