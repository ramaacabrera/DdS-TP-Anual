package Estadisticas;

public class Contador {

    private String valor;
    private int contador;

    public Contador(String valor){
        this.valor = valor;
        this.contador = 1;
    }

    public String getValor() { return valor; }
    public int getContador() { return contador; }

    public void incrementar() { contador++; }
}
