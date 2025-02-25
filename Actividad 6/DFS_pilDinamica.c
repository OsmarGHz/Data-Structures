#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define MAX_NODOS 100  // N�mero m�ximo de nodos

// ===================== Pila  =====================

// Nodo de la pila
typedef struct NodoPila {
    int dato;
    struct NodoPila* siguiente;
} NodoPila;

// Estructura de la pila
typedef struct Pila {
    NodoPila* tope;
} Pila;

// Crea una nueva pila
Pila* crearPila() {
    Pila* pila = (Pila*)malloc(sizeof(Pila));
    pila->tope = NULL;
    return pila;
}

// Agrega un elemento a la pila
void apilar(Pila* pila, int valor) {
    NodoPila* nuevoNodo = (NodoPila*)malloc(sizeof(NodoPila));
    nuevoNodo->dato = valor;
    nuevoNodo->siguiente = pila->tope;
    pila->tope = nuevoNodo;
}

// Elimina y retorna el elemento en el tope de la pila
int desapilar(Pila* pila) {
    if (pila->tope == NULL) { // Si la pila est� vac�a
        return -1;
    }
    NodoPila* temp = pila->tope;
    int valor = temp->dato;
    pila->tope = temp->siguiente;
    free(temp);
    return valor;
}

// Retorna 1 si la pila est� vac�a, 0 si no
int estaVacia(Pila* pila) {
    return pila->tope == NULL;
}

// Libera la memoria de la pila
void liberarPila(Pila* pila) {
    while (!estaVacia(pila)) {
        desapilar(pila);
    }
    free(pila);
}

// ===================== Grafo y Operaciones =====================

// Nodo de la lista de adyacencia
typedef struct Nodo {
    int vertice;  // dato que guarda el nodo
    struct Nodo* siguiente;  // nodos adyacentes
} Nodo;

// Estructura del grafo usando lista de adyacencia
typedef struct Grafo {
    int numVertices;  // n�mero de v�rtices que tendr� la gr�fica
    Nodo* listasAdyacencia[MAX_NODOS];  // lista de adyacencia para cada v�rtice
    int visitado[MAX_NODOS];  // bandera para nodos visitados
} Grafo;

// Crea un nuevo grafo
Grafo* crearGrafo(int vertices) {
    Grafo* grafo = (Grafo*)malloc(sizeof(Grafo)); // reserva memoria para el grafo
    grafo->numVertices = vertices; // asigna el n�mero de v�rtices
    for (int i = 0; i < vertices; i++) {
        grafo->listasAdyacencia[i] = NULL; //inicializa listas de adyacencia a null
        grafo->visitado[i] = 0; // inicializa todos como no visitados
    }
    return grafo;
}

// Crea un nuevo nodo para la lista de adyacencia
Nodo* crearNodo(int vertice) {
    Nodo* nuevoNodo = (Nodo*)malloc(sizeof(Nodo)); // reserva espacio en memoria
    nuevoNodo->vertice = vertice; // asigna el v�rtice al nodo
    nuevoNodo->siguiente = NULL;  // el nuevo nodo apunta a NULL
    return nuevoNodo;
}

// Agrega una arista al grafo (para grafo no dirigido)
void agregarArista(Grafo* grafo, int origen, int destino) {
    // Agrega arista de origen a destino
    Nodo* nuevoNodo = crearNodo(destino);
    //nuevo nodo apunta a la "cabeza" de la lista de adyacencia del vertice origen
    nuevoNodo->siguiente = grafo->listasAdyacencia[origen];
    //el nuevo nodo se vuelve la "cabeza" de la lista de adyacencia de origen
    grafo->listasAdyacencia[origen] = nuevoNodo;

    // Agrega la arista inversa
    nuevoNodo = crearNodo(origen);
    //nuevo nodo apunta a la "cabeza" de la lista de adyacencia del vertice destino
    nuevoNodo->siguiente = grafo->listasAdyacencia[destino];
    //nuevo nodo apunta a la "cabeza" de la lista de adyacencia del vertice destino
    grafo->listasAdyacencia[destino] = nuevoNodo;
}

// Muestra la lista de adyacencia del grafo
void mostrarListaAdyacencia(Grafo* grafo) {
    printf("Lista de adyacencia:\n");
    for (int i = 0; i < grafo->numVertices; i++) {
        printf("%d: ", i);
        Nodo* temp = grafo->listasAdyacencia[i];
        while (temp) {
            printf("%d -> ", temp->vertice);
            temp = temp->siguiente;
        }
        printf("NULL\n");
    }
    /*
    produce la salida:
    0: vertice -> vertice -> NULL
    1: vertice -> vertice -> NULL
    .
    .
    */
}

