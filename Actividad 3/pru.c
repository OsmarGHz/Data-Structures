#include <stdio.h>
#include <stdlib.h>

// Definición de la estructura del nodo.
typedef struct Nodo {
    int dato;
    struct Nodo *sig;
} Nodo;

Nodo *cabeza = NULL;  // Puntero al primer nodo de la lista.

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

// Función que busca un elemento en la lista. Devuelve el puntero al nodo encontrado o NULL.
Nodo* buscarElemento(int valorBuscado) {
    Nodo *actual = cabeza;
    while (actual != NULL) {
        if (actual->dato == valorBuscado)
            return actual;
        actual = actual->sig;
    }
    return NULL;
}

//Retorna la posicon del valor ingresado 
int buscar(int valorBuscado){
    if(cabeza == NULL)
        return -1;
    Nodo *actual = cabeza;
    int posicion = 1;
    do {
        if(actual->dato == valorBuscado)
            return posicion;
        actual = actual->sig;
        posicion++;
    } while(actual != cabeza);
    return -1;
}

// Inserta un nodo al inicio de la lista.
void insertarInicio(int valor) {
    Nodo *nuevo = (Nodo *)malloc(sizeof(Nodo));
    if (nuevo == NULL) {
        printf("Error de asignacion de memoria.\n");
        return;
    }
    nuevo->dato = valor;
    nuevo->sig = cabeza;
    cabeza = nuevo;
}

// Inserta un nodo al final de la lista.
void insertarFinal(int valor) {
    Nodo *nuevo = (Nodo *)malloc(sizeof(Nodo));
    if (nuevo == NULL) {
        printf("Error de asignacion de memoria.\n");
        return;
    }
    nuevo->dato = valor;
    nuevo->sig = NULL;
    
    if (cabeza == NULL) {
        cabeza = nuevo;
    } else {
        Nodo *actual = cabeza;
        while (actual->sig != NULL)
            actual = actual->sig;
        actual->sig = nuevo;
    }
}

// Inserta un nodo entre dos nodos
void insertarEntre(int valor, int valorAnterior) {
    Nodo *anteriorNodo = buscarElemento(valorAnterior);
    if (anteriorNodo == NULL) {
        printf("Error: Nodo anterior no existe.\n");
        return;
    }
    if (anteriorNodo->sig == NULL) {
        printf("Error: Nodo anterior es el último. Use la opcion en el menu para insertar al final (2).\n");
        return;
    }
    
    Nodo *nuevo = (Nodo *)malloc(sizeof(Nodo));
    if (nuevo == NULL) {
        printf("Error de asignacion de memoria.\n");
        return;
    }
    nuevo->dato = valor;
    nuevo->sig = anteriorNodo->sig;
    anteriorNodo->sig = nuevo;
}

// Borra el nodo del inicio de la lista.
void borrarInicio() {
    if (cabeza == NULL) {
        printf("Error: Lista vacia.\n");
        return;
    }
    Nodo *nodoBorrar = cabeza;
    cabeza = cabeza->sig;
    free(nodoBorrar);
}

// Borra el nodo final de la lista.
void borrarFinal() {
    if (cabeza == NULL) {
        printf("Error: Lista vacia.\n");
        return;
    }
    if (cabeza->sig == NULL) {  // Solo hay un nodo en la lista.
        free(cabeza);
        cabeza = NULL;
        return;
    }
    
    Nodo *actual = cabeza;
    while (actual->sig->sig != NULL)
        actual = actual->sig;
    
    // actual->sig es el último nodo.
    free(actual->sig);
    actual->sig = NULL;
}

// Borra un nodo que se encuentre entre dos nodos 
void borrarEntreNodo(int valor) {
    if (cabeza == NULL) {
        printf("Error: Lista vacia.\n");
        return;
    }
    if (cabeza->dato == valor) {
        printf("Error: El nodo a borrar es el primero. Use la opcion en el menu para borrar inicio (4)\n");
        return;
    }
    
    Nodo *prev = cabeza;
    // Buscar el nodo cuyo siguiente nodo tenga el dato buscado.
    while (prev->sig != NULL && prev->sig->dato != valor)
        prev = prev->sig;
    
    if (prev->sig == NULL) {
        printf("Error: Nodo no encontrado.\n");
        return;
    }
    
    // Si el nodo a borrar es el último, se indica que se debe usar borrarFinal.
    if (prev->sig->sig == NULL) {
        printf("Error: El nodo a borrar es el ultimo. Use la opcion en el menu para borrar final (5).\n");
        return;
    }
    
    Nodo *nodoBorrar = prev->sig;
    prev->sig = nodoBorrar->sig;
    free(nodoBorrar);
}

// Imprime los elementos de la lista.
void imprimirLista() {
    Nodo *actual = cabeza;
    while (actual != NULL) {
        printf("%d -> ", actual->dato);
        actual = actual->sig;
    }
    printf("ERROR: Lista vacia\n");
}

// Función que muestra el menú 
void menu() {
    int opcion;
    int valor, valorAnterior, pos;
    Nodo *encontrado;
    char input[100];  // Buffer para leer la opción ingresada

    do {
        printf("\n MENU \n");
        printf("1. Insertar nodo al inicio\n");
        printf("2. Insertar nodo al final\n");
        printf("3. Insertar nodo entre dos nodos\n");
        printf("4. Borrar nodo del inicio\n");
        printf("5. Borrar nodo del final\n");
        printf("6. Buscar un elemento\n");
        printf("7. Imprimir lista\n");
        printf("8. Borrar nodo entre dos nodos\n");
        printf("9. Salir\n");
        printf("SELECCIONE UNA OPCION: ");
        while (escaneoEntero(&opcion) == 0);

        switch (opcion) {
            case 1:
                printf("Ingrese el valor a insertar al inicio: ");
                while (escaneoEntero(&valor) == 0);
                insertarInicio(valor);
                break;
            case 2:
                printf("Ingrese el valor a insertar al final: ");
                while (escaneoEntero(&valor) == 0);
                insertarFinal(valor);
                break;
            case 3:
                printf("Ingrese el valor a insertar: ");
                while (escaneoEntero(&valor) == 0);
                printf("Ingrese el valor del nodo anterior (a la izquierda): ");
                while (escaneoEntero(&valorAnterior) == 0);
                insertarEntre(valor, valorAnterior);
                break;
            case 4:
                borrarInicio();
                break;
            case 5:
                borrarFinal();
                break;
            case 6:
                printf("Ingrese el valor a buscar: ");
                while (escaneoEntero(&valor) == 0);
                pos = buscar(valor);
                if (pos != -1)
                    printf("Elemento %d encontrado en la posicion %d.\n", valor, pos);
                else
                    printf("Elemento no encontrado.\n");
                break;
            case 7:
                imprimirLista();
                break;
            case 8:
                printf("Ingrese el valor del nodo a borrar (entre dos nodos): ");
                while (escaneoEntero(&valor) == 0);
                borrarEntreNodo(valor);
                break;
            case 9:
                printf("Saliendo del programa.\n");
                break;
            default:
                printf("Opcion no valida.\n");
                break;
        }
    } while (opcion != 9);
}

int main() {
    menu();
    return 0;
}
