package modelo;

public class Arista {
    public posVertice origen, destino;
    public int peso;
    
    public Arista(posVertice posicionVertices, posVertice posicionVertices2, int peso) {
        this.origen = posicionVertices;
        this.destino = posicionVertices2;
        this.peso = peso;
    }
}
