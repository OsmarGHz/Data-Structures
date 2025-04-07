#include <stdio.h>

// calcular la direccion de un elemento en un arreglo de n dimensiones
unsigned long calcularDireccion(int n, int tDimension[], int inf[], int k[], unsigned long dirE, int tElemento) {
    unsigned long direccion = dirE;
    unsigned long factor = 1;      //factor acumulado para las dimensiones

    for (int i = n - 1; i >= 0; i--) {
        direccion += (k[i] - inf[i]) * factor * tElemento;
        factor *= tDimension[i]; 
    }

    return direccion;
}

int main() {
    int n = 3;                    // Numero de dimensiones
    
    int tDimension[n] = {3, 4, 5};          // Tamanos de las dimensiones
    
    int inf[] = {0, 0, 0};        // Indices inferiores
    
    int k[] = {2, 3, 4};          // Indices del elemento a buscar
    
    unsigned long dirE = 000;    // Direccion base del arreglo
    
    int tElemento = 4;                    // Tamano de cada elemento (en bytes) 
    
    // CALCULA LA DIRECCION
    unsigned long direccion = calcularDireccion(n, tDimension, inf, k, dirE, tElemento);



    printf("La direccion del elemento A[2][3][4] es: %lu\n", direccion);

    return 0;
}

