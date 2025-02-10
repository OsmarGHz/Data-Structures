#include <stdio.h>
#define MAX 10 // Tamaño fijo para la lista

// Estructura para un nodo
typedef struct {
    int dato;
    int sig; // Índice del siguiente nodo
    int ant; // Índice del nodo anterior
} Nodo;

// Arreglo estático para representar la lista
Nodo lista[MAX];
int cabeza = -1; // Índice de la cabeza de la lista
int cola = -1; // Índice de la cola de la lista
int libre = 0; // Índice del siguiente espacio libre

// Inicializa la lista
void inicializarLista() {
    int i;
    for (i = 0; i < MAX - 1; i++) {
        lista[i].sig = i + 1; // El índice siguiente al actual
        lista[i].ant = -1; // No tiene anterior por ahora
    }
    lista[MAX - 1].sig = -1; // Fin de la lista
    lista[MAX - 1].ant = -1;
}

// Inserta un nodo al inicio
void insertaInicio(int valor) {
    if (libre == -1) {
        printf("Lista llena. No se puede insertar.\n");
        return;
    }
    int nuevo = libre; // Toma el índice libre actual
    libre = lista[libre].sig; // Actualiza el índice libre
    lista[nuevo].dato = valor;
    lista[nuevo].ant = -1;
    lista[nuevo].sig = cabeza;

    if (cabeza != -1) {
        lista[cabeza].ant = nuevo;
    } else {
        cola = nuevo;
    }
    cabeza = nuevo;
    if (cola == -1) {
        cola = cabeza;
    }
    printf("Nodo con valor %d insertado al inicio.\n", valor);
}

// Inserta un nodo al final
void insertaFinal(int valor) {
    if (libre == -1) {
        printf("Lista llena. No se puede insertar.\n");
        return;
    }
    int nuevo = libre; // Toma el índice libre actual
    libre = lista[libre].sig; // Actualiza el índice libre
    lista[nuevo].dato = valor;
    lista[nuevo].sig = -1;
    lista[nuevo].ant = cola;

    if (cola != -1) {
        lista[cola].sig = nuevo;
    } else {
        cabeza = nuevo;
    }
    cola = nuevo;
    if (cabeza == -1) {
        cabeza = cola;
    }
    printf("Nodo con valor %d insertado al final.\n", valor);
}

// Inserta un nodo DESPUÉS de un nodo de referencia dado
void insertarDespues(int ref, int valor) {
    int actual = cabeza;
    while (actual != -1 && lista[actual].dato != ref) {
        actual = lista[actual].sig;
    }

    if (actual == -1) {
        printf("Error: Nodo de referencia no encontrado.\n");
        return;
    }

    if (libre == -1) {
        printf("Lista llena. No se puede insertar.\n");
        return;
    }

    int nuevo = libre;
    libre = lista[libre].sig;
    lista[nuevo].dato = valor;
    lista[nuevo].ant = actual;
    lista[nuevo].sig = lista[actual].sig;

    if (lista[actual].sig != -1) {
        lista[lista[actual].sig].ant = nuevo;
    } else {
        cola = nuevo;
    }

    lista[actual].sig = nuevo;
    printf("Nodo con valor %d insertado después del nodo con valor %d.\n", valor, ref);
}

// Borra el nodo al inicio de la lista
void borrarInicio() {
    if (cabeza == -1) {
        printf("Error: Lista vacía. No se puede borrar.\n");
        return;
    }
    int temp = cabeza;
    cabeza = lista[cabeza].sig;

    if (cabeza != -1) {
        lista[cabeza].ant = -1;
    } else {
        cola = -1;
    }

    printf("Nodo del inicio borrado (valor %d).\n", lista[temp].dato);

    lista[temp].sig = libre;
    lista[temp].ant = -1;
    libre = temp;
}

