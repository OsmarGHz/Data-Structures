package modelo.grafo;

public class Aro {
    public posVertice origen, destino;
    public int peso;
    
    public Aro(posVertice posicionVertices, posVertice posicionVertices2, int peso) {
        this.origen = posicionVertices;
        this.destino = posicionVertices2;
        this.peso = peso;
    }

    public Aro(posVertice origen, posVertice destino) {
        this(origen, destino, 1);
    }
}
