import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

class Nodo {
    int valor, altura;
    Nodo izquierdo, derecho;

    public Nodo(int valor) {
        this.valor = valor;
        this.altura = 1; // Altura inicial del nodo
        izquierdo = derecho = null;
    }
}

class ArbolAVL {
    Nodo raiz;
    private List<Nodo> recorridoBusqueda = new ArrayList<>();

    private int altura(Nodo nodo) {
        return nodo == null ? 0 : nodo.altura;
    }

    private int balanceFactor(Nodo nodo) {
        return nodo == null ? 0 : altura(nodo.izquierdo) - altura(nodo.derecho);
    }

    private Nodo rotacionDerecha(Nodo y) {
        Nodo x = y.izquierdo;
        Nodo T2 = x.derecho;

        x.derecho = y;
        y.izquierdo = T2;

        y.altura = Math.max(altura(y.izquierdo), altura(y.derecho)) + 1;
        x.altura = Math.max(altura(x.izquierdo), altura(x.derecho)) + 1;

        return x;
    }

    private Nodo rotacionIzquierda(Nodo x) {
        Nodo y = x.derecho;
        Nodo T2 = y.izquierdo;

        y.izquierdo = x;
        x.derecho = T2;

        x.altura = Math.max(altura(x.izquierdo), altura(x.derecho)) + 1;
        y.altura = Math.max(altura(y.izquierdo), altura(y.derecho)) + 1;

        return y;
    }

    public void insertar(int valor) {
        raiz = insertarRec(raiz, valor);
    }

    private Nodo insertarRec(Nodo nodo, int valor) {
        if (nodo == null) return new Nodo(valor);

        if (valor < nodo.valor) nodo.izquierdo = insertarRec(nodo.izquierdo, valor);
        else if (valor > nodo.valor) nodo.derecho = insertarRec(nodo.derecho, valor);
        else return nodo; // No se permiten duplicados

        nodo.altura = 1 + Math.max(altura(nodo.izquierdo), altura(nodo.derecho));

        int balance = balanceFactor(nodo);

        // Rotaciones para balancear
        if (balance > 1 && valor < nodo.izquierdo.valor) return rotacionDerecha(nodo);
        if (balance < -1 && valor > nodo.derecho.valor) return rotacionIzquierda(nodo);
        if (balance > 1 && valor > nodo.izquierdo.valor) {
            nodo.izquierdo = rotacionIzquierda(nodo.izquierdo);
            return rotacionDerecha(nodo);
        }
        if (balance < -1 && valor < nodo.derecho.valor) {
            nodo.derecho = rotacionDerecha(nodo.derecho);
            return rotacionIzquierda(nodo);
        }

        return nodo;
    }

    public void eliminar(int valor) {
        raiz = eliminarRec(raiz, valor);
    }

    private Nodo eliminarRec(Nodo nodo, int valor) {
        if (nodo == null) return nodo;

        if (valor < nodo.valor) nodo.izquierdo = eliminarRec(nodo.izquierdo, valor);
        else if (valor > nodo.valor) nodo.derecho = eliminarRec(nodo.derecho, valor);
        else {
            if ((nodo.izquierdo == null) || (nodo.derecho == null)) {
                Nodo temp = nodo.izquierdo != null ? nodo.izquierdo : nodo.derecho;
                nodo = temp;
            } else {
                Nodo temp = minValorNodo(nodo.derecho);
                nodo.valor = temp.valor;
                nodo.derecho = eliminarRec(nodo.derecho, temp.valor);
            }
        }

        if (nodo == null) return nodo;

        nodo.altura = Math.max(altura(nodo.izquierdo), altura(nodo.derecho)) + 1;

        int balance = balanceFactor(nodo);

        // Rotaciones para balancear
        if (balance > 1 && balanceFactor(nodo.izquierdo) >= 0) return rotacionDerecha(nodo);
        if (balance > 1 && balanceFactor(nodo.izquierdo) < 0) {
            nodo.izquierdo = rotacionIzquierda(nodo.izquierdo);
            return rotacionDerecha(nodo);
        }
        if (balance < -1 && balanceFactor(nodo.derecho) <= 0) return rotacionIzquierda(nodo);
        if (balance < -1 && balanceFactor(nodo.derecho) > 0) {
            nodo.derecho = rotacionDerecha(nodo.derecho);
            return rotacionIzquierda(nodo);
        }

        return nodo;
    }

