#include <stdio.h>
#define MAX 100

typedef struct Nodo {
    int dato;
    int sig;  // Usaremos -1 para indicar “no existe” en la lista libre, pero en la lista circular nunca se usa -1.
} Nodo;

Nodo lista[MAX];
int cabeza = -1;  // Índice del primer nodo de la lista (vacía si es -1)
int libre = 0;    // Índice del primer nodo libre

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

// Inicializa el arreglo de nodos, armando la lista de libres
void inicializarLista(){
    for (int i = 0; i < MAX - 1; i++){
        lista[i].sig = i + 1;
    }
    lista[MAX - 1].sig = -1;
    cabeza = -1;
    libre = 0;
}

// Inserta un nodo al inicio de la lista circular
void insertarInicio(int valor){
    if (libre == -1){
        printf("Error: No hay espacio disponible en la lista.\n");
        return;
    }
    int nuevo = libre;
    libre = lista[libre].sig;  // Actualiza la lista de libres
    lista[nuevo].dato = valor;
    if (cabeza == -1){
        // La lista está vacía; el nuevo nodo se apunta a sí mismo.
        lista[nuevo].sig = nuevo;
        cabeza = nuevo;
    } else {
        // Se busca el último nodo para actualizar su enlace
        int ultimo = cabeza;
        while (lista[ultimo].sig != cabeza)
            ultimo = lista[ultimo].sig;
        lista[nuevo].sig = cabeza;
        cabeza = nuevo;
        lista[ultimo].sig = cabeza;
    }
}

// Inserta un nodo al final de la lista circular
void insertaFinal(int valor){
    if (libre == -1){
        printf("Error: No hay espacio disponible en la lista.\n");
        return;
    }
    int nuevo = libre;
    libre = lista[libre].sig;
    lista[nuevo].dato = valor;
    if (cabeza == -1){
        lista[nuevo].sig = nuevo;
        cabeza = nuevo;
    } else {
        int ultimo = cabeza;
        while (lista[ultimo].sig != cabeza)
            ultimo = lista[ultimo].sig;
        lista[ultimo].sig = nuevo;
        lista[nuevo].sig = cabeza;
    }
}

// Inserta un nodo después del nodo que contenga el valor "anterior"
void insertarEntre(int valor, int anterior){
    // Buscar el nodo cuyo dato sea igual a 'anterior'
    if (cabeza == -1){
        printf("Error: Lista vacia.\n");
        return;
    }
    int actual = cabeza;
    int encontrado = -1;
    do {
        if (lista[actual].dato == anterior) {
            encontrado = actual;
            break;
        }
        actual = lista[actual].sig;
    } while (actual != cabeza);

    if (encontrado == -1){
        printf("Error: Nodo con valor %d no encontrado.\n", anterior);
        return;
    }
    if (libre == -1){
        printf("Error: No hay espacio disponible en la lista.\n");
        return;
    }
    int nuevo = libre;
    libre = lista[libre].sig;
    lista[nuevo].dato = valor;
    lista[nuevo].sig = lista[encontrado].sig;
    lista[encontrado].sig = nuevo;
}

// Borra el nodo del inicio de la lista circular
void borrarInicio(){
    if (cabeza == -1){
        printf("Error, lista vacia.\n");
        return;
    }
    int nodoBorrar = cabeza;
    if (lista[cabeza].sig == cabeza){
        // Sólo hay un nodo
        cabeza = -1;
    } else {
        // Buscar el último nodo para actualizar su enlace
        int ultimo = cabeza;
        while (lista[ultimo].sig != cabeza)
            ultimo = lista[ultimo].sig;
        cabeza = lista[cabeza].sig;
        lista[ultimo].sig = cabeza;
    }
    // Se reincorpora el nodo borrado a la lista de libres
    lista[nodoBorrar].sig = libre;
    libre = nodoBorrar;
}

