package agregador.service.normalizacion;

import java.util.ArrayList;
import java.util.List;

public class GeolocalizadorOffline {

    private static final List<Region> regiones = new ArrayList<>();

    static {
        regiones.add(new Region("Uruguay", -35.0, -30.0, -58.5, -53.0));
        regiones.add(new Region("Chile", -56.0, -17.5, -76.0, -69.0)); // Chile es largo, corta un poco los andes
        regiones.add(new Region("Paraguay", -27.6, -19.3, -62.7, -54.2));
        regiones.add(new Region("Bolivia", -23.0, -9.6, -69.6, -57.4));
        regiones.add(new Region("Brasil", -33.7, 5.2, -74.0, -34.7)); // Brasil es gigante, atrapa el resto al este

        regiones.add(new Region("CABA, Buenos Aires", -34.7, -34.5, -58.55, -58.35));

        regiones.add(new Region("Buenos Aires", -41.0, -33.0, -63.4, -56.6));
        regiones.add(new Region("Córdoba", -35.0, -29.5, -65.5, -61.7));
        regiones.add(new Region("Santa Fe", -34.4, -28.0, -63.0, -59.0));
        regiones.add(new Region("Mendoza", -37.6, -32.0, -70.5, -66.5));
        regiones.add(new Region("Tucumán", -28.0, -26.0, -66.0, -64.5));
        regiones.add(new Region("Salta", -26.5, -22.0, -68.0, -62.0));
        regiones.add(new Region("Misiones", -28.2, -25.5, -56.0, -53.6));

        regiones.add(new Region("Argentina (Ubicación General)", -55.0, -21.0, -73.5, -53.5));
    }

    public static String obtenerUbicacionAproximada(double lat, double lon) {
        for (Region region : regiones) {
            if (lat >= region.minLat && lat <= region.maxLat &&
                    lon >= region.minLon && lon <= region.maxLon) {
                return region.nombre + " (Aprox)";
            }
        }
        return "Argentina";
    }

    private static class Region {
        String nombre;
        double minLat, maxLat, minLon, maxLon;

        public Region(String nombre, double minLat, double maxLat, double minLon, double maxLon) {
            this.nombre = nombre;
            this.minLat = minLat;
            this.maxLat = maxLat;
            this.minLon = minLon;
            this.maxLon = maxLon;
        }
    }
}