#include <stdio.h>
#include <stdlib.h>

#define MAX_NODOS 100 // Número máximo de nodos

typedef struct {
    int *datos;
    int capacidad;
    int tamano;
    int frente; // Nuevo índice para mejorar eficiencia
} Cola;

typedef struct Nodo {
    int vertice;
    struct Nodo *siguiente;
} Nodo;

typedef struct Grafo {
    int numVertices;
    Nodo *listasAdyacencia[MAX_NODOS];
    int visitado[MAX_NODOS];
} Grafo;

void limpiarBuffer() {
    char c;
    while ((c = getchar()) != '\n' && c != EOF);
}

int escaneoEntero(int *variable) {
    if (scanf("%d", variable) != 1) {
        printf("Entrada inválida. Ingrese un número: ");
        limpiarBuffer();
        return 0;
    }
    return 1;
}

Cola *crearCola(int capacidadInicial) {
    Cola *cola = (Cola *)malloc(sizeof(Cola));
    if (!cola) {
        fprintf(stderr, "Error al asignar memoria para la cola.\n");
        exit(EXIT_FAILURE);
    }
    cola->datos = (int *)malloc(capacidadInicial * sizeof(int));
    if (!cola->datos) {
        free(cola);
        fprintf(stderr, "Error al asignar memoria para los elementos.\n");
        exit(EXIT_FAILURE);
    }
    cola->capacidad = capacidadInicial;
    cola->tamano = 0;
    cola->frente = 0; // Inicializar el índice de inicio
    return cola;
}

void redimensionarCola(Cola *cola) {
    int nuevaCapacidad = cola->capacidad * 2;
    int *nuevosDatos = (int *)realloc(cola->datos, nuevaCapacidad * sizeof(int));
    if (!nuevosDatos) {
        fprintf(stderr, "Error al redimensionar la cola.\n");
        exit(EXIT_FAILURE);
    }
    cola->datos = nuevosDatos;
    cola->capacidad = nuevaCapacidad;
}

void encolar(Cola *cola, int elemento) {
    if (cola->tamano == cola->capacidad) {
        redimensionarCola(cola);
    }
    cola->datos[cola->frente + cola->tamano] = elemento;
    cola->tamano++;
}

int desencolar(Cola *cola) {
    if (cola->tamano == 0) {
        fprintf(stderr, "La cola está vacía. No se puede desencolar.\n");
        exit(EXIT_FAILURE);
    }
    int elemento = cola->datos[cola->frente];
    cola->frente++; 
    cola->tamano--;
    return elemento;
}

void liberarCola(Cola *cola) {
    free(cola->datos);
    free(cola);
}

Grafo *crearGrafo(int vertices) {
    Grafo *grafo = (Grafo *)malloc(sizeof(Grafo));
    grafo->numVertices = vertices;
    for (int i = 0; i < vertices; i++) {
        grafo->listasAdyacencia[i] = NULL;
        grafo->visitado[i] = 0;
    }
    return grafo;
}

Nodo *crearNodo(int vertice) {
    Nodo *nuevoNodo = (Nodo *)malloc(sizeof(Nodo));
    nuevoNodo->vertice = vertice;
    nuevoNodo->siguiente = NULL;
    return nuevoNodo;
}

void agregarArista(Grafo *grafo, int origen, int destino) {
    Nodo *nuevoNodo = crearNodo(destino);
    nuevoNodo->siguiente = grafo->listasAdyacencia[origen];
    grafo->listasAdyacencia[origen] = nuevoNodo;

    nuevoNodo = crearNodo(origen);
    nuevoNodo->siguiente = grafo->listasAdyacencia[destino];
    grafo->listasAdyacencia[destino] = nuevoNodo;
}

void mostrarListaAdyacencia(Grafo *grafo) {
    printf("Lista de adyacencia:\n");
    for (int i = 0; i < grafo->numVertices; i++) {
        printf("%d: ", i);
        Nodo *temp = grafo->listasAdyacencia[i];
        while (temp) {
            printf("%d -> ", temp->vertice);
            temp = temp->siguiente;
        }
        printf("NULL\n");
    }
}

void eliminarArista(Grafo *grafo, int origen, int destino) {
    Nodo *actual = grafo->listasAdyacencia[origen], *anterior = NULL;
    while (actual != NULL && actual->vertice != destino) {
        anterior = actual;
        actual = actual->siguiente;
    }
    if (actual != NULL) {
        if (anterior == NULL) grafo->listasAdyacencia[origen] = actual->siguiente;
        else anterior->siguiente = actual->siguiente;
        free(actual);
    }

    actual = grafo->listasAdyacencia[destino], anterior = NULL;
    while (actual != NULL && actual->vertice != origen) {
        anterior = actual;
        actual = actual->siguiente;
    }
    if (actual != NULL) {
        if (anterior == NULL) grafo->listasAdyacencia[destino] = actual->siguiente;
        else anterior->siguiente = actual->siguiente;
        free(actual);
    }
}

void eliminarGrafo(Grafo *grafo) {
    for (int i = 0; i < grafo->numVertices; i++) {
        Nodo *actual = grafo->listasAdyacencia[i];
        while (actual) {
            Nodo *temp = actual;
            actual = actual->siguiente;
            free(temp);
        }
    }
    free(grafo);
    printf("Grafo eliminado.\n");
}

void BFS_Iterativo(Grafo *grafo, int verticeInicio) {
    Cola *cola = crearCola(grafo->numVertices);
    int estadoDeVertices[MAX_NODOS] = {0}; 
    encolar(cola, verticeInicio);
    estadoDeVertices[verticeInicio] = 1;
    
    while (cola->tamano > 0) {
        int verticeActual = desencolar(cola);
        printf("%d ", verticeActual);
        estadoDeVertices[verticeActual] = 2;
        Nodo *temp = grafo->listasAdyacencia[verticeActual];
        while (temp) {
            if (estadoDeVertices[temp->vertice] == 0) {
                encolar(cola, temp->vertice);
                estadoDeVertices[temp->vertice] = 1;
            }
            temp = temp->siguiente;
        }
    }
    liberarCola(cola);
}

int main() {
    int opcion;
    Grafo *grafo = NULL;
    do {
        printf("\n===== MENU =====\n");
        printf("1. Crear grafo\n");
        if (grafo) {
            printf("2. Agregar arista\n3. Mostrar adyacencia\n4. BFS\n5. Eliminar arista\n6. Eliminar grafo\n");
        }
        printf("7. Salir\nIngrese opción: ");
        while (escaneoEntero(&opcion) == 0);
        switch (opcion) {
            case 1: if (!grafo) { int v; printf("Vertices: "); while (escaneoEntero(&v) == 0); grafo = crearGrafo(v); }
                else printf("Ya hay un grafo.\n"); break;
            case 2: case 5: if (grafo) { int o, d; printf("Origen: "); while (escaneoEntero(&o) == 0); printf("Destino: "); while (escaneoEntero(&d) == 0);
                if (o >= 0 && d >= 0 && o < grafo->numVertices && d < grafo->numVertices) (opcion == 2) ? agregarArista(grafo, o, d) : eliminarArista(grafo, o, d); }
                break;
            case 3: if (grafo) mostrarListaAdyacencia(grafo); break;
            case 4: if (grafo) { int i; printf("Inicio BFS: "); while (escaneoEntero(&i) == 0); BFS_Iterativo(grafo, i); printf("\n"); } break;
            case 6: if (grafo) { eliminarGrafo(grafo); grafo = NULL; } break;
        }
    } while (opcion != 7);
    return 0;
}
