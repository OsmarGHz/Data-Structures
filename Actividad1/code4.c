#include <stdio.h>

void imprimirMatriz(int msi [], int nFilas){
    int i,j;
    printf("Esta es la matriz triangular: \n");
    for (i = 0; i < nFilas; i++){
        for (j = 0; j <= i; j++) printf("%i ",msi[(((i)*(i+1))/2) + j]);
        printf("\n");
    }
}

int determinePD(int i, int j, int dirMemoriaO, int tamE){
    return dirMemoriaO + ((( (i*(i+1)) / 2 ) + j) * tamE);
}

int main(){
    int msi[10] = {1,2,3,4,5,6,7,8,9,10};
    int dirMemoriaO = 50, i=4, j=4, iMaquina = i-1, jMaquina = j-1, tamE=2;
    imprimirMatriz(msi,4);
    printf("El PD de la posicion (%i,%i) de la matriz triangular es: %i\n",i,j,determinePD(iMaquina,jMaquina,dirMemoriaO,tamE));
}