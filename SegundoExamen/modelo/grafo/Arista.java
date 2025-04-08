package modelo.grafo;

public class Arista {
    public int origen;   // Nuevo campo para el origen (opcional)
    public int destino;
    public int peso;

    // Constructor original (usado para representar aristas del grafo)
    public Arista(int destino, int peso) {
        this.destino = destino;
        this.peso = peso;
    }

    // Nuevo constructor para el MST que almacena origen, destino y peso
    public Arista(int origen, int destino, int peso) {
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
    }
}