    private Nodo minValorNodo(Nodo nodo) {
        Nodo actual = nodo;
        while (actual.izquierdo != null) actual = actual.izquierdo;
        return actual;
    }

    public boolean buscar(int valor) {
        recorridoBusqueda.clear();
        return buscarRec(raiz, valor);
    }

    private boolean buscarRec(Nodo nodo, int valor) {
        if (nodo == null) return false;
        recorridoBusqueda.add(nodo);
        if (valor == nodo.valor) return true;
        return valor < nodo.valor ? buscarRec(nodo.izquierdo, valor) : buscarRec(nodo.derecho, valor);
    }

    public List<Nodo> getRecorridoBusqueda() {
        return recorridoBusqueda;
    }

    public List<Integer> enOrden() {
        List<Integer> lista = new ArrayList<>();
        enOrdenRec(raiz, lista);
        return lista;
    }

    private void enOrdenRec(Nodo nodo, List<Integer> lista) {
        if (nodo != null) {
            enOrdenRec(nodo.izquierdo, lista);
            lista.add(nodo.valor);
            enOrdenRec(nodo.derecho, lista);
        }
    }
}

class PanelDibujo extends JPanel {
    private ArbolAVL arbol;
    private final int radio = 20;
    private final int distanciaVertical = 60;
    private List<Nodo> recorridoBusqueda;
    private int pasoActual = 0;
    private Timer timer;

    public PanelDibujo(ArbolAVL arbol) {
        this.arbol = arbol;
        this.recorridoBusqueda = new ArrayList<>();
        setPreferredSize(new Dimension(800, 400));
        setBackground(Color.WHITE);
    }

    public void setArbol(ArbolAVL arbol) {
        this.arbol = arbol;
        this.recorridoBusqueda = arbol.getRecorridoBusqueda();
        repaint();
    }

