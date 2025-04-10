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
    private final int RADIO_VERTICE = 50;
    private ArbolGeneral grafoBinario;

    //Se generara un panel en donde, al dibujar el arbol, los nodos se acomodaran como si fuera un arbol binario real
    //Los nodos tendran un tamano fijo relativamente grande. En caso de requerir mas espacio, se usara scroll horitontal y vertical en el panel
    
    public PanelGrafo() {
        setPreferredSize(new Dimension(1000, 700));
        setBackground(Color.WHITE);
        setLayout(null); // Usar diseño nulo para posicionar los nodos manualmente
    }

    //Funcion para agregar el arbol al panel
    public void setGrafo(ArbolGeneral grafo) {
        this.grafoBinario = grafo;
        repaint(); // Llama a paintComponent para redibujar el panel
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (grafoBinario != null && grafoBinario.root != null) {
            dibujarArbol(g, grafoBinario.root, getWidth() / 2, 50, getWidth() / 4);
        }
    }

    private void dibujarArbol(Graphics g, VerticeBinario nodo, int x, int y, int offset) {
        if (nodo != null) {
            g.setFont(new Font("Arial", Font.BOLD, 16));

            // Dibuja el nodo
            g.setColor(new Color(0xFFAA00));
            g.fillOval(x - RADIO_VERTICE / 2, y - RADIO_VERTICE / 2, RADIO_VERTICE, RADIO_VERTICE);
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(nodo.dato), x - 5, y + 5);

            // Dibuja las conexiones con los hijos
            if (nodo.left != null) {
                g.setColor(Color.BLACK);
                g.drawLine(x, y + RADIO_VERTICE / 2, x - offset, y + 50 - RADIO_VERTICE / 2);
                dibujarArbol(g, nodo.left, x - offset, y + 50, offset / 2);
            }
            if (nodo.right != null) {
                g.setColor(Color.BLACK);
                g.drawLine(x, y + RADIO_VERTICE / 2, x + offset, y + 50 - RADIO_VERTICE / 2);
                dibujarArbol(g, nodo.right, x + offset, y + 50, offset / 2);
            }
        }
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
    public VerticeBinario root;

    // Constructor para crear un árbol binario vacío
    public ArbolGeneral() {
        root = null;
    }
    
    // Constructor para crear un árbol binario con un nodo
    public ArbolGeneral(int rootData) {
        // Se crea la raíz del árbol
        root = new VerticeBinario(rootData);
    }

    //Funcion para resetear el arbol
    public void reset() {
        root = null;
    }

    //Funcion para agregar un 1er nodo al arbol
    public boolean agregarPrimerNodo(int dato) {
        if (root == null) {
            root = new VerticeBinario(dato); // Si el árbol está vacío, se crea la raíz
            return true;
        } else {
            return false;
        }
    }

    //Funcion para agregar un hijo al arbol al lado izquierdo del nodo padre
    public boolean agregarHijoIzquierdo(int padre, int hijo) {
        VerticeBinario nodoPadre = buscarNodo(root, padre);
        if (nodoPadre != null && nodoPadre.left == null) {
            nodoPadre.left = new VerticeBinario(hijo);
            return true;
        }
        return false;
    }

    //Funcion para agregar un hijo al arbol al lado derecho del nodo padre
    public boolean agregarHijoDerecho(int padre, int hijo) {
        VerticeBinario nodoPadre = buscarNodo(root, padre);
        if (nodoPadre != null && nodoPadre.right == null) {
            nodoPadre.right = new VerticeBinario(hijo);
            return true;
        }
        return false;
    }

    //Funcion para buscar un nodo en el arbol
    public VerticeBinario buscarNodo(VerticeBinario nodo, int dato) {
        if (nodo == null) {
            return null;
        }
        if (nodo.dato == dato) {
            return nodo; // Nodo encontrado
        }
        VerticeBinario encontrado = buscarNodo(nodo.left, dato);
        if (encontrado != null) {
            return encontrado; // Nodo encontrado en el subárbol izquierdo
        }
        return buscarNodo(nodo.right, dato); // Buscar en el subárbol derecho
    }

    //Funcion para eliminar un nodo del arbol
    public boolean eliminarNodo(VerticeBinario nodo, int dato) {
        if (nodo == null) {
            return false;
        }
        if (nodo.dato == dato) {
            nodo = null; // Eliminar el nodo
            return true;
        }
        boolean eliminadoIzquierdo = eliminarNodo(nodo.left, dato);
        if (eliminadoIzquierdo) {
            nodo.left = null; // Eliminar el hijo izquierdo
            return true;
        }
        boolean eliminadoDerecho = eliminarNodo(nodo.right, dato);
        if (eliminadoDerecho) {
            nodo.right = null; // Eliminar el hijo derecho
            return true;
        }
        return false;
    }


}

