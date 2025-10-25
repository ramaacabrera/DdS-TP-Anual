package agregador.PaqueteNormalizador;
import agregador.HechosYColecciones.Hecho;

public class MockNormalizador {

    public MockNormalizador(){}

    public Hecho normalizar(Hecho hecho) {
        //System.out.println("Empezando a normalizar");
        if (hecho == null) {
            return null;
        }

        // Crear una copia para no modificar el original
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

        // Normalizar categoría
        String categoriaNormalizada = CategoriaNormalizador.normalizarCategoria(hecho.getCategoria());
        hechoNormalizado.setCategoria(categoriaNormalizada);

        // Normalizar título (eliminar espacios extras)
        if (hecho.getTitulo() != null) {
            String tituloNormalizado = hecho.getTitulo().trim()
                    .replaceAll("\\s+", " ");
            hechoNormalizado.setTitulo(tituloNormalizado);
        }

        // Normalizar descripción
        if (hecho.getDescripcion() != null) {
            String descripcionNormalizada = hecho.getDescripcion().trim()
                    .replaceAll("\\s+", " ");
            hechoNormalizado.setDescripcion(descripcionNormalizada);
        }

        // NO HICIMOS NADA CON LAS FECHAS PORQUE SON TODOS TIPO DATE

        return hechoNormalizado;
    }
}