    public void iniciarBusquedaPasoAPaso() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        pasoActual = 0;
        timer = new Timer(500, e -> {
            if (pasoActual < recorridoBusqueda.size()) {
                pasoActual++;
                repaint();
            } else {
                timer.stop();
            }
        });
        timer.start();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (arbol.raiz != null) {
            dibujarNodo(g, arbol.raiz, getWidth() / 2, 40, getWidth() / 4);
        }
    }

    private void dibujarNodo(Graphics g, Nodo nodo, int x, int y, int offset) {
        g.setColor(Color.BLACK);

        // Dibujar líneas de conexión entre nodos
        if (nodo.izquierdo != null) {
            g.drawLine(x, y, x - offset, y + distanciaVertical);
            dibujarNodo(g, nodo.izquierdo, x - offset, y + distanciaVertical, offset / 2);
        }
        if (nodo.derecho != null) {
            g.drawLine(x, y, x + offset, y + distanciaVertical);
            dibujarNodo(g, nodo.derecho, x + offset, y + distanciaVertical, offset / 2);
        }

        // Iluminación de los nodos visitados
        if (recorridoBusqueda.contains(nodo)) {
            if (recorridoBusqueda.indexOf(nodo) <= pasoActual) {
                g.setColor(new Color(255, 223, 186)); // Nodos visitados, amarillo claro
                g.fillOval(x - radio, y - radio, 2 * radio, 2 * radio);
                g.setColor(Color.RED); // Bordes rojos
                g.drawOval(x - radio, y - radio, 2 * radio, 2 * radio);
            } else {
                g.setColor(new Color(135, 206, 250)); // Nodos no visitados, azul claro
                g.fillOval(x - radio, y - radio, 2 * radio, 2 * radio);
                g.setColor(Color.BLACK);
                g.drawOval(x - radio, y - radio, 2 * radio, 2 * radio);
            }
        } else {
            g.setColor(new Color(135, 206, 250)); // Nodos no visitados
            g.fillOval(x - radio, y - radio, 2 * radio, 2 * radio);
            g.setColor(Color.BLACK);
            g.drawOval(x - radio, y - radio, 2 * radio, 2 * radio);
        }

        // Escribir el valor del nodo
        g.drawString(Integer.toString(nodo.valor), x - 6, y + 4);
    }

    public void guardarImagen(String path) {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        paint(g2d);
        g2d.dispose();
        try {
            ImageIO.write(image, "PNG", new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class ArbolGUI extends JFrame {
    private ArbolAVL arbol;
    private JTextArea areaSalida;
    private JTextField campoValor;
    private PanelDibujo panelDibujo;

    public ArbolGUI() {
        arbol = new ArbolAVL();

        setTitle("Árbol AVL");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        campoValor = new JTextField(10);
        JButton btnInsertar = new JButton("Insertar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnBuscar = new JButton("Buscar");
        JButton btnCargar = new JButton("Cargar desde archivo");
        JButton btnMostrar = new JButton("Mostrar En Orden");
        JButton btnGuardar = new JButton("Guardar Árbol");

        areaSalida = new JTextArea(5, 40);
        areaSalida.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaSalida);

        JPanel panelSuperior = new JPanel();
        panelSuperior.add(new JLabel("Valor:"));
        panelSuperior.add(campoValor);
        panelSuperior.add(btnInsertar);
        panelSuperior.add(btnEliminar);
        panelSuperior.add(btnBuscar);
        panelSuperior.add(btnCargar);
        panelSuperior.add(btnMostrar);
        panelSuperior.add(btnGuardar);

        panelDibujo = new PanelDibujo(arbol);

        add(panelSuperior, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelDibujo, BorderLayout.SOUTH);

        // Acciones
        btnInsertar.addActionListener(e -> {
            try {
                int valor = Integer.parseInt(campoValor.getText());
                arbol.insertar(valor);
                mostrar("Insertado: " + valor);
                panelDibujo.repaint();
            } catch (NumberFormatException ex) {
                mostrar("Número inválido.");
            }
        });

        btnEliminar.addActionListener(e -> {
            try {
                int valor = Integer.parseInt(campoValor.getText());
                arbol.eliminar(valor);
                mostrar("Eliminado (si existía): " + valor);
                panelDibujo.repaint();
            } catch (NumberFormatException ex) {
                mostrar("Número inválido.");
            }
        });

        btnBuscar.addActionListener(e -> {
            try {
                int valor = Integer.parseInt(campoValor.getText());
                boolean encontrado = arbol.buscar(valor);
                if (encontrado) {
                    mostrar("Encontrado: " + valor);
                } else {
                    mostrar("No encontrado: " + valor);
                }
                panelDibujo.setArbol(arbol);
                panelDibujo.iniciarBusquedaPasoAPaso();
            } catch (NumberFormatException ex) {
                mostrar("Número inválido.");
            }
        });

        btnCargar.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int resultado = fileChooser.showOpenDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                try {
                    File archivo = fileChooser.getSelectedFile();
                    BufferedReader br = new BufferedReader(new FileReader(archivo));
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        try {
                            arbol.insertar(Integer.parseInt(linea.trim()));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    br.close();
                    mostrar("Árbol cargado desde " + archivo.getName());
                    panelDibujo.setArbol(arbol);
                } catch (IOException ex) {
                    mostrar("Error al cargar el archivo.");
                }
            }
        });

        btnMostrar.addActionListener(e -> {
            List<Integer> listaEnOrden = arbol.enOrden();
            StringBuilder sb = new StringBuilder("En Orden: ");
            for (int valor : listaEnOrden) {
                sb.append(valor).append(" ");
            }
            mostrar(sb.toString());
        });

        btnGuardar.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int resultado = fileChooser.showSaveDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                try {
                    File archivo = fileChooser.getSelectedFile();
                    BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));
                    for (int valor : arbol.enOrden()) {
                        bw.write(Integer.toString(valor));
                        bw.newLine();
                    }
                    bw.close();
                    mostrar("Árbol guardado en " + archivo.getName());
                } catch (IOException ex) {
                    mostrar("Error al guardar el archivo.");
                }
            }
        });
    }

    private void mostrar(String mensaje) {
        areaSalida.setText(mensaje);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ArbolGUI().setVisible(true);
        });
    }
}