package modelo;

import java.util.Random;
//import java.lang.Math;

public class GeneradorGrafo {
    Random random = new Random();

    public int TAM_MATRIZ = 8;
    public int[][] matrizVertices;
    public int numeroVertices, numeroAristas;
    public int[][] matrizCostos;
    public posVertice[] posicionVertices;
    public Arista[] posicionAristas;

    public GeneradorGrafo(){
        inicializarGrafo();
    }

    //indica que tipo de zona (vertice) habra en la matriz de vertices
    public int tipoZona() {
        /* numero entre 0 y 2
         * 0 no hay zona
         * 1 zona no contaminada
         * 2 zona contaminada
         */
        int tZona = random.nextInt(3);
        return tZona;
    }

    public boolean probabilidadAparecer(int probabilidad) {
        //numero entre 0 y 9
        int numero = random.nextInt(100);
        //representa la probabilidad que se busca tener
        if (numero < probabilidad) {
            return true;
        }
        return false;
    }

    //llena la matriz de vertices
    public int[][] llenarMatriz() {
        //llenar la matriz de forma aleatoria
        for (int i = 0; i < TAM_MATRIZ; i++) {
            for (int j = 0; j < TAM_MATRIZ; j++) {
                //llenamos con un 60% de probabilidad
                if (probabilidadAparecer(40)){
                    matrizVertices[i][j] = tipoZona();
                }
            }
        }
        //pendiente: añadir una condicion por si no se genera ningun vertice
        /*
         * if (numeroVertices == 0) {
            matrizVertices[random.nextInt(TAM_MATRIZ)][random.nextInt(TAM_MATRIZ)] = tipoZona();
            }
         */
        return matrizVertices;
    }

    // cuenta los vertices en la matriz de vertices
    public int contarVertices() {
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

    public int contarAristas() {
        //cuenta la cantidad de aristas generadas
        numeroAristas = 0;
        for (int i = 0; i < numeroVertices; i++) {
            for (int j = 0; j < numeroVertices; j++) {
                if (matrizCostos[i][j] != 0) {
                    numeroAristas++;
                }
            }
        }
        return numeroAristas;
    }

    public posVertice[] guardarPosVertices(){
        int aux = 0;
        //obtener las posiciones de los vertices
        for (int i = 0; i < TAM_MATRIZ; i++) {
            for (int j = 0; j < TAM_MATRIZ; j++) {
                if (matrizVertices[i][j] != 0) {
                    //guardamos la posicion del vertice, sus coordenadas y su valor
                    posicionVertices[aux] = new posVertice(i, j, matrizVertices[i][j]);
                    aux++;
                }
            }
        }
        return posicionVertices;
    }

    public Arista[] guardarPosAristas() {
        //guarda las posiciones de las aristas
        int aux = 0;
        for (int i = 0; i < numeroVertices; i++) {
            for (int j = 0; j < numeroVertices; j++) {
                if (matrizCostos[i][j] != 0) {
                    posicionAristas[aux] = new Arista(posicionVertices[i], posicionVertices[j], matrizCostos[i][j]);
                    aux++;
                }
            }
        }
        return posicionAristas;
    }
    //generar la matriz de costos en base a
    //la matriz de vertices
    public int[][] generarMatrizCostos() {
        // Primera pasada: generar conexiones con una probabilidad del 50%
        for (int i = 0; i < numeroVertices; i++) {
            for (int j = 0; j < numeroVertices; j++) { // Ahora recorremos toda la matriz
                if (i != j && probabilidadAparecer(2)) { // Evitar auto-conexiones
                    matrizCostos[i][j] = (int) calcularDistancia(posicionVertices[i], posicionVertices[j]);
                }
            }
        }

        // Segunda pasada: asegurar que todos los vértices tengan al menos una arista saliente
        for (int i = 0; i < numeroVertices; i++) {
            boolean tieneAristaSaliente = false;
            for (int j = 0; j < numeroVertices; j++) {
                if (matrizCostos[i][j] != 0) {
                    tieneAristaSaliente = true;
                    break;
                }
            }
            if (!tieneAristaSaliente) {
                // Si un vértice no tiene aristas salientes, conectar con otro aleatorio
                int otroVertice;
                do {
                    otroVertice = random.nextInt(numeroVertices);
                } while (otroVertice == i); // Asegurar que no se conecte consigo mismo

                matrizCostos[i][otroVertice] = (int) calcularDistancia(posicionVertices[i], posicionVertices[otroVertice]);
            }
        }
        return matrizCostos;
    }

    public int[][] grafoDirigido(int[][] matrizCostos) {
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

    public int[][] grafoNoDirigido(int[][] matrizCostos) {
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

    public double calcularDistancia(posVertice vertice1, posVertice vertice2) {
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

    //muestra la matriz en consola
    public void mostrarMatriz() {
        System.out.println("La matriz es: ");
        for (int i = 0; i < TAM_MATRIZ; i++) {
            for (int j = 0; j < TAM_MATRIZ; j++) {
                System.out.printf("%3d",matrizVertices[i][j]);       
            }
            System.out.println();
        }
    }

    //muestra la matriz en consola
    public void mostrarMatrizCostos() {
        System.out.println("La matriz de costos es: ");
        for (int i = 0; i < numeroVertices; i++) {
            for (int j = 0; j < numeroVertices; j++) {
                System.out.printf("%3d",matrizCostos[i][j]);       
            }
            System.out.println();
        }
    }

    private void inicializarGrafo() {
        matrizVertices = new int[TAM_MATRIZ][TAM_MATRIZ]; // Asegura que la matriz se inicializa
        
        llenarMatriz();
        numeroVertices = contarVertices();
        posicionVertices = new posVertice[numeroVertices];
        guardarPosVertices();
        
        matrizCostos = new int[numeroVertices][numeroVertices];

        matrizCostos = generarMatrizCostos();
        numeroAristas = contarAristas();
        posicionAristas = new Arista[numeroAristas];
        guardarPosAristas();
        mostrarMatriz();
        //mostrarMatrizCostos();
        //grafoDirigido(matrizCostos);
        grafoDirigido(matrizCostos);
        //mostrarMatrizCostos();
    }
    
    public static void main(String[] args) {

        new GeneradorGrafo();
        //g.inicializarGrafo();
        
    }
    
}