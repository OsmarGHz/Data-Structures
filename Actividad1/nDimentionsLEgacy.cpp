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
    int n;
    printf("\nDimensiones del arreglo?: ");
    scanf("%d", &n);
    int tDimension[n];          // Tamanos de las dimensiones
    int inf[n];        // Indices inferiores
    int k[n];           // Indices del elemento a buscar
    for (int i = 0; i < n; i++){
        printf("\nIngrese tamano de dimension %d: ", i+1);
        scanf("%d",&tDimension[i]);
        inf[i]=0;
    }
    unsigned long dirE = 000;    // Direccion base del arreglo
    printf("Que elemento desea buscar? Ingrese coordenadas separadas por un espacio: ");
    for (int i = 0; i < n; i++){
        scanf("%d", &k[i]);
    }
    int tElemento = 4;                    // Tamano de cada elemento (en bytes) 
    
    // CALCULA LA DIRECCION
    unsigned long direccion = calcularDireccion(n, tDimension, inf, k, dirE, tElemento);



    printf("La direccion del elemento A");
    for (int i = 0; i < n; i++){
        printf("[%d]", k[i]);
    }
    printf(" es: %lu\n",  direccion);
    

    return 0;
}