// Elimina una arista del grafo (ambos sentidos)
void eliminarArista(Grafo* grafo, int origen, int destino) {
    Nodo *actual, *anterior;

    // Eliminar de la lista de 'origen'
    //a actual se le asigna la cabeza de la lista de adyacencia de origen
    actual = grafo->listasAdyacencia[origen];
    anterior = NULL;
    //se recorre la lista de adyacencia de origen hasta
    //encontrar destino
    while (actual != NULL && actual->vertice != destino) {
        anterior = actual;
        actual = actual->siguiente;
    }
    //Si actual no es NULL despu�s del ciclo, significa que se
    //encontr� un nodo cuyo valor (vertice) es igual a destino.
    if (actual != NULL) {
        if (anterior == NULL) { //Esto significa que el nodo a eliminar es el primero de la lista (la cabeza).
            //Se actualiza la cabeza de la lista de adyacencia de origen
            //para que apunte al siguiente nodo (actual->siguiente).
            grafo->listasAdyacencia[origen] = actual->siguiente;
        } else { //El nodo a eliminar est� en alg�n lugar del medio o al final de la lista.

            //Se actualiza el enlace del nodo anterior (anterior->siguiente) para que "salte" el
            //nodo que se va a eliminar, enlaz�ndose directamente con el siguiente de actual.
            anterior->siguiente = actual->siguiente;
        }
        //Se libera la memoria asignada al nodo eliminado
        free(actual);
        printf("Arista eliminada de %d a %d.\n", origen, destino);
    } else {
        printf("No se encontr� la arista de %d a %d.\n", origen, destino);
    }

    // Eliminar de la lista de 'destino', misma l�gica
    actual = grafo->listasAdyacencia[destino];
    anterior = NULL;
    while (actual != NULL && actual->vertice != origen) {
        anterior = actual;
        actual = actual->siguiente;
    }
    if (actual != NULL) {
        if (anterior == NULL) {
            grafo->listasAdyacencia[destino] = actual->siguiente;
        } else {
            anterior->siguiente = actual->siguiente;
        }
        free(actual);
    }
}

// Elimina el grafo y libera la memoria
void eliminarGrafo(Grafo* grafo) {
    for (int i = 0; i < grafo->numVertices; i++) {
        //elimina las listas de adyacencia
        Nodo* actual = grafo->listasAdyacencia[i];
        while (actual != NULL) {
            Nodo* temp = actual;
            actual = actual->siguiente;
            free(temp);
        }
    }
    free(grafo);
    printf("Grafo eliminado.\n");
}

// Reinicia el arreglo de visitados
//usado para hacer varios recorridos en una sola ejecucion
void reiniciarVisitado(Grafo* grafo) {
    for (int i = 0; i < grafo->numVertices; i++) {
        grafo->visitado[i] = 0;
    }
}

// B�squeda en profundidad (DFS) iterativa usando la pila din�mica
void DFS_Iterativo(Grafo* grafo, int verticeInicio) {
    Pila* pila = crearPila();
    apilar(pila, verticeInicio);

    while (!estaVacia(pila)) {
        //se verifica si el vertice en el tope de la pila
        //ya fue visitado, si no es el caso entonces se muestra por pantalla
        //y se marca como visitado
        int verticeActual = desapilar(pila);
        if (!grafo->visitado[verticeActual]) {
            printf("%d ", verticeActual);
            //el vertice en cuestion se marca como visitado
            //para no ser "procesado" de nuevo en iteraciones posteriores
            grafo->visitado[verticeActual] = 1;
        }
        /*
        se una un apuntador a un nodo llamado temp al cual se le
        asignara la cabeza de la lista de adyacencia del vertice que
        se esta trabajando en esta iteracion.
        */
        Nodo* temp = grafo->listasAdyacencia[verticeActual];
        while (temp) {
            if (!grafo->visitado[temp->vertice]) {
                /*
                si el vertice guardado en el nodo en turno de la lista de adyacencia
                del vertice actual no est� marcado como visitado, entonces ser� agregado
                a la pila, es decir, aqu� se agregan a la pila los v�rtices adyacentes
                del vertice actual
                */
                apilar(pila, temp->vertice);
            }
            //se avanza de nodo en la lista de adyacencia
            temp = temp->siguiente;
        }
        /*
        terminados de ser agregados a la pila los vertices adyacentes
        del vertice actual entonces se regresara a verificar si la pila
        esta vacia, si no lo esta entonces se hara de nuevo todo el proceso
        anterior con el detalle de que ya habra nodos marcados como visitados
        y por tanto ya no seran agregados a la pila. De esta forma al llegar
        a un vertice que ya no tiene vertices adyacentes se hara un "retorno"
        hasta encontrar otro vertice si tiene adyacentes (si es que lo hay).
        */
    }
    liberarPila(pila);
}

