#include <stdio.h>
#include <stdlib.h>

// Definición de la estructura de un nodo de la lista doblemente ligada
typedef struct Nodo {
    int dato;
    struct Nodo* next;
    struct Nodo* prev;
} Nodo;

// Punteros globales para la cabeza y la cola de la lista
Nodo* cabeza = NULL;
Nodo* cola = NULL;

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

// -------------------------------------------------------------------
// Inserta un nodo al inicio de la lista
void insertaInicio(int valor) {
    Nodo* nuevo = (Nodo*) malloc(sizeof(Nodo));
    if (nuevo == NULL) {
        printf("Lo sentimos: Memoria insuficiente.\n");
        return;
    }
    nuevo->dato = valor;
    nuevo->next = cabeza;
    nuevo->prev = NULL;
    
    if (cabeza != NULL)
        cabeza->prev = nuevo;
    else
        cola = nuevo;  // Si la lista estaba vacía, el nuevo nodo es también la cola
    
    cabeza = nuevo;
    printf("Nodo con valor %d insertado al inicio.\n", valor);
}

// -------------------------------------------------------------------
// Inserta un nodo al final de la lista
void insertaFinal(int valor) {
    Nodo* nuevo = (Nodo*) malloc(sizeof(Nodo));
    if (nuevo == NULL) {
        printf("Lo sentimos: Memoria insuficiente.\n");
        return;
    }
    nuevo->dato = valor;
    nuevo->next = NULL;
    nuevo->prev = cola;
    
    if (cola != NULL)
        cola->next = nuevo;
    else
        cabeza = nuevo;  // Si la lista estaba vacía, el nuevo nodo es también la cabeza
    
    cola = nuevo;
    printf("Nodo con valor %d insertado al final.\n", valor);
}

// -------------------------------------------------------------------
// Inserta un nodo DESPUÉS de un nodo de referencia dado
void insertarDespues(Nodo* ref, int valor) {
    if (ref == NULL) {
        printf("Lo sentimos: Nodo de referencia nulo.\n");
        return;
    }
    
    Nodo* nuevo = (Nodo*) malloc(sizeof(Nodo));
    if (nuevo == NULL) {
        printf("Lo sentimos: Memoria insuficiente.\n");
        return;
    }
    nuevo->dato = valor;
    nuevo->prev = ref;
    nuevo->next = ref->next;
    
    if (ref->next != NULL)
        ref->next->prev = nuevo;
    else
        cola = nuevo;  // Si el nodo de referencia era la cola, se actualiza
    
    ref->next = nuevo;
    printf("Nodo con valor %d insertado después del nodo con valor %d.\n", valor, ref->dato);
}

// -------------------------------------------------------------------
// Borra el nodo al inicio de la lista
void borrarInicio() {
    if (cabeza == NULL) {
        printf("Lo sentimos: Lista vacía. No se puede borrar.\n");
        return;
    }
    Nodo* temp = cabeza;
    cabeza = cabeza->next;
    
    if (cabeza != NULL)
        cabeza->prev = NULL;
    else
        cola = NULL;  // La lista queda vacía
    
    printf("Nodo del inicio borrado (valor %d).\n", temp->dato);
    free(temp);
}

// -------------------------------------------------------------------
// Borra el nodo al final de la lista
void borrarFinal() {
    if (cola == NULL) {
        printf("Lo sentimos: Lista vacía. No se puede borrar.\n");
        return;
    }
    Nodo* temp = cola;
    cola = cola->prev;
    
    if (cola != NULL)
        cola->next = NULL;
    else
        cabeza = NULL;  // La lista queda vacía
    
    printf("Nodo eliminado del final con valor: %d\n", temp->dato);
    free(temp);
}

// -------------------------------------------------------------------
void borrarNodo(int valor) {
    if (cabeza == NULL) {
        printf("Lo sentimos: Lista vacía. No se puede borrar.\n");
        return;
    }

    Nodo* actual = cabeza;

    // Buscar el nodo con el valor especificado
    while (actual != NULL && actual->dato != valor) {
        actual = actual->next;
    }

    // Si no se encontró el nodo
    if (actual == NULL) {
        printf("Lo sentimos: Nodo con valor %d no encontrado.\n", valor);
        return;
    }

    // Si es el único nodo en la lista
    if (actual == cabeza && actual == cola) {
        cabeza = cola = NULL;
    }
    // Si es la cabeza de la lista
    else if (actual == cabeza) {
        cabeza = actual->next;
        cabeza->prev = NULL;
    }
    // Si es la cola de la lista
    else if (actual == cola) {
        cola = actual->prev;
        cola->next = NULL;
    }
    // Si es un nodo intermedio
    else {
        actual->prev->next = actual->next;
        actual->next->prev = actual->prev;
    }

    printf("Nodo con valor %d eliminado.\n", actual->dato);
    free(actual);
}

