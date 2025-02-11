#include <stdio.h>
#include <stdlib.h>

typedef struct Nodo {
    int dato;
    struct Nodo *sig;
} Nodo;

Nodo *cabeza = NULL;  // Puntero al primer nodo de la lista

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

// Inserta un nodo al inicio de la lista circular
void insertarInicio(int valor){
    Nodo *nuevo = (Nodo*) malloc(sizeof(Nodo));
    if(nuevo == NULL) {
        printf("Error: No se pudo asignar memoria.\n");
        return;
    }
    nuevo->dato = valor;
    if(cabeza == NULL){
        nuevo->sig = nuevo;
        cabeza = nuevo;
    } else {
        // Se busca el último nodo
        Nodo *ultimo = cabeza;
        while(ultimo->sig != cabeza)
            ultimo = ultimo->sig;
        nuevo->sig = cabeza;
        cabeza = nuevo;
        ultimo->sig = cabeza;
    }
}

// Inserta un nodo al final de la lista circular
void insertarFinal(int valor){
    Nodo *nuevo = (Nodo*) malloc(sizeof(Nodo));
    if(nuevo == NULL){
        printf("Error: No se pudo asignar memoria.\n");
        return;
    }
    nuevo->dato = valor;
    if(cabeza == NULL){
        nuevo->sig = nuevo;
        cabeza = nuevo;
    } else {
        Nodo *ultimo = cabeza;
        while(ultimo->sig != cabeza)
            ultimo = ultimo->sig;
        ultimo->sig = nuevo;
        nuevo->sig = cabeza;
    }
}

// Inserta un nodo después del nodo que contenga el valor "referencia"
void insertarEntre(int valor, int referencia){
    if(cabeza == NULL){
        printf("Error: Lista vacia.\n");
        return;
    }
    Nodo *actual = cabeza;
    Nodo *encontrado = NULL;
    do {
        if(actual->dato == referencia){
            encontrado = actual;
            break;
        }
        actual = actual->sig;
    } while(actual != cabeza);

    if(encontrado == NULL){
        printf("Error: Nodo con valor %d no encontrado.\n", referencia);
        return;
    }
    Nodo *nuevo = (Nodo*) malloc(sizeof(Nodo));
    if(nuevo == NULL){
        printf("Error: No se pudo asignar memoria.\n");
        return;
    }
    nuevo->dato = valor;
    nuevo->sig = encontrado->sig;
    encontrado->sig = nuevo;
}

// Borra el nodo del inicio de la lista circular
void borrarInicio(){
    if(cabeza == NULL){
        printf("Error: Lista vacia.\n");
        return;
    }
    if(cabeza->sig == cabeza){
        free(cabeza);
        cabeza = NULL;
    } else {
        // Se busca el último nodo
        Nodo *ultimo = cabeza;
        while(ultimo->sig != cabeza)
            ultimo = ultimo->sig;
        Nodo *nodoBorrar = cabeza;
        cabeza = cabeza->sig;
        ultimo->sig = cabeza;
        free(nodoBorrar);
    }
}

// Borra el nodo del final de la lista circular
void borrarFinal(){
    if(cabeza == NULL){
        printf("Error: Lista vacia.\n");
        return;
    }
    if(cabeza->sig == cabeza){
        free(cabeza);
        cabeza = NULL;
    } else {
        Nodo *actual = cabeza;
        // Se busca el nodo anterior al último
        while(actual->sig->sig != cabeza)
            actual = actual->sig;
        Nodo *nodoBorrar = actual->sig;
        actual->sig = cabeza;
        free(nodoBorrar);
    }
}

// Borra un nodo intermedio de la lista circular (no se puede borrar el nodo del inicio)
void borrarEntre(){
    if(cabeza == NULL){
        printf("Error: Lista vacia.\n");
        return;
    }
    int valor;
    printf("Ingrese el valor del nodo a borrar (no puede ser el nodo del inicio): ");
    while(escaneoEntero(&valor) == 0);

    // Si el nodo a borrar es el nodo de inicio, se indica que use la opción correspondiente.
    if(cabeza->dato == valor){
        printf("El nodo a borrar es el inicio. Use la opción de borrar nodo del inicio.\n");
        return;
    }

    Nodo *prev = cabeza;
    Nodo *actual = cabeza->sig;
    while(actual != cabeza && actual->dato != valor){
        prev = actual;
        actual = actual->sig;
    }

    if(actual == cabeza){
        printf("Nodo con valor %d no encontrado.\n", valor);
        return;
    }

    prev->sig = actual->sig;
    free(actual);
    printf("Nodo con valor %d eliminado.\n", valor);
}

// Busca un elemento en la lista y retorna su posición (1, 2, …) o -1 si no se encuentra.
int buscarElemento(int valorBuscado){
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

// Imprime la lista circular
void imprimirLista(){
    if(cabeza == NULL){
        printf("Lista vacia.\n");
        return;
    }
    Nodo *actual = cabeza;
    do {
        printf("%d -> ", actual->dato);
        actual = actual->sig;
    } while(actual != cabeza);
    printf("(circular)\n");
}

// Menú interactivo
void menu(){
    int opcion, valor, referencia, pos;
    do {
        printf("\nMENU\n");
        printf("1. Insertar nodo al inicio\n");
        printf("2. Insertar nodo al final\n");
        printf("3. Insertar nodo entre dos nodos (por referencia)\n");
        printf("4. Borrar nodo del inicio\n");
        printf("5. Borrar nodo del final\n");
        printf("6. Borrar nodo entre dos nodos\n");
        printf("7. Buscar un elemento\n");
        printf("8. Imprimir lista\n");
        printf("9. Salir\n");
        printf("Seleccione una opcion: ");
        while (escaneoEntero(&opcion) == 0);

        switch(opcion){
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
                printf("Ingrese el valor de referencia (despues del cual insertar): ");
                while (escaneoEntero(&referencia) == 0);
                insertarEntre(valor, referencia);
                break;
            case 4:
                borrarInicio();
                break;
            case 5:
                borrarFinal();
                break;
            case 6:
                borrarEntre();
                break;
            case 7:
                printf("Ingrese el valor a buscar: ");
                while (escaneoEntero(&valor) == 0);
                pos = buscarElemento(valor);
                if(pos != -1)
                    printf("Elemento encontrado en la posicion: %d\n", pos);
                else
                    printf("Elemento no encontrado.\n");
                break;
            case 8:
                imprimirLista();
                break;
            case 9:
                printf("Saliendo del programa.\n");
                break;
            default:
                printf("Opcion no valida.\n");
                break;
        }
    } while(opcion != 9);
}

int main(){
    menu();
    return 0;
}
