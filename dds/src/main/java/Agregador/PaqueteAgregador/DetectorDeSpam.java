package Agregador.PaqueteAgregador;

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
            "sin impuestos", "sólo hoy", "legalmente", "sin esfuerzo", "cuenta suspendida", "free",
            "win", "winner", "money", "cash", "click here", "offer", "prize", "credit",
            "discount", "buy now", "limited time", "act now", "guaranteed", "no cost", "enter your details",
            "congratulations", "claim now", "no obligation", "instant access", "get rich", "inheritance",
            "bonus", "draw", "credit card", "alert", "bank", "pending invoice", "verify account",
            "confirm account", "password", "risk", "last chance", "claim your prize", "best price",
            "easy money", "quick money", "luxury", "cryptocurrency", "no taxes",
            "only today", "legally", "no effort", "account suspended",
            "urgent", "limited offer", "urgent action required", "exclusive deal",
            "order now", "save big", "act fast", "cheap", "lowest price", "miracle", "investment opportunity",
            "investment", "risk free", "unsubscribe", "lose weight", "work from home", "make money fast",
            "get paid", "earn extra", "double your", "clearance", "special promotion", "hot deal",
            "secret", "trial", "deal ends soon", "today only", "winner!", "earn cash", "act immediately"
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