public class Vista extends JFrame{
    public static ArbolGeneral grafoBinario = null;
    public static PanelGrafo panelGrafo;

    public Vista(){
        super("Arbol Binario: Preorden, Inorden y Posorden");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
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
        btnCrearGrafo.setBackground(new Color(0xFFCC00));
        btnCrearGrafo.setBorderPainted(false);
        btnCrearGrafo.setFocusPainted(false);
        btnCrearGrafo.setOpaque(true);
        btnCrearGrafo.setFont(new Font("Arial", Font.BOLD, 14));
        btnCrearGrafo.setForeground(Color.BLACK);
        barraHerramientas.add(btnCrearGrafo);

        JButton btnAgregarHijo = new JButton("Agregar Hijo");
        btnAgregarHijo.addActionListener(e -> agregarHijo());
        btnAgregarHijo.setBackground(new Color(0xFFAA00));
        btnAgregarHijo.setBorderPainted(false);
        btnAgregarHijo.setFocusPainted(false);
        btnAgregarHijo.setOpaque(true);
        btnAgregarHijo.setFont(new Font("Arial", Font.BOLD, 14));
        btnAgregarHijo.setForeground(Color.BLACK);
        barraHerramientas.add(btnAgregarHijo);

        JButton btnModificarNodo = new JButton("Modificar Nodo");
        btnModificarNodo.setBackground(new Color(0xFF8800));
        btnModificarNodo.setBorderPainted(false);
        btnModificarNodo.setFocusPainted(false);
        btnModificarNodo.setOpaque(true);
        btnModificarNodo.setFont(new Font("Arial", Font.BOLD, 14));
        btnModificarNodo.setForeground(Color.BLACK);
        btnModificarNodo.addActionListener(e -> modificarNodo());
        barraHerramientas.add(btnModificarNodo);

        JButton btnEliminarNodo = new JButton("Eliminar Nodo");
        btnEliminarNodo.addActionListener(e -> eliminarNodoEHijos());
        barraHerramientas.add(btnEliminarNodo);

        // JButton btnRecorrerPreorden = new JButton("Recorrer Preorden");
        // btnRecorrerPreorden.addActionListener(e -> recorrerPreorden());
        // barraHerramientas.add(btnRecorrerPreorden);

        // JButton btnRecorrerInorden = new JButton("Recorrer Inorden");
        // btnRecorrerInorden.addActionListener(e -> recorrerInorden());
        // barraHerramientas.add(btnRecorrerInorden);

        // JButton btnRecorrerPosorden = new JButton("Recorrer Posorden");
        // btnRecorrerPosorden.addActionListener(e -> recorrerPosorden());
        // barraHerramientas.add(btnRecorrerPosorden);

        // JButton btnGuardar = new JButton("Guardar Grafo");
        // btnGuardar.addActionListener(e -> guardarGrafo());
        // barraHerramientas.add(btnGuardar);

        // JButton btnCargar = new JButton("Cargar Grafo");
        // btnCargar.addActionListener(e -> cargarGrafo());
        // barraHerramientas.add(btnCargar);

        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> System.exit(0));
        barraHerramientas.add(btnSalir);

        grafoBinario = new ArbolGeneral();
        panelGrafo = new PanelGrafo();
        panelGrafo.setGrafo(grafoBinario);
        add(panelGrafo, BorderLayout.CENTER);

