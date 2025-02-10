//Version Original
#include <stdio.h>
#define MAX 10 // Tamaño fijo para la lista

// Estructura para un nodo
typedef struct{
    int dato;
    int sig; // Índice del siguiente nodo
    int ant; // Índice del nodo anterior
} Nodo;

// Arreglo estático para representar la lista
Nodo lista[MAX];
int cabeza = -1; // Índice de la cabeza de la lista
int libre = 0;   // Índice del siguiente espacio libre

// Inicializa la lista
void inicializarLista(){
    int i;
    for (i = 0; i < MAX - 1; i++){
        lista[i].sig = i + 1; // El índice siguiente al actual
        lista[i].ant = -1;    // No tiene ant por ahora
    }
    lista[MAX - 1].sig = -1; // Fin de la lista
    lista[MAX - 1].ant = -1;
}

void insertarInicio(int valor){
    if (libre == -1){
        printf("Lista llena. No se puede insertar.\n");
        return;
    }
    int nuevo = libre; // Toma el indice libre actual
    libre = lista[libre].sig; // Actualiza el indice libre

    lista[nuevo].ant = -1;
    lista[nuevo].dato = valor;
    lista[nuevo].sig = cabeza;

    if (cabeza != -1) lista[cabeza].ant = nuevo; //Actualizamos el anterior de la antigua cabeza, para indicar que ya es 2do elemento en vez de 1ro

    cabeza = nuevo;
}

void insertarEntre(int valor, int posAnterior){
    if (libre == -1){
        printf("Lista llena. No se puede insertar.\n");
        return;
    }
    if (posAnterior == -1){
        printf("Índice inválido para inserción entre nodos.\n");
        return;
    }
    int nuevo = libre;
    libre = lista[libre].sig;  // Actualiza el índice libre

    lista[nuevo].dato = valor;
    lista[nuevo].ant = posAnterior;
    lista[nuevo].sig = lista[posAnterior].sig; // El nuevo nodo apunta al que seguía al nodo anterior

    lista[posAnterior].sig = nuevo;           // El nodo anterior apunta al nuevo nodo
    if (lista[nuevo].sig != -1){              // Si el nuevo nodo no es el último...
        lista[lista[nuevo].sig].ant = nuevo;  // ...actualiza el enlace "ant" del siguiente nodo
    }
}

// Inserta un nodo al final
void insertaFinal(int valor){
    if (libre == -1){
        printf("Lista llena. No se puede insertar.\n");
        return;
    }
    int nuevo = libre;        // Toma el índice libre actual
    libre = lista[libre].sig; // Actualiza el índice libre
    lista[nuevo].dato = valor;
    lista[nuevo].sig = -1;
    if (cabeza == -1){
        // Si la lista está vacía
        cabeza = nuevo;
        lista[nuevo].ant = -1;
    }
    else{
        // Busca el último nodo
        int actual = cabeza;
        while (lista[actual].sig != -1){
            actual = lista[actual].sig;
        }
        lista[actual].sig = nuevo;
        lista[nuevo].ant = actual;
    }
}

int borrarInicio(){
    if (cabeza == -1){
        printf("Lista vacía. No se puede borrar.\n");
        return -1;
    }
    int nodoBorrar = cabeza;
    int valor = lista[nodoBorrar].dato;
    cabeza = lista[nodoBorrar].sig; // Actualiza la cabeza

    if (cabeza != -1) {
        lista[cabeza].ant = -1;  // La nueva cabeza no tiene anterior
    }
    // Agrega el nodo borrado a la lista de espacios libres
    lista[nodoBorrar].sig = libre;
    libre = nodoBorrar;
    return valor;
}

// Borra el nodo al final
int borrarFinal(){
    if (cabeza == -1){
        printf("Lista vacía. No se puede borrar.\n");
        return -1;
    }
    int actual = cabeza;
    int ant = -1;
    while (lista[actual].sig != -1){
        ant = actual;
        actual = lista[actual].sig;
    }
    int valor = lista[actual].dato;
    if (ant != -1){                        // hay más de un nodo como NULL
        lista[ant].sig = -1;
    }
    else{
        cabeza = -1; // La lista quedó vacía
    }
    // Regresa el nodo eliminado a la lista de espacios libres
    lista[actual].sig = libre;
    libre = actual;
    return valor;
}

// Borra un nodo que se encuentre entre dos nodos (no es ni el primero ni el último)
int borrarEntre(int pos){
    if (pos == -1){
        printf("Nodo inválido.\n");
        return -1;
    }
    // Verifica que el nodo tenga tanto un nodo anterior como un nodo siguiente
    if (lista[pos].ant == -1 || lista[pos].sig == -1){
        printf("El nodo no se encuentra entre dos nodos.\n");
        return -1;
    }
    int valor = lista[pos].dato;
    int anterior = lista[pos].ant;
    int siguiente = lista[pos].sig;
    
    // Une el nodo anterior con el siguiente
    lista[anterior].sig = siguiente;
    lista[siguiente].ant = anterior;
    
    // Agrega el nodo eliminado a la lista de espacios libres
    lista[pos].sig = libre;
    libre = pos;
    return valor;
}

