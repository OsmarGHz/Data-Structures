#include <stdio.h>
#include <stdlib.h>

#define MAX_NODOS 100 // Número máximo de nodos

typedef struct {
    int * datos;      // Array dinámico para almacenar los elementos
    int capacidad;   // Capacidad actual del array
    int tamano;      // Número actual de elementos en la cola
} Cola;

// Estructura del nodo para la lista de adyacencia
typedef struct Nodo {
    int vertice; // dato que guarda el nodo
    struct Nodo * siguiente; // nodos adyacentes
} Nodo;

// Estructura del grafo usando lista de adyacencia
typedef struct Grafo {
    int numVertices; // número de vértices que tendrá la gráfica
    Nodo * listasAdyacencia[MAX_NODOS]; // lista de adyacencia para cada vértice
    int visitado[MAX_NODOS]; // bandera para nodos visitados
} Grafo;

void limpiarBuffer(){
    char c;
    while ((c = getchar()) != '\n' && c != EOF);
}

int escaneoEntero(int * variable){
    if (scanf("%d", variable) != 1){
        printf("Entrada invalida. Ingrese un numero: ");
        limpiarBuffer();
        return 0;
    }
    return 1;
}

// Función para crear una cola con capacidad inicial
Cola * crearCola(int capacidadInicial) {
    Cola * cola = (Cola *)malloc(sizeof(Cola));
    if (!cola) {
        fprintf(stderr, "Error al asignar memoria para la cola.\n");
        exit(EXIT_FAILURE);
    }
    cola -> datos = (int *)malloc(capacidadInicial * sizeof(int));
    if (!cola -> datos) {
        free(cola);
        fprintf(stderr, "Error al asignar memoria para los elementos.\n");
        exit(EXIT_FAILURE);
    }
    cola -> capacidad = capacidadInicial;
    cola -> tamano = 0;
    return cola;
}

// Función para redimensionar la cola (duplicar la capacidad)
void redimensionarCola(Cola * cola) {
    int nuevaCapacidad = cola -> capacidad * 2;
    int * nuevosDatos = (int *)realloc(cola->datos, nuevaCapacidad * sizeof(int));
    if (!nuevosDatos) {
        fprintf(stderr, "Error al redimensionar la cola.\n");
        exit(EXIT_FAILURE);
    }
    cola -> datos = nuevosDatos;
    cola -> capacidad = nuevaCapacidad;
    //printf("Cola redimensionada a capacidad %d.\n", nuevaCapacidad);
}

// Función para encolar (agregar) un elemento a la cola
void encolar(Cola * cola, int elemento) {
    if (cola -> tamano == cola -> capacidad) {
        redimensionarCola(cola);
    }
    cola -> datos[cola -> tamano] = elemento;
    cola -> tamano++;
    //printf("Elemento %d encolado.\n", elemento);
}

// Función para desencolar (eliminar) el primer elemento de la cola
int desencolar(Cola * cola) {
    if (cola -> tamano == 0) {
        fprintf(stderr, "La cola está vacía. No se puede desencolar.\n");
        exit(EXIT_FAILURE);
    }
    int elemento = cola -> datos[0];
    // Desplazar los elementos a la izquierda
    for (int i = 1; i < cola -> tamano; i++) {
        cola -> datos[i - 1] = cola -> datos[i];
    }
    cola -> tamano--;
    return elemento;
}

// Función para mostrar los elementos de la cola
void mostrarCola(Cola * cola) {
    if (cola -> tamano == 0) {
        printf("La cola está vacía.\n");
        return;
    }
    printf("Elementos de la cola: ");
    for (int i = 0; i < cola -> tamano; i++) {
        printf("%d ", cola -> datos[i]);
    }
    printf("\n");
}

// Función para liberar la memoria de la cola
void liberarCola(Cola * cola) {
    free(cola -> datos);
    free(cola);
}