        //Scrollbar para el panel
        JScrollPane scrollPane = new JScrollPane(panelGrafo);
        scrollPane.setPreferredSize(new Dimension(1000, 700));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);


    }

    //Funcion para reiniciar el arbol
    private void resetVistaGrafo() {
        grafoBinario.reset();
        panelGrafo.setGrafo(grafoBinario);
        JOptionPane.showMessageDialog(this, "Arbol reiniciado.");
    }

    //Funcion para agregar un hijo al arbol. Se preguntara si se inserta a la izquierda o a la derecha del padre
    private void agregarHijo() {
        if (grafoBinario.root == null) {
            String raizStr = JOptionPane.showInputDialog(this, "Ingrese el valor del nodo raíz:");
            if (raizStr != null) {
                int raiz = Integer.parseInt(raizStr);
                grafoBinario.agregarPrimerNodo(raiz);
                panelGrafo.setGrafo(grafoBinario);
            }else{
                JOptionPane.showMessageDialog(this, "No se puede agregar un hijo sin un nodo raíz.");
                return;
            }
            return;
        }

        String padreStr = JOptionPane.showInputDialog(this, "Ingrese el nodo padre:");
        String hijoStr = JOptionPane.showInputDialog(this, "Ingrese el nodo hijo:");
        if (padreStr != null && hijoStr != null) {
            int padre = Integer.parseInt(padreStr);
            //Se busca si existe el nodo padre en el arbol
            if(grafoBinario.buscarNodo(grafoBinario.root, padre) == null){
                JOptionPane.showMessageDialog(this, "El nodo padre no existe en el arbol.");
                return;
            }
            int hijo = Integer.parseInt(hijoStr);
            //Se busca si ya existe el valor del nodo hijo en el arbol
            if(grafoBinario.buscarNodo(grafoBinario.root, hijo) != null){
                JOptionPane.showMessageDialog(this, "El nodo hijo ya existe en el arbol.");
                return;
            }else{
                String lado = JOptionPane.showInputDialog(this, "¿Izquierda o Derecha? (I/D):");
                if (lado != null) {
                    if (lado.equalsIgnoreCase("I")) {
                        if(grafoBinario.agregarHijoIzquierdo(padre, hijo)){
                            JOptionPane.showMessageDialog(this, "Nodo hijo agregado a la izquierda del padre.");
                        }else{
                            JOptionPane.showMessageDialog(this, "El nodo padre ya tiene un hijo a la izquierda.");
                        }
                    } else if (lado.equalsIgnoreCase("D")) {
                        if(grafoBinario.agregarHijoDerecho(padre, hijo)){
                            JOptionPane.showMessageDialog(this, "Nodo hijo agregado a la derecha del padre.");
                        }else{
                            JOptionPane.showMessageDialog(this, "El nodo padre ya tiene un hijo a la derecha.");
                        }
                    }else{
                        JOptionPane.showMessageDialog(this, "Opcion no valida. Solo se aceptan I o D");
                        return;
                    }
                    panelGrafo.setGrafo(grafoBinario);
                }
            }
        }
    }

    //Funcion para modificar un nodo del arbol
    private void modificarNodo() {
        String nodoStr = JOptionPane.showInputDialog(this, "Ingrese el nodo a modificar:");
        String nuevoStr = JOptionPane.showInputDialog(this, "Ingrese el nuevo valor:");
        if (nodoStr != null && nuevoStr != null) {
            int nodo = Integer.parseInt(nodoStr);
            int nuevo = Integer.parseInt(nuevoStr);
            VerticeBinario vertice = grafoBinario.buscarNodo(grafoBinario.root, nodo);
            if (vertice == null) {
                JOptionPane.showMessageDialog(this, "El nodo no existe en el arbol.");
                return;
            }
            if (vertice != null) {
                //Se busca si ya existe el valor del nodo hijo en el arbol
                if(grafoBinario.buscarNodo(grafoBinario.root, nuevo) != null){
                    JOptionPane.showMessageDialog(this, "El nuevo nodo ya existe en el arbol.");
                    return;
                }else{
                    vertice.dato = nuevo;
                    panelGrafo.setGrafo(grafoBinario);
                    JOptionPane.showMessageDialog(this, "Nodo modificado correctamente.");
                }
            }
        }
    }

    //Funcion para eliminar un nodo y sus hijos del arbol
    private void eliminarNodoEHijos() {
        String nodoStr = JOptionPane.showInputDialog(this, "Ingrese el nodo a eliminar:");
        if (nodoStr != null) {
            int nodo = Integer.parseInt(nodoStr);
            VerticeBinario vertice = grafoBinario.buscarNodo(grafoBinario.root, nodo);
            if (vertice == null) {
                JOptionPane.showMessageDialog(this, "El nodo no existe en el arbol.");
                return;
            }
            if (vertice != null) {
                vertice.left = null;
                vertice.right = null;
                grafoBinario.eliminarNodo(grafoBinario.root, nodo);
                panelGrafo.setGrafo(grafoBinario);
                JOptionPane.showMessageDialog(this, "Nodo eliminado correctamente.");
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