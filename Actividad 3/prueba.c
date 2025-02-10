#include <stdio.h>
#define MAX 10  // Tamaño fijo de la lista

// Estructura de un nodo de la lista doble estática
typedef struct {
    int dato;
    int sig; // Índice del siguiente nodo
    int ant; // Índice del nodo anterior
} Nodo;

// Arreglo estático que representa la lista y variables globales
Nodo lista[MAX];
int cabeza = -1; // Índice de la cabeza de la lista
int libre = 0;   // Índice del siguiente espacio libre

// -------------------------------------------------------------------
// Función: inicializarLista
// Inicializa el arreglo enlazado: crea la "lista de libres"
void inicializarLista() {
    for (int i = 0; i < MAX - 1; i++) {
        lista[i].sig = i + 1; // El siguiente libre
        lista[i].ant = -1;    // No se usa en la lista libre
    }
    lista[MAX - 1].sig = -1;
    lista[MAX - 1].ant = -1;
}

// -------------------------------------------------------------------
// Inserta un nodo al inicio de la lista
void insertaInicio(int valor) {
    if (libre == -1) {
        printf("Error: Lista llena. No se puede insertar.\n");
        return;
    }
    int nuevo = libre;
    libre = lista[nuevo].sig;  // Actualiza el primer libre

    lista[nuevo].dato = valor;
    lista[nuevo].sig = cabeza;
    lista[nuevo].ant = -1;

    if (cabeza != -1) {
        lista[cabeza].ant = nuevo;
    }
    cabeza = nuevo;
    printf("Nodo con valor %d insertado al inicio.\n", valor);
}

// -------------------------------------------------------------------
// Inserta un nodo al final de la lista
void insertaFinal(int valor) {
    if (libre == -1) {
        printf("Error: Lista llena. No se puede insertar.\n");
        return;
    }
    int nuevo = libre;
    libre = lista[nuevo].sig;

    lista[nuevo].dato = valor;
    lista[nuevo].sig = -1;
    
    if (cabeza == -1) {
        cabeza = nuevo;
        lista[nuevo].ant = -1;
    } else {
        int actual = cabeza;
        while (lista[actual].sig != -1) {
            actual = lista[actual].sig;
        }
        lista[actual].sig = nuevo;
        lista[nuevo].ant = actual;
    }
    printf("Nodo con valor %d insertado al final.\n", valor);
}

// -------------------------------------------------------------------
// Inserta un nodo DESPUÉS de un nodo de referencia (dado por su índice)
void insertarDespues(int anterior, int valor) {
    if (libre == -1) {
        printf("Error: Lista llena. No se puede insertar.\n");
        return;
    }
    int nuevo = libre;
    libre = lista[nuevo].sig;

    lista[nuevo].dato = valor;
    lista[nuevo].ant = anterior;
    lista[nuevo].sig = lista[anterior].sig;

    if (lista[anterior].sig != -1) {
        lista[lista[anterior].sig].ant = nuevo;
    }
    lista[anterior].sig = nuevo;
    printf("Nodo con valor %d insertado después del nodo en el índice %d.\n", valor, anterior);
}

// -------------------------------------------------------------------
// Borra el nodo al inicio de la lista
void borrarInicio() {
    if (cabeza == -1) {
        printf("Error: Lista vacía. No se puede borrar.\n");
        return;
    }
    int nodoBorrar = cabeza;
    cabeza = lista[cabeza].sig;
    if (cabeza != -1) {
        lista[cabeza].ant = -1;
    }
    // Se reincorpora el nodo borrado a la lista de libres
    lista[nodoBorrar].sig = libre;
    libre = nodoBorrar;
    printf("Nodo del inicio borrado (valor %d).\n", lista[nodoBorrar].dato);
}

// -------------------------------------------------------------------
// Borra el nodo al final de la lista
void borrarFinal() {
    if (cabeza == -1) {
        printf("Error: Lista vacía. No se puede borrar.\n");
        return;
    }
    int actual = cabeza;
    int ant = -1;
    while (lista[actual].sig != -1) {
        ant = actual;
        actual = lista[actual].sig;
    }
    int valor = lista[actual].dato;
    if (ant != -1) {
        lista[ant].sig = -1;
    } else {
        cabeza = -1;
    }
    lista[actual].sig = libre;
    libre = actual;
    printf("Nodo eliminado del final con valor: %d\n", valor);
}