// Funciones del grafo
Grafo * crearGrafo(int vertices) {
    Grafo * grafo = (Grafo *) malloc(sizeof(Grafo)); // reserva memoria para el grafo
    grafo -> numVertices = vertices; // asigna el número de vértices
    for (int i = 0; i < vertices; i++) {
        grafo -> listasAdyacencia[i] = NULL;
        grafo -> visitado[i] = 0;
    }
    return grafo;
}

Nodo * crearNodo(int vertice) {
    Nodo * nuevoNodo = (Nodo *)malloc(sizeof(Nodo)); // reserva espacio en memoria
    nuevoNodo -> vertice = vertice; // asigna el vértice al nodo
    nuevoNodo -> siguiente = NULL; // el nuevo nodo apunta a NULL
    return nuevoNodo;
}

void agregarArista(Grafo * grafo, int origen, int destino) {
    // Agregar arista de origen a destino
    Nodo * nuevoNodo = crearNodo(destino);
    nuevoNodo -> siguiente = grafo -> listasAdyacencia[origen];
    grafo -> listasAdyacencia[origen] = nuevoNodo;
    // Agregar la arista inversa para grafo no dirigido
    nuevoNodo = crearNodo(origen);
    nuevoNodo -> siguiente = grafo -> listasAdyacencia[destino];
    grafo -> listasAdyacencia[destino] = nuevoNodo;
}

void mostrarListaAdyacencia(Grafo * grafo) {
    printf("Lista de adyacencia:\n");
    for (int i = 0; i < grafo -> numVertices; i++) {
        printf("%d: ", i);
        Nodo * temp = grafo -> listasAdyacencia[i];
        while (temp) {
            printf("%d -> ", temp -> vertice);
            temp = temp -> siguiente;
        }
        printf("NULL\n");
    }
}

void eliminarArista(Grafo * grafo, int origen, int destino) {
    Nodo * actual, * anterior;
    // Eliminar de la lista de 'origen' el nodo que tenga 'destino'
    actual = grafo -> listasAdyacencia[origen];
    anterior = NULL;
    while (actual != NULL && actual -> vertice != destino) {
        anterior = actual;
        actual = actual -> siguiente;
    }
    if (actual != NULL) {
        if (anterior == NULL) {
            grafo -> listasAdyacencia[origen] = actual -> siguiente;
        } else {
            anterior -> siguiente = actual -> siguiente;
        }
        free(actual);
        printf("Arista eliminada de %d a %d.\n", origen, destino);
    } else {
        printf("No se encontro la arista de %d a %d.\n", origen, destino);
    }
    // Eliminar de la lista de 'destino' el nodo que tenga 'origen'
    actual = grafo -> listasAdyacencia[destino];
    anterior = NULL;
    while (actual != NULL && actual -> vertice != origen) {
        anterior = actual;
        actual = actual -> siguiente;
    }
    if (actual != NULL) {
        if (anterior == NULL) {
            grafo -> listasAdyacencia[destino] = actual->siguiente;
        } else {
            anterior -> siguiente = actual->siguiente;
        }
        free(actual);
    }
}

void eliminarGrafo(Grafo * grafo) {
    for (int i = 0; i < grafo -> numVertices; i++) {
        Nodo * actual = grafo -> listasAdyacencia[i];
        while (actual != NULL) {
            Nodo * temp = actual;
            actual = actual -> siguiente;
            free(temp);
        }
    }
    free(grafo);
    printf("Grafo eliminado.\n");
}

void reiniciarVisitado(Grafo * grafo) {
    for (int i = 0; i < grafo -> numVertices; i++) {
        grafo -> visitado[i] = 0;
    }
}