// Borra el nodo al final de la lista
void borrarFinal() {
    if (cola == -1) {
        printf("Error: Lista vacía. No se puede borrar.\n");
        return;
    }

    int temp = cola;
    cola = lista[cola].ant;

    if (cola != -1) {
        lista[cola].sig = -1;
    } else {
        cabeza = -1;
    }

    printf("Nodo eliminado del final con valor: %d\n", lista[temp].dato);

    lista[temp].sig = libre;
    lista[temp].ant = -1;
    libre = temp;
}

void borrarNodo(int valor) {
    if (cabeza == -1) {
        printf("Error: Lista vacía. No se puede borrar.\n");
        return;
    }

    int actual = cabeza;

    // Buscar el nodo con el valor especificado
    while (actual != -1 && lista[actual].dato != valor) {
        actual = lista[actual].sig;
    }

    // Si no se encontró el nodo
    if (actual == -1) {
        printf("Error: Nodo con valor %d no encontrado.\n", valor);
        return;
    }

    // Si es el único nodo en la lista
    if (actual == cabeza && actual == cola) {
        cabeza = cola = -1;
    }
    // Si es la cabeza de la lista
    else if (actual == cabeza) {
        cabeza = lista[actual].sig;
        lista[cabeza].ant = -1;
    }
    // Si es la cola de la lista
    else if (actual == cola) {
        cola = lista[actual].ant;
        lista[cola].sig = -1;
    }
    // Si es un nodo intermedio
    else {
        lista[lista[actual].ant].sig = lista[actual].sig;
        lista[lista[actual].sig].ant = lista[actual].ant;
    }

    printf("Nodo con valor %d eliminado.\n", lista[actual].dato);
    lista[actual].sig = libre;
    lista[actual].ant = -1;
    libre = actual;
}

// Borra el único nodo de la lista (si existe únicamente uno)
void borrarUnico() {
    if (cabeza == -1) {
        printf("Error: Lista vacía. No hay nodo que borrar.\n");
        return;
    }
    if (lista[cabeza].sig != -1) {
        printf("Error: La lista tiene más de un nodo.\n");
        return;
    }

    printf("Nodo único borrado con valor: %d\n", lista[cabeza].dato);
    lista[cabeza].sig = libre;
    lista[cabeza].ant = -1;
    libre = cabeza;
    cabeza = cola = -1;
}

// Funciones de Búsqueda
// Buscar por valor desde la cabeza: retorna la posición (iniciando en 1) o -1 si no se encuentra.
int buscarPosicionDesdeCabeza(int valor) {
    int actual = cabeza;
    int pos = 1;
    while (actual != -1) {
        if (lista[actual].dato == valor)
            return pos;
        pos++;
        actual = lista[actual].sig;
    }
    return -1;
}

// Buscar por valor desde la cola: retorna la posición (iniciando en 1 desde la cola) o -1 si no se encuentra.
int buscarPosicionDesdeCola(int valor) {
    int actual = cola;
    int pos = 1;
    while (actual != -1) {
        if (lista[actual].dato == valor)
            return pos;
        pos++;
        actual = lista[actual].ant;
    }
    return -1;
}

// Buscar y retornar el índice del nodo por valor (desde la cabeza)
int buscarNodoPorValorDesdeCabeza(int valor) {
    int actual = cabeza;
    while (actual != -1) {
        if (lista[actual].dato == valor)
            return actual;
        actual = lista[actual].sig;
    }
    return -1;
}

// Buscar y retornar el índice del nodo en la posición indicada (posición 1 es la cabeza)
int buscarNodoPorPosicionDesdeCabeza(int pos) {
    int actual = cabeza;
    int count = 1;
    while (actual != -1 && count < pos) {
        actual = lista[actual].sig;
        count++;
    }
    return actual;
}

// Imprime la lista de cabeza a cola
void imprimirLista() {
    if (cabeza == -1) {
        printf("Lista vacía.\n");
        return;
    }
    int actual = cabeza;
    printf("Lista: ");
    while (actual != -1) {
        printf("%d ", lista[actual].dato);
        actual = lista[actual].sig;
    }
    printf("\n");
}

