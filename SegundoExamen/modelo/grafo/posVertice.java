package modelo.grafo;

public class posVertice {
    public int x;
    public int y;
    public int tipoZona;
    
    public posVertice(int x, int y, int tipoZona) {
        this.x = x;
        this.y = y;
        this.tipoZona = tipoZona;
    }

    // Sobreescribimos equals y hashCode para usar en mapas y listas sin problemas
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof posVertice)) return false;
        posVertice other = (posVertice) obj;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}
