import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Queue;

//Panel donde dibujaremos el arbol
class PanelGrafo extends JPanel{
    private final int RADIO_VERTICE = 25;
    private ArbolGeneral grafoBinario;

    public void setGrafo(ArbolGeneral grafoBinario) {
        this.grafoBinario = grafoBinario;
        repaint(); // Redibuja el panel
    }
}

class VerticeBinario{
    int dato;
    VerticeBinario left;
    VerticeBinario right;

    public VerticeBinario (int elemento){
        dato = elemento;
        left = null; right = null;
    }
}

class ArbolGeneral {
    private VerticeBinario root;
    
    public ArbolGeneral(int rootData) {
        // Se crea la raíz del árbol
        root = new VerticeBinario(rootData);
    }

    public VerticeBinario getRoot() {
        return root;
    }

    //Funcion para resetear el arbol
    public void reset() {
        root = null;
    }

    // Recorre el árbol en preorden para verificar si ya existe el dato.
    private boolean exists(VerticeBinario nodo, int dato) {
        if (nodo == null)
            return false;
        if (nodo.dato==dato)
            return true;
        // Primero recorre los hijos y luego los hermanos
        return exists(nodo.left, dato) || exists(nodo.right, dato);
    }

    // Inserta un nuevo nodo como hijo de 'padre' si el dato no existe en ningún nodo.
    public boolean addChild(VerticeBinario padre, int dato) {
        if (exists(root, dato)) {
            return false; // El dato ya existe.
        }
        VerticeBinario nuevo = new VerticeBinario(dato);
        if (padre.left == null) {
            padre.left = nuevo;
        } else {
            // Recorre la lista de hermanos hasta el final y añade el nuevo nodo.
            VerticeBinario current = padre.left;
            while (current.right != null) {
                current = current.right;
            }
            current.right = nuevo;
        }
        return true;
    }

    // Modifica el dato de un nodo (si se cumple que el nuevo dato no existe en el árbol).
    public boolean modifyNode(VerticeBinario nodo, int nuevoDato) {
        if (exists(root, nuevoDato)) {
            return false; // No se puede modificar porque el nuevo dato ya existe.
        }
        nodo.dato = nuevoDato;
        return true;
    }

    // Elimina el nodo que tenga 'dato' dado. Requiere pasar el nodo padre para poder reconectar la lista de hijos.
    // Esta implementación elimina el nodo y todo su subárbol.
    public boolean deleteChild(VerticeBinario padre, int dato) {
        if (padre == null || padre.left == null) {
            return false;
        }
        // Si el primer hijo es el que se desea eliminar:
        if (padre.left.dato==dato) {
            padre.left = padre.left.right; // Se elimina el nodo y se conecta su siguiente hermano (si lo tiene)
            return true;
        } else {
            // Buscar entre los hermanos
            VerticeBinario current = padre.left;
            while (current.right != null) {
                if (current.right.dato==dato) {
                    current.right = current.right.right;
                    return true;
                }
                current = current.right;
            }
        }
        return false;
    }
}

public class VistaCopia extends JFrame{
    public static ArbolGeneral grafoBinario = null;
    public static PanelGrafo panelGrafo;

    public VistaCopia(){
        super("Arbol Binario: Preorden, Inorden y Posorden");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout());

        // Toolbar en la parte lateral izquierda
        JToolBar barraHerramientas = new JToolBar();
        barraHerramientas.setFloatable(false);
        barraHerramientas.setOrientation(SwingConstants.VERTICAL);
        add(barraHerramientas, BorderLayout.WEST);

        JButton btnCrearGrafo = new JButton("Crear Grafo");
        btnCrearGrafo.addActionListener(e -> resetVistaGrafo());
        barraHerramientas.add(btnCrearGrafo);

        JButton btnAgregarHijo = new JButton("Agregar Hijo");
        btnAgregarHijo.addActionListener(e -> agregarHijo());
        barraHerramientas.add(btnAgregarHijo);

        JButton btnModificarNodo = new JButton("Modificar Nodo");
        btnModificarNodo.addActionListener(e -> modificarNodo());
        barraHerramientas.add(btnModificarNodo);
    }

    //Hacer reset en la interfaz del grafo
    public void resetVistaGrafo() {
        if (grafoBinario != null) {
            grafoBinario.reset();
        }
        panelGrafo.setGrafo(grafoBinario);
    }    

    //Agregar hijo al grafo, preguntandole al usuario el valor que desea agregar (y checando que no este repetido)
    //Ademas, se preguntara el valor del padre al que se le desea agregar el hijo.
    //Si el padre aun no tiene hijos, se preguntara si quiere agregar al hijo, como hijo izquierdo o derecho.
    public void agregarHijo() {
        String valorPadre = JOptionPane.showInputDialog("Ingrese el valor del padre:");
        String valorHijo = JOptionPane.showInputDialog("Ingrese el valor del hijo:");

        if (valorPadre != null && valorHijo != null) {
            int padre = Integer.parseInt(valorPadre);
            int hijo = Integer.parseInt(valorHijo);

            if (grafoBinario == null) {
                grafoBinario = new ArbolGeneral(padre);
            }

            VerticeBinario nodoPadre = grafoBinario.getRoot();
            if (grafoBinario.addChild(nodoPadre, hijo)) {
                JOptionPane.showMessageDialog(this, "Hijo agregado correctamente.");
            } else {
                JOptionPane.showMessageDialog(this, "El dato ya existe en el árbol.");
            }
        }
    }

    //Modificar nodo, preguntando al usuario el valor del nodo a modificar y el nuevo valor
    //Si el nodo no existe, se le informara al usuario.
    public void modificarNodo() {
        String valorNodo = JOptionPane.showInputDialog("Ingrese el valor del nodo a modificar:");
        String nuevoValor = JOptionPane.showInputDialog("Ingrese el nuevo valor:");

        if (valorNodo != null && nuevoValor != null) {
            int nodo = Integer.parseInt(valorNodo);
            int nuevo = Integer.parseInt(nuevoValor);

            VerticeBinario nodoModificar = grafoBinario.getRoot();
            if (grafoBinario.modifyNode(nodoModificar, nuevo)) {
                JOptionPane.showMessageDialog(this, "Nodo modificado correctamente.");
            } else {
                JOptionPane.showMessageDialog(this, "El nuevo dato ya existe en el árbol.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Vista vista = new Vista();
            vista.setVisible(true);
        });
    }
}