// -------------------------------------------------------------------
// Borra el único nodo de la lista (si existe únicamente uno)
void borrarUnico() {
    if (cabeza == NULL) {
        printf("Lo sentimos: Lista vacía. No hay nodo que borrar.\n");
        return;
    }
    if (cabeza->next != NULL) {
        printf("Lo sentimos: La lista tiene más de un nodo.\n");
        return;
    }
    
    printf("Nodo único borrado con valor: %d\n", cabeza->dato);
    free(cabeza);
    cabeza = cola = NULL;
}

// -------------------------------------------------------------------
// Funciones de Búsqueda
// Buscar por valor desde la cabeza: retorna la posición (iniciando en 1) o -1 si no se encuentra.
int buscarPosicionDesdeCabeza(int valor) {
    Nodo* actual = cabeza;
    int pos = 1;
    while (actual != NULL) {
        if (actual->dato == valor)
            return pos;
        pos++;
        actual = actual->next;
    }
    return -1;
}

// Buscar por valor desde la cola: retorna la posición (iniciando en 1 desde la cola) o -1 si no se encuentra.
int buscarPosicionDesdeCola(int valor) {
    Nodo* actual = cola;
    int pos = 1;
    while (actual != NULL) {
        if (actual->dato == valor)
            return pos;
        pos++;
        actual = actual->prev;
    }
    return -1;
}

// Buscar y retornar el nodo por valor (desde la cabeza)
Nodo* buscarNodoPorValorDesdeCabeza(int valor) {
    Nodo* actual = cabeza;
    while (actual != NULL) {
        if (actual->dato == valor)
            return actual;
        actual = actual->next;
    }
    return NULL;
}

// Buscar y retornar el nodo en la posición indicada (posición 1 es la cabeza)
Nodo* buscarNodoPorPosicionDesdeCabeza(int pos) {
    Nodo* actual = cabeza;
    int count = 1;
    while (actual != NULL && count < pos) {
        actual = actual->next;
        count++;
    }
    return actual;
}

// -------------------------------------------------------------------
// Imprime la lista de cabeza a cola
void imprimirLista() {
    if (cabeza == NULL) {
        printf("Lista vacía.\n");
        return;
    }
    Nodo* actual = cabeza;
    printf("Lista: ");
    while (actual != NULL) {
        printf("%d ", actual->dato);
        actual = actual->next;
    }
    printf("\n");
}

// -------------------------------------------------------------------
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
        while (escaneoEntero(&opcion) == 0);

        int valor, pos, ref, metodo;
        Nodo* refNodo = NULL;
        switch(opcion) {
            case 1:
                printf("Ingrese el valor a insertar al inicio: ");
                while (escaneoEntero(&valor) == 0);
                insertaInicio(valor);
                break;
            case 2:
                printf("Ingrese el valor a insertar al final: ");
                while (escaneoEntero(&valor) == 0);
                insertaFinal(valor);
                break;
            case 3:
                // Insertar entre dos nodos
                printf("Seleccione método para insertar entre dos nodos:\n");
                printf("1. Por valor del nodo de referencia\n");
                printf("2. Por posición del nodo de referencia\n");
                printf("Opción: ");
                while (escaneoEntero(&metodo) == 0);
                printf("Ingrese el valor a insertar: ");
                while (escaneoEntero(&valor) == 0);
                if (metodo == 1) {
                    printf("Ingrese el valor del nodo de referencia (después del cual insertar): ");
                    while (escaneoEntero(&ref) == 0);
                    refNodo = buscarNodoPorValorDesdeCabeza(ref);
                    if (refNodo == NULL)
                        printf("Lo sentimos: Nodo de referencia no encontrado.\n");
                    else
                        insertarDespues(refNodo, valor);
                } else if (metodo == 2) {
                    printf("Ingrese la posición del nodo de referencia (después del cual insertar): ");
                    while (escaneoEntero(&pos) == 0);
                    refNodo = buscarNodoPorPosicionDesdeCabeza(pos);
                    if (refNodo == NULL)
                        printf("Lo sentimos: No existe nodo en la posición %d.\n", pos);
                    else
                        insertarDespues(refNodo, valor);
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
                while (escaneoEntero(&valor) == 0);
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
                while (escaneoEntero(&desde) == 0);
                printf("Ingrese el valor a buscar: ");
                while (escaneoEntero(&valor) == 0);
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
    } while(opcion != 10);
}

// -------------------------------------------------------------------
// Función principal
int main() {
    menu();
    return 0;
}