// -------------------------------------------------------------------
// Borra el nodo que está DESPUÉS del nodo de referencia (dado por índice)
void borrarNodo(int indice) {
    if (indice == -1 || cabeza == -1) {
        printf("Error: Nodo no encontrado o lista vacía.\n");
        return;
    }

    // Si es el primer nodo (cabeza)
    if (indice == cabeza) {
        cabeza = lista[indice].sig;
        if (cabeza != -1) {
            lista[cabeza].ant = -1;
        }
    } else {
        // Ajustar los enlaces del nodo anterior y siguiente
        if (lista[indice].ant != -1) {
            lista[lista[indice].ant].sig = lista[indice].sig;
        }
        if (lista[indice].sig != -1) {
            lista[lista[indice].sig].ant = lista[indice].ant;
        }
    }

    printf("Nodo eliminado con valor: %d\n", lista[indice].dato);

    // Liberar el nodo
    lista[indice].sig = libre;
    libre = indice;
}

// -------------------------------------------------------------------
// Borra el único nodo de la lista (si es que existe solo uno)
void borrarUnico() {
    if (cabeza == -1) {
        printf("Error: Lista vacía. No hay nodo que borrar.\n");
        return;
    }
    if (lista[cabeza].sig != -1) {
        printf("Error: La lista tiene más de un nodo.\n");
        return;
    }
    int nodoBorrar = cabeza;
    int valor = lista[nodoBorrar].dato;
    cabeza = -1;
    lista[nodoBorrar].sig = libre;
    libre = nodoBorrar;
    printf("Nodo único borrado con valor: %d\n", valor);
}

// -------------------------------------------------------------------
// Funciones de Búsqueda
// Buscar por valor desde la cabeza: retorna el índice o -1 si no se encuentra.
int buscarPorValorDesdeCabeza(int valor) {
    int actual = cabeza;
    int pos = 1; // La posición lógica inicia en 1 para la cabeza
    while (actual != -1) {
        if (lista[actual].dato == valor) return pos;
        actual = lista[actual].sig;
        pos++;
    }
    return -1; // No se encontró el valor
}

// Buscar por valor desde la cola: retorna el índice o -1 si no se encuentra.
int buscarPorValorDesdeCola(int valor) {
    if (cabeza == -1) {
        return -1; // La lista está vacía
    }
    // Buscar la cola
    int actual = cabeza;
    while (lista[actual].sig != -1) {
        actual = lista[actual].sig;
    }
    // Ahora 'actual' es la cola; la posición lógica desde la cola es 1
    int pos = 1;
    while (actual != -1) {
        if (lista[actual].dato == valor)
            return pos;
        actual = lista[actual].ant;
        pos++;
    }
    return -1; // No se encontró el valor
}

// Buscar por posición desde la cabeza (posición 1 es la cabeza)
int buscarPorPosicionDesdeCabeza(int pos) {
    int actual = cabeza;
    int count = 1;
    while (actual != -1 && count < pos) {
        actual = lista[actual].sig;
        count++;
    }
    return actual;  // Devuelve -1 si la posición no existe
}

// -------------------------------------------------------------------
// Imprime la lista (de cabeza a cola)
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

// -------------------------------------------------------------------
// Menú principal (adaptado al estilo del programa simple)
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

        int valor, pos, ref, refIndex, metodo, desde;
        switch(opcion) {
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
                    refIndex = buscarPorValorDesdeCabeza(ref);
                    if (refIndex == -1) {
                        printf("Error: Nodo de referencia no encontrado.\n");
                    } else {
                        insertarDespues(refIndex, valor);
                    }
                } else if (metodo == 2) {
                    printf("Ingrese la posición del nodo de referencia (después del cual insertar): ");
                    scanf("%d", &pos);
                    refIndex = buscarPorPosicionDesdeCabeza(pos);
                    if (refIndex == -1) {
                        printf("Error: No existe nodo en la posición %d.\n", pos);
                    } else {
                        insertarDespues(refIndex, valor);
                    }
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
                refIndex = buscarPorValorDesdeCabeza(valor);
                if (refIndex == -1) {
                    printf("Error: Nodo no encontrado.\n");
                } else {
                    borrarNodo(refIndex);
                }
                break;
            case 7:
                borrarUnico();
                break;
            case 8:
                printf("Seleccione desde:\n");
                printf("1. Desde la cabeza\n");
                printf("2. Desde la cola\n");
                printf("Opción: ");
                scanf("%d", &desde);
                printf("Ingrese el valor a buscar: ");
                scanf("%d", &valor);
                if (desde == 1) {
                    refIndex = buscarPorValorDesdeCabeza(valor);
                } else if (desde == 2) {
                    refIndex = buscarPorValorDesdeCola(valor);
                } else {
                    printf("Opción no válida.\n");
                    break;
                }
                if (refIndex == -1) {
                    printf("Elemento %d no encontrado.\n", valor);
                } else {
                    printf("Elemento %d encontrado en el índice %d.\n", valor, refIndex);
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
    inicializarLista();
    menu();
    return 0;
}
