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

    if (cabeza!=-1) lista[cabeza].ant = nuevo; //Actualizamos el anterior de la antigua cabeza, para indicar que ya es 2do elemento en vez de 1ro

    cabeza = nuevo;
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

void menu(){
    int opcion, valor, vAnterior, encontrado;

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
        printf("9. Imprimir lista\n");
        printf("10. Salir\n");
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
                //insertaFInal(valor);
                break;

            case 3:
                printf("Ingrese el valor a insertar: ");
                scanf("%d", &valor);
                printf("Ingrese el valor del nodo anterior: ");
                scanf("%d", &vAnterior);
                encontrado = buscarElemento(cabeza,vAnterior);
                    if(encontrado != -1){
                        //insertarEntre(valor, encontrado);
                    } else {
                        printf("Error, nodo no existente");
                    }
                break;
            
            case 4:
                //borrarInicio();
                break;

            case 5:
                borrarFinal();
                break;

            case 6:

            case 7:

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
                imprimirLista();
                break;

            case 10:
                printf("Saliendo del programa. \n");
                break;

            default:
                printf("Opcion no valida. \n");
                break;
        }
    }while (opcion!=10);    
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
    inicializarLista();
    menu();
    return 0;
}