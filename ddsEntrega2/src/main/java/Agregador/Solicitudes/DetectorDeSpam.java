package Agregador.Solicitudes;

import java.util.Arrays;
import java.util.List;

public class DetectorDeSpam {

    private static final List<String> PALABRAS_SPAM = Arrays.asList(
            "gratis", "gana", "dinero", "click", "oferta", "premio", "crédito", "descuento",
            "compra ahora", "haz clic", "100% gratis", "limitado", "mejora tu", "sin costo",
            "ingresá tus datos", "ganador", "recibe ahora", "sin compromiso", "acceso inmediato",
            "multiplica tus ingresos", "transferencia", "herencia", "bono", "sorteo",
            "tarjeta de crédito", "alerta", "banco", "factura pendiente", "necesitamos verificar",
            "confirmar cuenta", "contraseña", "riesgo", "última oportunidad", "reclama tu premio",
            "mejor precio", "dinero fácil", "dinero rápido", "lujo", "bitcoin", "criptomoneda",
            "sin impuestos", "sólo hoy", "legalmente", "sin esfuerzo", "cuenta suspendida"
    );

    public static boolean esSpam(String cadenaAEvaluar){
        String textoNormalizado = cadenaAEvaluar.toLowerCase();
        int coincidencia = 0;
        for (String s : PALABRAS_SPAM) {
            if (textoNormalizado.contains(s)) {
                coincidencia++;
                if (coincidencia >= 2) return true;
            }
        }
        return false;
    }
}