// Borra el único nodo de la lista (si existe)
int borrarUnico(){
    if (cabeza == -1){
        printf("Lista vacía.\n");
        return -1;
    }
    if (lista[cabeza].sig != -1){
        printf("La lista tiene más de un nodo, no es un único nodo.\n");
        return -1;
    }
    int valor = lista[cabeza].dato;
    int nodo = cabeza;
    cabeza = -1; // La lista queda vacía
    lista[nodo].sig = libre;
    libre = nodo;
    return valor;
}

// Busca un elemento recorriendo la lista desde la cola hacia la cabeza
int buscarDesdeCola(int cabeza, int valor){
    if (cabeza == -1)
        return -1;
    // Ubica la cola: recorre la lista hasta el nodo cuyo "sig" es -1
    int actual = cabeza;
    while (lista[actual].sig != -1)
        actual = lista[actual].sig;
    // Recorre hacia atrás usando el campo "ant"
    while (actual != -1){
        if (lista[actual].dato == valor)
            return actual;
        actual = lista[actual].ant;
    }
    return -1;
}

// Función para buscar un elemento en la lista desde la cabeza
int buscarElemento(int cabeza, int valor){
    int actual = cabeza;
    while (actual != -1){ // Mientras no llegue al final
        if (lista[actual].dato == valor){
            return actual; // Retorna el índice del nodo si encuentra el valor
        }
        actual = lista[actual].sig; // Avanza al siguiente nodo
    }
    return -1; // Retorna -1 si no se encuentra el valor
}

// Imprime la lista
void imprimirLista(){
    int actual = cabeza;
    while (actual != -1){
        printf("%d -> ", lista[actual].dato);
        actual = lista[actual].sig;
    }
    printf("NULL\n");
}

void menu(){
    int opcion, valor, vAnterior, encontrado, res;

    do{
        printf("\n MENU \n");
        printf("1. Insertar nodo al inicio\n");
        printf("2. Insertar nodo al final\n");
        printf("3. Insertar nodo entre dos nodos\n");
        printf("4. Borrar nodo del inicio\n");
        printf("5. Borrar nodo del final\n");
        printf("6. Borrar nodo entre 2 nodos\n");
        printf("7. Borrar unico nodo\n");
        printf("8. Buscar un elemento\n");
        printf("9. Buscar un elemento (desde la cola)\n");
        printf("10. Imprimir lista\n");
        printf("11. Salir\n");
        printf("SELECCIONE UNA OPCION: ");
        scanf("%d", &opcion);

        switch (opcion){
            case 1:
                printf("Ingrese el valor a insertar al inicio: ");
                scanf("%d", &valor);
                insertarInicio(valor);
                break;

            case 2:
                printf("Ingrese el valor a insertar al final: ");
                scanf("%d", &valor);
                insertaFinal(valor);
                break;

            case 3:
                printf("Ingrese el valor a insertar: ");
                scanf("%d", &valor);
                printf("Ingrese el valor del nodo anterior (para insertar nuevo nodo): ");
                scanf("%d", &vAnterior);
                encontrado = buscarElemento(cabeza,vAnterior);
                    if(encontrado != -1){
                        insertarEntre(valor, encontrado);
                    } else {
                        printf("Error, el nodo anterior no existe");
                    }
                break;
            
            case 4:
                res = borrarInicio();
                if(res != -1)
                    printf("Nodo borrado al inicio con valor: %d\n", res);
                break;

            case 5:
                res = borrarFinal();
                if(res != -1)
                    printf("Nodo borrado al final con valor: %d\n", res);
                break;

            case 6:
                printf("Ingrese el valor del nodo a borrar (entre 2 nodos): ");
                scanf("%d", &valor);
                encontrado = buscarElemento(cabeza, valor);
                if(encontrado != -1){
                    res = borrarEntre(encontrado);
                    if(res != -1)
                        printf("Nodo borrado con valor: %d\n", res);
                } else {
                    printf("Error, nodo no existente o no se encuentra entre dos nodos.\n");
                }
                break;

            case 7:
                res = borrarUnico();
                if(res != -1)
                    printf("Nodo único borrado con valor: %d\n", res);
                break;

            case 8:
                printf("Ingrese el valor a buscar: ");
                scanf("%d", &valor);
                encontrado = buscarElemento(cabeza,valor);
                    if (encontrado != -1){
                        printf("Elemeno encontrado en la posicion: %d\n", encontrado);
                    } else {
                        printf("Elemento no encontrado. \n");
                    }
                break;  
                
            case 9:
                printf("Ingrese el valor a buscar (busqueda desde la cola): ");
                scanf("%d", &valor);
                encontrado = buscarDesdeCola(cabeza,valor);
                    if (encontrado != -1){
                        printf("Elemeno encontrado en la posicion: %d\n", encontrado);
                    } else {
                        printf("Elemento no encontrado. \n");
                    }
                break; 
            
            case 10:
                imprimirLista();
                break;

            case 11:
                printf("Saliendo del programa... \n");
                break;

            default:
                printf("Opcion no valida. \n");
                break;
        }
    }while (opcion!=11);    
}

// Función principal
int main(){
    inicializarLista();
    menu();
    return 0;
}