// Menú principal
void menu() {
    int opcion;
    do {
        printf("\nMENU\n");
        printf("1. Insertar nodo al inicio\n");
        printf("2. Insertar nodo al final\n");
        printf("3. Insertar nodo entre dos nodos\n");
        printf("4. Borrar nodo del inicio\n");
        printf("5. Borrar nodo del final\n");
        printf("6. Borrar nodo entre dos nodos\n");
        printf("7. Borrar el único nodo\n");
        printf("8. Buscar un elemento\n");
        printf("9. Imprimir lista\n");
        printf("10. Salir\n");
        printf("Seleccione una opción: ");
        scanf("%d", &opcion);

        int valor, pos, ref, metodo;
        int refNodo = -1;
        switch (opcion) {
            case 1:
                printf("Ingrese el valor a insertar al inicio: ");
                scanf("%d", &valor);
                insertaInicio(valor);
                break;
            case 2:
                printf("Ingrese el valor a insertar al final: ");
                scanf("%d", &valor);
                insertaFinal(valor);
                break;
            case 3:
                // Insertar entre dos nodos
                printf("Seleccione método para insertar entre dos nodos:\n");
                printf("1. Por valor del nodo de referencia\n");
                printf("2. Por posición del nodo de referencia\n");
                printf("Opción: ");
                scanf("%d", &metodo);
                printf("Ingrese el valor a insertar: ");
                scanf("%d", &valor);
                if (metodo == 1) {
                    printf("Ingrese el valor del nodo de referencia (después del cual insertar): ");
                    scanf("%d", &ref);
                    refNodo = buscarNodoPorValorDesdeCabeza(ref);
                    if (refNodo == -1)
                        printf("Error: Nodo de referencia no encontrado.\n");
                    else
                        insertarDespues(ref, valor);
                } else if (metodo == 2) {
                    printf("Ingrese la posición del nodo de referencia (después del cual insertar): ");
                    scanf("%d", &pos);
                    refNodo = buscarNodoPorPosicionDesdeCabeza(pos);
                    if (refNodo == -1)
                        printf("Error: No existe nodo en la posición %d.\n", pos);
                    else
                        insertarDespues(lista[refNodo].dato, valor);
                } else {
                    printf("Opción no válida.\n");
                }
                break;
            case 4:
                borrarInicio();
                break;
            case 5:
                borrarFinal();
                break;
            case 6:
                printf("Ingrese el valor del nodo a borrar: ");
                scanf("%d", &valor);
                borrarNodo(valor);
                break;
            case 7:
                borrarUnico();
                break;
            case 8:
                printf("Seleccione desde:\n");
                printf("1. Desde la cabeza\n");
                printf("2. Desde la cola\n");
                printf("Opción: ");
                int desde;
                scanf("%d", &desde);
                printf("Ingrese el valor a buscar: ");
                scanf("%d", &valor);
                if (desde == 1) {
                    pos = buscarPosicionDesdeCabeza(valor);
                    if (pos == -1)
                        printf("Elemento %d no encontrado desde la cabeza.\n", valor);
                    else
                        printf("Elemento %d encontrado en la posición %d desde la cabeza.\n", valor, pos);
                } else if (desde == 2) {
                    pos = buscarPosicionDesdeCola(valor);
                    if (pos == -1)
                        printf("Elemento %d no encontrado desde la cola.\n", valor);
                    else
                        printf("Elemento %d encontrado en la posición %d desde la cola.\n", valor, pos);
                } else {
                    printf("Opción no válida.\n");
                }
                break;
            case 9:
                imprimirLista();
                break;
            case 10:
                printf("Saliendo del programa.\n");
                break;
            default:
                printf("Opción no válida.\n");
        }
    } while (opcion != 10);
}

// Función principal
int main() {
    inicializarLista();
    menu();
    return 0;
}