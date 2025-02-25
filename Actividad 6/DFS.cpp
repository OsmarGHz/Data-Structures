#include <stdio.h>
#include <stdlib.h>

#define MAX_NODOS 100 // Número máximo de nodos

// Estructura de la pila
typedef struct Pila {
    int cima;
    int elementos[MAX_NODOS];
} Pila;

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

// Funciones de la pila
Pila * crearPila() {
    Pila * pila = (Pila *) malloc(sizeof(Pila)); // toma espacio en memoria
    pila -> cima = -1; // inicializa la pila con su cima en -1
    return pila;
}

void apilar(Pila * pila, int valor) {
    if (pila -> cima == MAX_NODOS - 1) { // verificar si la pila está llena
        printf("¡Desbordamiento de pila!\n");
        return;
    }
    pila -> elementos[++pila -> cima] = valor; // avanza y guarda el valor
}

int desapilar(Pila * pila) {
    if (pila -> cima == -1) { // verifica si la pila está vacía
        return -1;
    }
    return pila->elementos[pila -> cima--]; // obtiene el valor y decrementa la cima
}

int estaVacia(Pila * pila) {
    return pila -> cima == -1; // retorna 1 si está vacía, 0 si no
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

// Búsqueda en profundidad iterativa usando una pila
void DFS_Iterativo(Grafo * grafo, int verticeInicio) {
    Pila * pila = crearPila();
    apilar(pila, verticeInicio);
    while (!estaVacia(pila)) {
        int verticeActual = desapilar(pila);
        if (!grafo -> visitado[verticeActual]) {
            printf("%d ", verticeActual);
            grafo -> visitado[verticeActual] = 1;
        }
        Nodo * temp = grafo -> listasAdyacencia[verticeActual];
        while (temp) {
            if (!grafo -> visitado[temp -> vertice]) {
                apilar(pila, temp -> vertice);
            }
            temp = temp -> siguiente;
        }
    }
    free(pila);
}

int main() {
    int opcion;
    Grafo * grafo = NULL;
    do {
        printf("\n===== MENU =====\n");
        printf("1. Crear grafo nuevo\n");
        printf("2. Agregar arista\n");
        printf("3. Mostrar lista de adyacencia\n");
        printf("4. Ejecutar Recorrido a lo profundo\n");
        printf("5. Eliminar arista\n");
        printf("6. Eliminar grafo\n");
        printf("7. Salir\n");
        printf("Ingrese una opcion: ");
        scanf("%d", &opcion);
        switch (opcion) {
        case 1:
            if (grafo != NULL) {
                printf("Ya existe un grafo. Eliminelo para crear uno nuevo.\n");
            } else {
                int numVertices;
                printf("Ingrese el numero de vertices: ");
                scanf("%d", &numVertices);
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
                scanf("%d", &origen);
                printf("Ingrese el vertice de destino: ");
                scanf("%d", &destino);
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
                printf("Ingrese el vertice de inicio para DFS: ");
                scanf("%d", &inicio);
                if (inicio < 0 || inicio >= grafo -> numVertices) {
                    printf("Vertice invalido.\n");
                } else {
                    reiniciarVisitado(grafo);
                    printf("Recorrido a lo profundo iniciado desde el vertice %d:\n", inicio);
                    DFS_Iterativo(grafo, inicio);
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
                scanf("%d", &origen);
                printf("Ingrese el vertice de destino de la arista a eliminar: ");
                scanf("%d", &destino);
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
