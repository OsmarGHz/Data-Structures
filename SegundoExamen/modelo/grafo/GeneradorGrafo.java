package modelo.grafo;

import java.util.ArrayList;
import java.util.Random;
//import java.lang.Math;

public class GeneradorGrafo {
    Random random = new Random();

    public int TAM_MATRIZ = 12;
    public int[][] matrizVertices;
    public int numeroVertices, numeroAristas;
    public int[][] matrizCostos;
    public posVertice[] posicionVertices;
    public Aro[] posicionAristas;
    public boolean esDirigido;

    public java.util.ArrayList<java.util.ArrayList<Arista>> listaAdyacencia; // Lista de adyacencia de aristas ponderadas

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

    public Aro[] guardarPosAristas() {
        //guarda las posiciones de las aristas
        int aux = 0;
        for (int i = 0; i < numeroVertices; i++) {
            for (int j = 0; j < numeroVertices; j++) {
                if (matrizCostos[i][j] != 0) {
                    posicionAristas[aux] = new Aro(posicionVertices[i], posicionVertices[j], matrizCostos[i][j]);
                    aux++;
                }
            }
        }
        return posicionAristas;
    }
    //generar la matriz de costos en base a
    //la matriz de vertices
    public int[][] generarMatrizCostos() {
        // Inicializar lista de adyacencia
        listaAdyacencia.clear();
        for (int i = 0; i < numeroVertices; i++) {
            listaAdyacencia.add(new ArrayList<>());
        }

        // Primera pasada: conexiones aleatorias
        for (int i = 0; i < numeroVertices; i++) {
            for (int j = 0; j < numeroVertices; j++) {
                if (i != j && probabilidadAparecer(2)) {
                    int peso = (int) calcularDistancia(posicionVertices[i], posicionVertices[j]);
                    matrizCostos[i][j] = peso;
                    matrizCostos[j][i] = peso; // Para mantener la simetría en la matriz de costos
                    agregarArista(i, j, peso); // Nueva función optimizada
                }
            }
        }

        // Segunda pasada: garantizar al menos una arista saliente
        for (int i = 0; i < numeroVertices; i++) {
            if (listaAdyacencia.get(i).isEmpty()) {
                int otroVertice;
                do {
                    otroVertice = random.nextInt(numeroVertices);
                } while (otroVertice == i);
                
                int peso = (int) calcularDistancia(posicionVertices[i], posicionVertices[otroVertice]);
                matrizCostos[i][otroVertice] = peso;
                agregarArista(i, otroVertice, peso);
            }
        }
        return matrizCostos;
    }

    private void agregarArista(int origen, int destino, int peso) {
        // Verificar si la arista ya existe (para no duplicar)
        for (Arista arista : listaAdyacencia.get(origen)) {
            if (arista.destino == destino) {
                return;
            }
        }
        
        // Agregar arista en ambas direcciones si el grafo es no dirigido
        listaAdyacencia.get(origen).add(new Arista(destino, peso));

        listaAdyacencia.get(destino).add(new Arista(origen, peso));

    }

    //genera una matriz pero no guarda en la lista de adyacencia
    public int[][] generarMatrizCostos2() {
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

    public int[][] grafoNoPonderado (int[][] matrizCostos) {
        for (int i = 0; i < numeroVertices; i++) {
            for (int j = 0; j < numeroVertices; j++) { // Ahora recorremos toda la matriz
                if (i != j && matrizCostos[i][j] != 0) { // Evitar auto-conexiones
                    matrizCostos[i][j] = 1;
                }
            }
        }
        return matrizCostos;
    }

    public int[][] grafoDirigido(int[][] matrizCostos) {
        esDirigido = true;
        //manipular para no tener grafos dirigidos bidireccionales
        //recibimos una matriz con un 40% de aristas eliminadas (aprox)
        //eliminar los valores triangulares inferiores
        for (int i = 0; i < matrizCostos.length; i++) {
            for (int j = 0; j < matrizCostos.length - (matrizCostos.length - i); j++) {
                //pasar los elementos de la parte triangular inferior a la superior
                if (matrizCostos[i][j] != 0 && matrizCostos[j][i] == 0) {
                    matrizCostos[j][i] = matrizCostos[i][j];
                }
                matrizCostos[i][j] = 0;
            }
        }
        return matrizCostos;
    }

    public int[][] grafoDirigido2(int[][] matrizCostos) {
        esDirigido = true;
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
        esDirigido = false;
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
            if (i == 0) {
                System.out.printf("%6d",i);
            } else {
                System.out.printf("%3d",i);
            }
            if (i == numeroVertices - 1) System.out.println();
        }
        for (int i = 0; i < numeroVertices; i++) {
            if (i == 0) {
                System.out.printf("%6s","-");
            } else {
                System.out.printf("%3s","---");
            }
            if (i == numeroVertices - 1) System.out.println();
        }
        /*
        for (int i = 0; i < numeroVertices; i++) {
            System.out.printf("-");
            if (i == numeroVertices - 1) System.out.println();    
        }
            */
        for (int i = 0; i < numeroVertices; i++) {
            System.out.printf("%2d|", i);
            for (int j = 0; j < numeroVertices; j++) {
                System.out.printf("%3d",matrizCostos[i][j]);       
            }
            System.out.println();
        }
    }

