package modelo;

import java.util.Random;
//import java.lang.Math;

public class GeneradorGrafo {
    Random random = new Random();

    final int TAM_MATRIZ = 12;
    int[][] matrizVertices = new int[TAM_MATRIZ][TAM_MATRIZ];
    int numeroVertices, numeroAristas;
    int[][] matrizCostos;
    posVertice[] posicionVertices;

    public GeneradorGrafo(){

    }

    public GeneradorGrafo(int[][] matrizVertices, int numeroVertices, int numeroAristas, 
                            int[][] matrizCostos, posVertice[] posicionVertices) 
    {
        this.matrizVertices = matrizVertices;
        this.numeroVertices = numeroVertices;
        this.numeroAristas = numeroAristas;
        this.matrizCostos = matrizCostos;
        this.posicionVertices = posicionVertices;
    }

    //indica que tipo de zona (vertice) habra en la matriz de vertices
    int tipoZona() {
        /* numero entre 0 y 2
         * 0 no hay zona
         * 1 zona no contaminada
         * 2 zona contaminada
         */
        int tZona = random.nextInt(3);
        return tZona;
    }

    boolean probabilidadAparecer(int probabilidad) {
        //numero entre 0 y 9
        int numero = random.nextInt(10);
        //representa la probabilidad que se busca tener
        if (numero < probabilidad) {
            return true;
        }
        return false;
    }

    //llena la matriz de vertices
    int[][] llenarMatriz() {
        //llenar la matriz de forma aleatoria
        for (int i = 0; i < TAM_MATRIZ; i++) {
            for (int j = 0; j < TAM_MATRIZ; j++) {
                //llenamos con un 60% de probabilidad
                if (probabilidadAparecer(2)){
                    matrizVertices[i][j] = tipoZona();
                }
            }
        }
        return matrizVertices;
    }

    // cuenta los vertices en la matriz de vertices
    int contarVertices() {
        numeroVertices = 0;
        //cuenta la cantidad de vertices generados
        for (int i = 0; i < TAM_MATRIZ; i++) {
            for (int j = 0; j < TAM_MATRIZ; j++) {
                if (matrizVertices[i][j] != 0) {
                    numeroVertices++;
                }
            }
        }
        return numeroVertices;
    }

    //muestra la matriz en consola
    void mostrarMatriz() {
        System.out.println("La matriz es: ");
        for (int i = 0; i < TAM_MATRIZ; i++) {
            for (int j = 0; j < TAM_MATRIZ; j++) {
                System.out.printf("%3d",matrizVertices[i][j]);       
            }
            System.out.println();
        }
    }

    //muestra la matriz en consola
    void mostrarMatrizCostos(int[][] matrizCostos) {
        System.out.println("La matriz de costos es: ");
        for (int i = 0; i < numeroVertices; i++) {
            for (int j = 0; j < numeroVertices; j++) {
                System.out.printf("%3d",matrizCostos[i][j]);       
            }
            System.out.println();
        }
    }

    //generar la matriz de costos en base a
    //la matriz de vertices
    int[][] generarMatrizCostos() {
        this.matrizCostos = new int[this.numeroVertices][this.numeroVertices];
        posVertice[] posicionVertices = new posVertice[this.numeroVertices];
        int aux = 0;
        //obtener las posiciones de los vertices
        for (int i = 0; i < TAM_MATRIZ; i++) {
            for (int j = 0; j < TAM_MATRIZ; j++) {
                if (matrizVertices[i][j] != 0) {
                    //guardamos la posicion del vertice, sus coordenadas
                    posicionVertices[aux] = new posVertice(i, j);
                    aux++;
                }
            }
        }
        //calculamos las distancias y llenamos la matrizDeCostos
        for (int i = 0; i < numeroVertices; i++) {
            for (int j = 0; j < numeroVertices; j++) {
                //hacemos cero a la diagonal principal
                if (i == j) {
                    matrizCostos[i][j] = 0;
                } else {
                    //llenamos con un 60% de probabilidad
                    if (probabilidadAparecer(6)) {
                        matrizCostos[i][j] = (int) calcularDistancia(posicionVertices[i], posicionVertices[j]);
                    } else {
                        matrizCostos[i][j] = 0;
                    }
                    
                    //matrizCostos[i][j] = (int) calcularDistancia(posicionVertices[i], posicionVertices[j]);
                }
            }
        }

        //cuenta la cantidad de aristas generadas
        for (int i = 0; i < numeroVertices; i++) {
            for (int j = 0; j < numeroVertices; j++) {
                if (matrizCostos[i][j] != 0) {
                    numeroAristas++;
                }
            }
        }
        return matrizCostos;
    }

    int[][] grafoDirigido(int[][] matrizCostos) {
        //manipular para no tener grafos dirigidos bidireccionales
        //recibimos una matriz con un 40% de aristas eliminadas (aprox)
        for (int i = 0; i < matrizCostos.length; i++) {
            for (int j = 0; j < matrizCostos.length; j++) {
                if (matrizCostos[i][j] != 0 && matrizCostos[j][i] != 0) {
                    //aca hacemos que una de las dos aristas entre dos vertices sea eliminada
                    if (probabilidadAparecer(5)) {
                        matrizCostos[i][j] = 0;
                    } else {
                        matrizCostos[j][i] = 0;
                    }
                }
            }
        }
        return matrizCostos;
    }

    int[][] grafoNoDirigido(int[][] matrizCostos) {
        //manipular para no tener grafos dirigidos
        //recibimos una matriz con un 40% de aristas eliminadas (aprox)
        for (int i = 0; i < matrizCostos.length; i++) {
            for (int j = 0; j < matrizCostos.length; j++) {
                if (matrizCostos[i][j] != 0 || matrizCostos[j][i] != 0) {
                    //aca hacemos que una de las dos aristas entre dos vertices sea añadida
                    if (matrizCostos[i][j] == 0) {
                        matrizCostos[i][j] = matrizCostos[j][i];
                    } else if (matrizCostos[j][i] == 0){
                        matrizCostos[j][i] = matrizCostos[i][j];
                    }
                }
            }
        }
        return matrizCostos;
    }

    double calcularDistancia(posVertice vertice1, posVertice vertice2) {
        double distanciaX, distanciaY, distancia;

        distanciaX = Math.abs(vertice1.x - vertice2.x);
        //System.out.println("DistanciaX: "+distanciaX);
        distanciaY = Math.abs(vertice1.y - vertice2.y);
        //System.out.println("DistanciaY: "+distanciaY);

        if (distanciaX != 0 && distanciaY != 0) {
            distancia = Math.hypot(distanciaX, distanciaY);
        } else {
            distancia = distanciaX + distanciaY;
        }

        return distancia;
    }

    public static void main(String[] args) {

        GeneradorGrafo g = new GeneradorGrafo();
        g.llenarMatriz();
        g.mostrarMatriz();
        System.out.println("Hay "+g.contarVertices()+" vertices");
        /*
        g.mostrarMatrizCostos(g.generarMatrizCostos());
        System.out.println("Hay "+g.numeroAristas+" aristas");
        System.out.println("Grafo no dirigido");
        g.mostrarMatrizCostos(g.grafoNoDirigido(g.matrizCostos));
        System.out.println("Grafo dirigido");
        g.mostrarMatrizCostos(g.grafoDirigido(g.matrizCostos));
        */
    }
}