// Funcion para obtener un numero v�lido del usuario
int obtenerNumero(const char *mensaje) {
    char entrada[100]; // Buffer para almacenar la entrada del usuario
    int valido, i;

    do {
        printf("%s", mensaje);  // Muestra el mensaje
        scanf("%s", entrada);   //lee la entrada
        // Verificar si toda la entrada contiene solo d�gitos
        valido = 1;
        for (i = 0; i < strlen(entrada); i++) {
            if (!isdigit(entrada[i])) {
                valido = 0;
                break; //con valido 0 se sale del bucle
            }
        }

        if (!valido) {
            printf("Error: Ingresa solo numeros.\n");
        }
    } while (!valido); // repetir mientras valido sea 0

    return atoi(entrada); // Convertir la entrada a entero
}

// ===================== Men� Principal =====================

int main() {
    int opcion;
    Grafo* grafo = NULL;

    do {
        printf("\n===== MENU =====\n");
        printf("1. Crear grafo nuevo\n");
        printf("2. Agregar arista\n");
        printf("3. Mostrar lista de adyacencia\n");
        printf("4. Ejecutar Recorrido a lo profundo\n");
        printf("5. Eliminar arista\n");
        printf("6. Eliminar grafo\n");
        printf("7. Salir\n");
        opcion = obtenerNumero("Ingrese una opcion: ");

        switch(opcion) {
            case 1: {
                if (grafo != NULL) {
                    printf("Ya existe un grafo. Eliminelo para crear uno nuevo.\n");
                } else {
                    int numVertices = obtenerNumero("Ingrese el numero de vertices: ");
                    grafo = crearGrafo(numVertices);
                    printf("Grafo creado con %d vertices.\n", numVertices);
                }
                break;
            }
            case 2: {
                if (grafo == NULL) {
                    printf("Primero debe crear un grafo.\n");
                } else {
                    int origen, destino;
                    origen = obtenerNumero("Ingrese el vertice de origen: ");
                    destino = obtenerNumero("Ingrese el vertice de destino: ");
                    if (origen < 0 || origen >= grafo->numVertices || destino < 0 || destino >= grafo->numVertices) {
                        printf("Vertice invalido.\n");
                    } else {
                        agregarArista(grafo, origen, destino);
                        printf("Arista agregada entre %d y %d.\n", origen, destino);
                    }
                }
                break;
            }
            case 3: {
                if (grafo == NULL) {
                    printf("No hay grafo creado.\n");
                } else {
                    mostrarListaAdyacencia(grafo);
                }
                break;
            }
            case 4: {
                if (grafo == NULL) {
                    printf("No hay grafo creado.\n");
                } else {
                    int inicio = obtenerNumero("Ingrese el vertice de inicio para el recorrido: ");
                    if (inicio < 0 || inicio >= grafo->numVertices) {
                        printf("Vertice invalido.\n");
                    } else {
                        reiniciarVisitado(grafo);
                        printf("Recorrido a lo profundo iniciado desde el vertice %d:\n", inicio);
                        DFS_Iterativo(grafo, inicio);
                        printf("\n");
                    }
                }
                break;
            }
            case 5: {
                if (grafo == NULL) {
                    printf("No hay grafo creado.\n");
                } else {
                    int origen, destino;
                    origen = obtenerNumero("Ingrese el vertice de origen de la arista a eliminar: ");
                    destino = obtenerNumero("Ingrese el vertice de destino de la arista a eliminar: ");
                    eliminarArista(grafo, origen, destino);
                }
                break;
            }
            case 6: {
                if (grafo == NULL) {
                    printf("No hay grafo creado.\n");
                } else {
                    eliminarGrafo(grafo);
                    grafo = NULL;
                }
                break;
            }
            case 7:
                printf("Saliendo...\n");
                break;
            default:
                printf("Opcion no valida.\n");
        }
    } while (opcion != 7);

    return 0;
}