    public void generarGrafoConexo() {
        listaAdyacencia.clear();
        for (int i = 0; i < numeroVertices; i++) {
            listaAdyacencia.add(new ArrayList<>());
        }
    
        boolean[] visitado = new boolean[numeroVertices];
        ArrayList<Integer> noVisitados = new ArrayList<>();
        for (int i = 0; i < numeroVertices; i++) noVisitados.add(i);
    
        // Escoge un nodo inicial al azar
        int actual = random.nextInt(numeroVertices);
        visitado[actual] = true;
        noVisitados.remove(Integer.valueOf(actual));
    
        while (!noVisitados.isEmpty()) {
            int siguiente = noVisitados.get(random.nextInt(noVisitados.size()));
    
            // Encuentra un nodo ya conectado para unirlo
            ArrayList<Integer> conectados = new ArrayList<>();
            for (int i = 0; i < numeroVertices; i++) {
                if (visitado[i]) conectados.add(i);
            }
    
            int destino = conectados.get(random.nextInt(conectados.size()));
    
            int peso = (int) calcularDistancia(posicionVertices[siguiente], posicionVertices[destino]);
            matrizCostos[siguiente][destino] = peso;
            matrizCostos[destino][siguiente] = peso;
            agregarArista(siguiente, destino, peso);
    
            visitado[siguiente] = true;
            noVisitados.remove(Integer.valueOf(siguiente));
        }
    
        // Opcional: añadir más aristas aleatorias para "rellenar"
        for (int i = 0; i < numeroVertices; i++) {
            for (int j = i + 1; j < numeroVertices; j++) {
                if (matrizCostos[i][j] == 0 && probabilidadAparecer(10)) { // probabilidad de arista extra
                    int peso = (int) calcularDistancia(posicionVertices[i], posicionVertices[j]);
                    matrizCostos[i][j] = peso;
                    matrizCostos[j][i] = peso;
                    agregarArista(i, j, peso);
                }
            }
        }
    }
    
    private void inicializarGrafo() {
        matrizVertices = new int[TAM_MATRIZ][TAM_MATRIZ]; // Inicializa matriz de vértices
    
        llenarMatriz(); // Llena la matriz con zonas aleatorias
        numeroVertices = contarVertices(); // Cuenta los vértices creados
    
        // Asegura que haya al menos 2 vértices para poder generar un grafo conexo
        if (numeroVertices < 2) {
            // Puedes forzar la creación de al menos 2 vértices si quieres
            matrizVertices[random.nextInt(TAM_MATRIZ)][random.nextInt(TAM_MATRIZ)] = tipoZona();
            matrizVertices[random.nextInt(TAM_MATRIZ)][random.nextInt(TAM_MATRIZ)] = tipoZona();
            numeroVertices = contarVertices();
        }
    
        posicionVertices = new posVertice[numeroVertices];
        guardarPosVertices(); // Guarda posiciones (x, y) de cada vértice
    
        listaAdyacencia = new ArrayList<>(numeroVertices);
        matrizCostos = new int[numeroVertices][numeroVertices];
    
        generarGrafoConexo(); // ← ¡Aquí garantizamos que el grafo sea conexo!
    
        numeroAristas = contarAristas();
        posicionAristas = new Aro[numeroAristas];
        guardarPosAristas();
    
        mostrarMatriz(); // Opcional: muestra la matriz de vértices (no la de costos)
    }
    
    
    public static void main(String[] args) {

        new GeneradorGrafo();
        //g.inicializarGrafo();
        
    }
    
}