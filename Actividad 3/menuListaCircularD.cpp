#include <stdio.h>
#include <stdlib.h>

typedef struct Nodo {
    int dato;
    struct Nodo *sig;
} Nodo;

Nodo *cabeza = NULL;  // Puntero al primer nodo de la lista

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
        printf("6. Buscar un elemento\n");
        printf("7. Imprimir lista\n");
        printf("8. Salir\n");
        printf("Seleccione una opcion: ");
        scanf("%d", &opcion);

        switch(opcion){
            case 1:
                printf("Ingrese el valor a insertar al inicio: ");
                scanf("%d", &valor);
                insertarInicio(valor);
                break;
            case 2:
                printf("Ingrese el valor a insertar al final: ");
                scanf("%d", &valor);
                insertarFinal(valor);
                break;
            case 3:
                printf("Ingrese el valor a insertar: ");
                scanf("%d", &valor);
                printf("Ingrese el valor de referencia (despues del cual insertar): ");
                scanf("%d", &referencia);
                insertarEntre(valor, referencia);
                break;
            case 4:
                borrarInicio();
                break;
            case 5:
                borrarFinal();
                break;
            case 6:
                printf("Ingrese el valor a buscar: ");
                scanf("%d", &valor);
                pos = buscarElemento(valor);
                if(pos != -1)
                    printf("Elemento encontrado en la posicion: %d\n", pos);
                else
                    printf("Elemento no encontrado.\n");
                break;
            case 7:
                imprimirLista();
                break;
            case 8:
                printf("Saliendo del programa.\n");
                break;
            default:
                printf("Opcion no valida.\n");
                break;
        }
    } while(opcion != 8);
}

int main(){
    menu();
    return 0;
}
