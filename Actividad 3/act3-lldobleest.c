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
    if (ant != -1){                        // hay más de un nodo
        lista[ant].sig = -1; // como NULL
    }
    else{
        cabeza = -1; // La lista quedó vacía
    }
    return valor;
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
        printf("%d ", lista[actual].dato);
        actual = lista[actual].sig;
    }
    printf("\n");
}

// Función principal
int main(){
    /*
    inicializarLista();
    insertaFinal(5);
    insertaFinal(10);
    insertaFinal(15);
    printf("Lista después de insertar: ");
    imprimirLista();
    int eliminado = borrarFinal();
    printf("Nodo eliminado con valor: %d\n", eliminado);
    printf("Lista después de borrar: ");
    imprimirLista();
    // Buscar un elemento en la lista
    int buscarValor = 10;
    int indiceEncontrado = buscarElemento(cabeza, buscarValor);
    if (indiceEncontrado != -1){
        printf("Elemento %d encontrado en el índice %d.\n", buscarValor, indiceEncontrado);
    }
    else{
        printf("Elemento %d no encontrado en la lista.\n", buscarValor);
    }
    */
    return 0;
}