#include <stdio.h>
#include <stdlib.h>

typedef struct{
    int * dir;
    int capacidad; //Capacidad maxima (aunque puede cambiar) de elementos
    int tamActual;
    int frente;
    int final;
} ColaCircular;

ColaCircular * startCola(int capacidadInicial){
    ColaCircular * cola = (ColaCircular*) malloc(sizeof(ColaCircular));
    if (cola == NULL){
        printf("Lo sentimos,no se pudo asignar memoria para la cola :(");
        exit(EXIT_FAILURE); //Practicamente lo mismo que exit(1);
    }
    (*cola).dir = (int *) malloc(capacidadInicial * sizeof(int));
    if( (* cola).dir == NULL ){
        printf("Lo sentimos, no se pudo asignar memoria para elementos de la cola :(");
        free(cola);
        exit(EXIT_FAILURE);
    }
    (*cola).capacidad = capacidadInicial;
    cola->frente = 0;
    cola->final = -1;
    cola->tamActual = 0;
    return cola;
}

int estaVacia(ColaCircular * cola){
    /*  if(cola->tamActual == 0) return 1;
        else return 0;
    */
    return (cola->tamActual == 0);
}

void cambiarTamanoCola(ColaCircular * cola){
    int nuevaCapacidad = ( cola->capacidad ) * 2;
    int * nuevosElementos = (int*) malloc(nuevaCapacidad*sizeof(int));
    if (nuevosElementos == NULL){
        printf("Lo sentimos, no se pudo redimensionar la cola :(");
        exit(EXIT_FAILURE);
    }
    
    for (int i = 0; i < cola -> tamActual; i++){
        nuevosElementos[i] = cola->dir[(cola->frente + i) % cola->capacidad];
    }

    free(cola->dir);
    cola->dir = nuevosElementos;
    cola->capacidad = nuevaCapacidad;
    cola->frente = 0;
    cola->final = cola->tamActual - 1;    
}

void encolar(ColaCircular* cola, int info){
    if (cola->tamActual == cola->capacidad) cambiarTamanoCola(cola);
    cola->final = (cola->final + 1) % cola->capacidad;
    cola->dir[cola->final]  = info;
    cola->tamActual++;  
}

int desencolar(ColaCircular* cola){
    int dato;
    if (estaVacia(cola)){
        printf("No puedes eliminar un elemento, cola vacia");
        exit(EXIT_FAILURE);
    }
    dato = cola->dir[cola->frente];
    cola->frente = (cola->frente + 1) % cola->capacidad;
    cola->tamActual--;
    return dato;    
}

int getFrente(ColaCircular* cola){
    if (estaVacia(cola)){
        printf("No puedes eliminar un elemento, cola vacia");
        exit(EXIT_FAILURE);
    }
    return cola->dir[cola->frente];
}

void liberarCola(ColaCircular* cola){
    free(cola->dir);
    free(cola);
}

int main(){
    ColaCircular* cola = startCola(2); //Inicia con capacidad para 2 elementos
    encolar(cola,5);
    encolar(cola,4);
    encolar(cola,3);

    printf("Elemento al frente: %d\n", getFrente(cola));

    printf("Elemento eliminado: %d\n", desencolar(cola));
    printf("Elemento eliminado: %d\n", desencolar(cola));

    encolar(cola, 50);
    encolar(cola, 60);

    printf("Elemento al frente: %d\n", getFrente(cola));

    // Mostrar los elementos restantes
    while (!estaVacia(cola)) {
        printf("Elemento eliminado: %d\n", desencolar(cola));
    }

    liberarCola(cola);
    return 0;
}