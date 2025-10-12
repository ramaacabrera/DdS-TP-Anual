package Estadisticas;

public class Contador<T> {

    private T valor;
    private int contador;

    public Contador(T valor){
        this.valor = valor;
        this.contador = 1;
    }

    public T getValor() { return valor; }
    public int getContador() { return contador; }

    public void incrementar() { contador++; }
}