// Borra el nodo del final de la lista circular
void borrarFinal(){
    if (cabeza == -1){
        printf("Error, lista vacia.\n");
        return;
    }
    int nodoBorrar;
    if (lista[cabeza].sig == cabeza){
        // Sólo hay un nodo
        nodoBorrar = cabeza;
        cabeza = -1;
    } else {
        int actual = cabeza;
        // Se busca el nodo que precede al último (aquel cuyo siguiente apunta a cabeza)
        while (lista[lista[actual].sig].sig != cabeza)
            actual = lista[actual].sig;
        nodoBorrar = lista[actual].sig;
        lista[actual].sig = cabeza;
    }
    lista[nodoBorrar].sig = libre;
    libre = nodoBorrar;
}

// BORRAR ENTRE: Borra un nodo (que no sea el inicio) que contenga el valor especificado.
void borrarEntre(){
    if (cabeza == -1){
        printf("Error, lista vacia.\n");
        return;
    }
    int valor;
    printf("Ingrese el valor del nodo a borrar (no puede ser el inicio): ");
    while (escaneoEntero(&valor) == 0);

    // Si el nodo a borrar es el inicio, se indica que use la opción correspondiente.
    if (lista[cabeza].dato == valor){
        printf("El nodo a borrar es el inicio. Use la opción de borrar nodo del inicio.\n");
        return;
    }

    int anterior = cabeza;
    int actual = lista[cabeza].sig;
    // Recorre la lista hasta volver a la cabeza
    while (actual != cabeza && lista[actual].dato != valor){
        anterior = actual;
        actual = lista[actual].sig;
    }
    if (actual == cabeza){
        printf("Nodo con valor %d no encontrado (o es el inicio).\n", valor);
        return;
    }
    // Se elimina el nodo encontrado
    lista[anterior].sig = lista[actual].sig;
    // Se reincorpora el nodo borrado a la lista de libres
    lista[actual].sig = libre;
    libre = actual;
    printf("Nodo con valor %d eliminado.\n", valor);
}

// Busca un elemento en la lista; retorna el índice del nodo (dentro del arreglo) o -1 si no se encuentra.
int buscarElemento(int valorBuscado) {
    if (cabeza == -1) return -1;
    int actual = cabeza;
    do {
        if (lista[actual].dato == valorBuscado)
            return actual;
        actual = lista[actual].sig;
    } while (actual != cabeza);
    return -1;
}

// Imprime la lista circular
void imprimirLista(){
    if (cabeza == -1){
        printf("Lista vacia.\n");
        return;
    }
    int actual = cabeza;
    do {
        printf("%d -> ", lista[actual].dato);
        actual = lista[actual].sig;
    } while (actual != cabeza);
    printf("(circular)\n");
}

// Menú interactivo
void menu(){
    int opcion, valor, anterior, encontrado;
    do {
        printf("\nMENU\n");
        printf("1. Insertar nodo al inicio\n");
        printf("2. Insertar nodo al final\n");
        printf("3. Insertar nodo entre dos nodos\n");
        printf("4. Borrar nodo del inicio\n");
        printf("5. Borrar nodo del final\n");
        printf("6. Borrar nodo entre dos nodos\n");
        printf("7. Buscar un elemento\n");
        printf("8. Imprimir lista\n");
        printf("9. Salir\n");
        printf("Seleccione una opcion: ");
        while (escaneoEntero(&opcion) == 0);

        switch (opcion){
            case 1:
                printf("Ingrese el valor a insertar al inicio: ");
                while (escaneoEntero(&valor) == 0);
                insertarInicio(valor);
                break;
            case 2:
                printf("Ingrese el valor a insertar al final: ");
                while (escaneoEntero(&valor) == 0);
                insertaFinal(valor);
                break;
            case 3:
                printf("Ingrese el valor a insertar: ");
                while (escaneoEntero(&valor) == 0);
                printf("Ingrese el valor del nodo anterior: ");
                while (escaneoEntero(&anterior) == 0);
                insertarEntre(valor, anterior);
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
                encontrado = buscarElemento(valor);
                if (encontrado != -1)
                    printf("Elemento encontrado en la posicion (indice): %d\n", encontrado);
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
    } while (opcion != 9);
}

int main(){
    inicializarLista();
    menu();
    return 0;
}
