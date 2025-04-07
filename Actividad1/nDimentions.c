#include <stdio.h>

// calcular la direccion de un elemento en un arreglo de n dimensiones
int calcularDireccion(int n, int tDimension[], int limInf[], int k[], int dirE, int tElemento) {
    int direccion = dirE;
    int factor = 1;      //factor acumulado para las dimensiones

    for (int i = n - 1; i >= 0; i--) {
        direccion += (k[i] - limInf[i]) * factor * tElemento;
        factor *= tDimension[i]; 
    }

    return direccion;
}

void ingresarBusqueda(int n, int tDimension[], int k[], int desic){
    int temp;
    printf("\nQue elemento desea buscar? Ingrese coordenadas separadas por un espacio (c/u inicia en ");
    if (desic){ printf("1");}
    else{ printf("0"); }
    printf(")");
        for (int i = 0; i < n;){
            scanf("%d", &temp);
            if(desic) temp--;
            if (temp>=0 && temp<tDimension[i]){
                k[i]=temp;
                i++;
            }else{ printf("Valor invalido. Intentelo de nuevo: ");}
        }
}

int main() {
    int n;
    int dirE = 000;    // Direccion base del arreglo
    int desic, direccion;
    int tElemento;                    // Tamano de cada elemento (en bytes)

    printf("\nNumero de Dimensiones del arreglo?: ");
    scanf("%d", &n);

    int tDimension[n];          // Tamanos de las dimensiones
    int limInf[n];        // Indices inferiores
    int k[n];           // Indices del elemento a buscar

    for (int i = 0; i < n; i++){
        printf("\nIngrese tamano de dimension %d: ", i+1);
        scanf("%d",&tDimension[i]);
        limInf[i]=0;
    }
    printf("\nNumero de Direccion inicial de la matriz? ");
    scanf("%d", &dirE);
    printf("Deseas buscar en lenguaje maquina (posicion 0) o humano (posicion 1)?");
    scanf("%d",&desic);

    ingresarBusqueda(n,tDimension,k,desic);
    
    printf("\nTamano de cada elemento en bytes: ");
    scanf("%d", &tElemento);
    
    // CALCULA LA DIRECCION
    direccion = calcularDireccion(n, tDimension, limInf, k, dirE, tElemento);


    printf("La direccion del elemento A");
    for (int i = 0; i < n; i++){
        printf("[%d]", k[i]+1);
    }
    printf(" (lo cual es A");
    for (int i = 0; i < n; i++){
        printf("[%d]", k[i]);
    }
    printf(" en lenguaje maquina), es: %lu\n",  direccion);
    

    return 0;
}
