#include <stdio.h>
#define MAX 100

typedef struct Nodo {
    int dato;
    int sig;
} Nodo;

Nodo lista[MAX];
int cabeza = -1;
int libre = 0;

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

void inicializarLista() {
    for (int i = 0; i < MAX - 1; i++) {
        lista[i].sig = i + 1;
    }
    lista[MAX - 1].sig = -1;
}

void insertaInicio(int valor) {
    if (libre == -1) {
        printf("Error: No hay espacio disponible en la lista.\n");
        return;
    }

    int nuevo = libre;
    libre = lista[libre].sig;

    lista[nuevo].dato = valor;
    lista[nuevo].sig = cabeza;
    cabeza = nuevo;
    printf("Nodo con valor %d insertado al inicio.\n", valor);
}

void insertaFinal(int valor) {
    if (libre == -1) {
        printf("Errorr: No espacio disponible.\n");
        return;
    }

    int nuevo = libre;
    libre = lista[libre].sig;

    lista[nuevo].dato = valor;
    lista[nuevo].sig = -1;

    if (cabeza == -1) {
        cabeza = nuevo;
    } else {
        int actual = cabeza;
        while (lista[actual].sig != -1) {
            actual = lista[actual].sig;
        }
        lista[actual].sig = nuevo;
    }
    printf("Nodo con valor %d insertado al final.\n", valor);
}

void insertarEntre(int valor, int anterior) {
    if (libre == -1) {
        printf("Error no hay espacio disponible en la lista.\n");
        return;
    }

    if (anterior < 0 || anterior >= MAX || lista[anterior].sig == -1) {
        printf("Error: Nodo anterior no válido.\n");
        return;
    }

    int nuevo = libre;
    libre = lista[libre].sig;

    lista[nuevo].dato = valor;
    lista[nuevo].sig = lista[anterior].sig;
    lista[anterior].sig = nuevo;
    printf("Nodo con valor %d insertado después del nodo con valor %d.\n", valor, lista[anterior].dato);
}

void borrarInicio() {
    if (cabeza == -1) {
        printf("Error, lista vacia.\n");
        return;
    }

    int nodoBorrar = cabeza;
    cabeza = lista[cabeza].sig;
    lista[nodoBorrar].sig = libre;
    libre = nodoBorrar;
    printf("Nodo del inicio borrado (valor %d).\n", lista[nodoBorrar].dato);
}

void borrarFinal() {
    if (cabeza == -1) {
        printf("Error, lista vacia.\n");
        return;
    }

    if (lista[cabeza].sig == -1) {
        int nodoBorrar = cabeza;
        cabeza = -1;
        lista[nodoBorrar].sig = libre;
        libre = nodoBorrar;
        printf("Nodo eliminado del final con valor: %d\n", lista[nodoBorrar].dato);
        return;
    }

    int actual = cabeza;
    while (lista[lista[actual].sig].sig != -1) {
        actual = lista[actual].sig;
    }

    int nodoBorrar = lista[actual].sig;
    lista[actual].sig = -1;
    lista[nodoBorrar].sig = libre;
    libre = nodoBorrar;
    printf("Nodo eliminado del final con valor: %d\n", lista[nodoBorrar].dato);
}

void borrarNodo(int valor) {
    if (cabeza == -1) {
        printf("Lo sentimos: Lista vacía. No se puede borrar.\n");
        return;
    }

    int actual = cabeza;
    int anterior = -1;

    // Buscar el nodo con el valor especificado y el nodo anterior
    while (actual != -1 && lista[actual].dato != valor) {
        anterior = actual;
        actual = lista[actual].sig;
    }

    // Si no se encontró el nodo
    if (actual == -1) {
        printf("Lo sentimos: Nodo con valor %d no encontrado.\n", valor);
        return;
    }

    // Si es el único nodo en la lista
    if (actual == cabeza && lista[actual].sig == -1) {
        cabeza = -1;
    }
    // Si es la cabeza de la lista
    else if (actual == cabeza) {
        cabeza = lista[actual].sig;
    }
    // Si es un nodo intermedio
    else {
        lista[anterior].sig = lista[actual].sig;
    }

    lista[actual].sig = libre;
    libre = actual;

    printf("Nodo con valor %d eliminado.\n", valor);
}

void borrarUnico() {
    if (cabeza == -1) {
        printf("Lo sentimos: Lista vacía. No hay nodo que borrar.\n");
        return;
    }
    if (lista[cabeza].sig != -1) {
        printf("Lo sentimos: La lista tiene más de un nodo.\n");
        return;
    }

    printf("Nodo único borrado con valor: %d\n", lista[cabeza].dato);
    lista[cabeza].sig = libre;
    libre = cabeza;
    cabeza = -1;
}

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

int buscarNodoPorValorDesdeCabeza(int valor) {
    int actual = cabeza;
    while (actual != -1) {
        if (lista[actual].dato == valor)
            return actual;
        actual = lista[actual].sig;
    }
    return -1;
}

int buscarNodoPorPosicionDesdeCabeza(int pos) {
    int actual = cabeza;
    int count = 1;
    while (actual != -1 && count < pos) {
        actual = lista[actual].sig;
        count++;
    }
    return actual;
}

void imprimirLista() {
    int actual = cabeza;
    printf("Lista: ");
    while (actual != -1) {
        printf("%d ", lista[actual].dato);
        actual = lista[actual].sig;
    }
    printf("\n");
}

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

        int valor, pos, ref, metodo, refNodo;
        switch (opcion) {
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
                if (metodo ==1) {
                    printf("Ingrese el valor del nodo de referencia (después del cual insertar): ");
                    while (escaneoEntero(&ref) == 0);
                    refNodo = buscarNodoPorValorDesdeCabeza(ref);
                    if (refNodo == -1)
                        printf("Lo sentimos: Nodo de referencia no encontrado.\n");
                    else
                        insertarEntre(valor, refNodo);
                } else if (metodo == 2) {
                    printf("Ingrese la posición del nodo de referencia (después del cual insertar): ");
                    while (escaneoEntero(&pos) == 0);
                    refNodo = buscarNodoPorPosicionDesdeCabeza(pos);
                    if (refNodo == -1)
                        printf("Lo sentimos: No existe nodo en la posición %d.\n", pos);
                    else
                        insertarEntre(valor, refNodo);
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
                printf("Ingrese el valor a buscar: ");
                while (escaneoEntero(&valor) == 0);
                pos = buscarPosicionDesdeCabeza(valor);
                if (pos == -1)
                    printf("Elemento %d no encontrado desde la cabeza.\n", valor);
                else
                    printf("Elemento %d encontrado en la posición %d desde la cabeza.\n", valor, pos);
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

int main() {
    inicializarLista();
    menu();
    return 0;
}