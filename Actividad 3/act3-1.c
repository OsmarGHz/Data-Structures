#include <stdio.h>
#define MAX 100

typedef struct Nodo
{
    int dato;
    int sig;
} Nodo;

Nodo lista[MAX];
int cabeza = -1;
int libre = 0;

void inicializarLista(){
    for(int i = 0; i < MAX - 1; i++){
        lista [i].sig = i + 1;
    }
    lista[MAX - 1].sig = -1;
}

void insertaFInal(int valor){
    if (libre == -1){
        printf("Errorr: No espacio disponible.\n");
        return;
    }

    int nuevo = libre;
    libre = lista[libre].sig;

    lista[nuevo].dato = valor;
    lista[nuevo].sig = -1;

    if (cabeza == -1){
        cabeza = nuevo;
    } else {
        int actual = cabeza;
        while (lista[actual].sig != -1){
            actual = lista[actual].sig;
        }
        lista[actual].sig = nuevo;
    }
}

void insertarInicio(int valor){
    if (libre == -1){
        printf("Error: No hay espacio disponible en la lista.\n");
        return;
    }

    int nuevo = libre;
    libre = lista[libre].sig;

    lista[nuevo].dato = valor;
    lista[nuevo].sig = cabeza;
    cabeza = nuevo;
}

void insertarEntre (int valor, int anterior){
    if (libre == -1){
        printf("Error no hay espacio disponible en la lista.\n");
        return;
    }

    int nuevo = libre;
    libre = lista[libre].sig;
    
    lista[nuevo].dato = valor;
    lista[nuevo].sig = lista[anterior].sig;
    lista[anterior].sig = nuevo;
}

void borrarInicio(){
    if (cabeza == -1){
        printf("Error, lista vacia.\n");
        return;
    }

    int nodoBorrar = cabeza;
    cabeza = lista[cabeza].sig;
    lista[nodoBorrar].sig = libre;
    libre = nodoBorrar;
}

void borrarFinal(){
    if (cabeza == -1){
        printf("Error, lista vacia.\n");
        return;
    }

    if (lista[cabeza].sig == -1){
        int nodoBorrar = cabeza;
        cabeza = -1;
        lista[nodoBorrar].sig = libre;
        libre = nodoBorrar;
        return; 
    }

    int actual = cabeza;
    while(lista[lista[actual].sig].sig != -1){
        actual = lista[actual].sig;
    }

    int nodoBorrar = lista[actual].sig;
    lista[actual].sig = -1;
    lista[nodoBorrar].sig = libre;
    libre = nodoBorrar;
}

void borrarEntre(int anterior){
    if(lista[anterior].sig == -1){
        printf("Error no hay nodo que borrar");
        return;
    }

    int nodoBorrar = lista[anterior].sig;
    lista[anterior].sig = lista[nodoBorrar].sig;
    lista[nodoBorrar].sig = libre;
    libre = nodoBorrar;
}

int buscarElemento(int valorBuscado){
    int actual = cabeza;
    int poisicion = 1;
    while (actual != -1){
        if(lista[actual].dato == valorBuscado){
            return poisicion;
        }
        actual = lista[actual].sig;
        poisicion++;
    }
    return -1;
}

void imprimirLista(){
    int actual = cabeza;
    while (actual != -1){
        printf("%d -> ", lista[actual].dato);
        actual = lista[actual].sig;
    }
    printf("NULL\n");
}


void menu(){
    int opcion, valor, anterior, encontrado;

    do{
        printf("\n MENU \n");
        printf("1. Insertar nodo al inicio\n2. Insertar nodo al final\n3. Insertar nodo entre dos nodos\n4. Borrar nodo del inicio\n5. Borrar nodo del final\n6. Buscar un elemento\n7. Imprimir lista\n8. Salir\nSELECCIONE UNA OPCION: ");
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
                insertaFInal(valor);
                break;

            case 3:
                printf("Ingrese el valor a insertar: ");
                scanf("%d", &valor);
                printf("Ingrese el valor del nodo anterior: ");
                scanf("%d", &anterior);
                encontrado = buscarElemento(anterior);
                    if(encontrado != -1){
                        insertarEntre(valor, encontrado);
                    } else {
                        printf("Error, nodo no existente");
                    }
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
                encontrado = buscarElemento(valor);
                    if (encontrado != -1){
                        printf("Elemeno encontrado en la posicion: %d\n", encontrado);
                    } else {
                        printf("Elemento no encontrado. \n");
                    }
                break;    
            
            case 7:
                imprimirLista();
                break;

            case 8:
                printf("Saliendo del programa. \n");
                break;

            default:
                printf("Opcion no valida. \n");
                break;
        }
    }while (opcion!=8);    
}

int main(){
    inicializarLista();
    menu();
    return 0;
}