// Búsqueda en anchura usando una cola
// Función para realizar la búsqueda en anchura (BFS) de manera iterativa
// Parametros:
// - grafo: puntero al grafo en el que se hara la búsqueda
// - verticeInicio: vértice desde el cual se hara la búsqueda
void BFS_Iterativo(Grafo * grafo, int verticeInicio) {
    Cola * cola = crearCola(grafo -> numVertices);
    int estadoDeVertices[grafo -> numVertices]; //0 = en espera; 1 = listo; 2 = procesado
    int verticeActual;
    for(int i = 0; i < grafo -> numVertices; i++){
        estadoDeVertices[i] = 0;
    }
    encolar(cola, verticeInicio);
    estadoDeVertices[verticeInicio] = 1;
    while (cola -> tamano > 0) {
        verticeActual = desencolar(cola);
        printf("%d ", verticeActual);
        estadoDeVertices[verticeActual] = 2;
        Nodo * temp = grafo -> listasAdyacencia[verticeActual];
        while (temp) {
            if (estadoDeVertices[temp -> vertice] == 0) {
                encolar(cola, temp -> vertice);
                estadoDeVertices[temp -> vertice] = 1;
            }
            temp = temp -> siguiente;
        }
        free(temp);
    }
    free(cola);
}

int main() {
    int opcion;
    Grafo * grafo = NULL;
    do {
        printf("\n===== MENU =====\n");
        if (grafo == NULL) { printf("1. Crear grafo nuevo\n"); }
        if (grafo != NULL) {
            printf("2. Agregar arista\n");
            printf("3. Mostrar lista de adyacencia\n");
            printf("4. Ejecutar Recorrido a lo ancho\n");
            printf("5. Eliminar arista\n");
            printf("6. Eliminar grafo\n");
        }
        printf("7. Salir\n");
        printf("Ingrese una opcion: ");
        while (escaneoEntero(&opcion) == 0);
        switch (opcion) {
        case 1:
            if (grafo != NULL) {
                printf("Ya existe un grafo. Eliminelo para crear uno nuevo.\n");
            } else {
                int numVertices;
                printf("Ingrese el numero de vertices: ");
                while (escaneoEntero(&numVertices) == 0);
                grafo = crearGrafo(numVertices);
                printf("Grafo creado con %d vertices.\n", numVertices);
            }
            break;
        case 2:
            if (grafo == NULL) {
                printf("Primero debe crear un grafo.\n");
            } else {
                int origen, destino;
                printf("Ingrese el vertice de origen: ");
                while (escaneoEntero(&origen) == 0);
                printf("Ingrese el vertice de destino: ");
                while (escaneoEntero(&destino) == 0);
                if (origen < 0 || origen >= grafo -> numVertices || destino < 0 || destino >= grafo -> numVertices) {
                    printf("Vertice invalido.\n");
                } else {
                    agregarArista(grafo, origen, destino);
                    printf("Arista agregada entre %d y %d.\n", origen, destino);
                }
            }
            break;
        case 3:
            if (grafo == NULL) {
                printf("No hay grafo creado.\n");
            } else {
                mostrarListaAdyacencia(grafo);
            }
            break;
        case 4:
            if (grafo == NULL) {
                printf("No hay grafo creado.\n");
            } else {
                int inicio;
                printf("Ingrese el vertice de inicio para BFS: ");
                while (escaneoEntero(&inicio) == 0);
                if (inicio < 0 || inicio >= grafo->numVertices) {
                    printf("Vertice invalido.\n");
                } else {
                    reiniciarVisitado(grafo);
                    printf("Recorrido BFS iniciado desde el vertice %d:\n", inicio);
                    BFS_Iterativo(grafo, inicio);
                    printf("\n");
                }
            }
            break;
        case 5:
            if (grafo == NULL) {
                printf("No hay grafo creado.\n");
            } else {
                int origen, destino;
                printf("Ingrese el vertice de origen de la arista a eliminar: ");
                while (escaneoEntero(&origen) == 0);+
                printf("Ingrese el vertice de destino de la arista a eliminar: ");
                while (escaneoEntero(&destino) == 0);
                eliminarArista(grafo, origen, destino);
            }
            break;
        case 6:
            if (grafo == NULL) {
                printf("No hay grafo creado.\n");
            } else {
                eliminarGrafo(grafo);
                grafo = NULL;
            }
            break;
        case 7:
            printf("Saliendo...\n");
            break;
        default:
            printf("Opcion no valida.\n");
        }
    } while (opcion != 7);
    return 0;
}