import java.awt.*;
import javax.swing.*;

interface ArbolBase {
    void insertar(int valor);
    void eliminar(int valor);
    boolean buscar(int valor);
    String inorden();
    NodoVisual getVisualTree();
}

class Nodo {
    int valor;
    Nodo izq, der;

    Nodo(int valor) {
        this.valor = valor;
    }
}

class NodoVisual {
    int valor;
    NodoVisual izq, der, padre;
    Color color = Color.LIGHT_GRAY;
    String extraInfo = "";
    boolean conexionIzq = false;
    boolean conexionDer = false;

    NodoVisual(int valor) {
        this.valor = valor;
    }
}

class PanelDibujo extends JPanel {
    private NodoVisual raizVisual;

    public void setRaizVisual(NodoVisual raizVisual) {
        this.raizVisual = raizVisual;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (raizVisual != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            dibujar(g2, raizVisual, getWidth() / 2, 50, getWidth() / 4);
        }
    }

    private void dibujar(Graphics2D g, NodoVisual nodo, int x, int y, int offset) {
        int radio = 20;
        g.setColor(nodo.color);
        g.fillOval(x - radio, y - radio, 2 * radio, 2 * radio);
        g.setColor(Color.BLACK);
        g.drawOval(x - radio, y - radio, 2 * radio, 2 * radio);

        String texto = String.valueOf(nodo.valor);
        FontMetrics fm = g.getFontMetrics();
        int anchoTexto = fm.stringWidth(texto);
        int altoTexto = fm.getAscent();
        g.drawString(texto, x - anchoTexto / 2, y + altoTexto / 4);

        if (!nodo.extraInfo.isEmpty()) {
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            g.drawString(nodo.extraInfo, x - 30, y + radio + 15);
            g.setFont(new Font("Arial", Font.BOLD, 14));
        }

        int lineaX = x;
        int lineaY = y + radio;

        if (nodo.izq != null) {
            int hijoX = x - offset;
            int hijoY = y + 50;
            g.setColor(nodo.conexionIzq ? Color.BLUE : Color.BLACK);
            g.drawLine(lineaX, lineaY, hijoX, hijoY - radio);
            dibujar(g, nodo.izq, hijoX, hijoY, offset / 2);
        }

        if (nodo.der != null) {
            int hijoX = x + offset;
            int hijoY = y + 50;
            g.setColor(nodo.conexionDer ? Color.BLUE : Color.BLACK);
            g.drawLine(lineaX, lineaY, hijoX, hijoY - radio);
            dibujar(g, nodo.der, hijoX, hijoY, offset / 2);
        }
    }
}

// Añadir ArbolBusqueda
class ArbolBinario implements ArbolBase {
    protected Nodo raiz;

    public void insertar(int valor) {
        raiz = insertarRec(raiz, valor);
    }

    protected Nodo insertarRec(Nodo nodo, int valor) {
        if (nodo == null) return new Nodo(valor);
        if (nodo.izq == null) nodo.izq = insertarRec(nodo.izq, valor);
        else nodo.der = insertarRec(nodo.der, valor);
        return nodo;
    }

    public boolean buscar(int valor) {
        return buscarRec(raiz, valor);
    }

    protected boolean buscarRec(Nodo nodo, int valor) {
        if (nodo == null) return false;
        if (nodo.valor == valor) return true;
        return buscarRec(nodo.izq, valor) || buscarRec(nodo.der, valor);
    }

    public String inorden() {
        StringBuilder sb = new StringBuilder();
        inordenRec(raiz, sb);
        return sb.toString();
    }

    protected void inordenRec(Nodo nodo, StringBuilder sb) {
        if (nodo != null) {
            inordenRec(nodo.izq, sb);
            sb.append(nodo.valor).append(" ");
            inordenRec(nodo.der, sb);
        }
    }

    public NodoVisual getVisualTree() {
        return convertir(raiz);
    }

    protected NodoVisual convertir(Nodo nodo) {
        if (nodo == null) return null;
        NodoVisual vis = new NodoVisual(nodo.valor);
        vis.izq = convertir(nodo.izq);
        if (vis.izq != null) vis.izq.padre = vis;
        vis.der = convertir(nodo.der);
        if (vis.der != null) vis.der.padre = vis;
        return vis;
    }

    private Nodo encontrarMinimo(Nodo nodo) {
        while (nodo.izq != null) nodo = nodo.izq;
        return nodo;
    }

    @Override
    public void eliminar(int valor) {
        raiz = eliminarRec(raiz, valor);
    }

    protected Nodo eliminarRec(Nodo nodo, int valor) {
        if (nodo == null) return null;
    
        if (valor < nodo.valor) {
            nodo.izq = eliminarRec(nodo.izq, valor);
        } else if (valor > nodo.valor) {
            nodo.der = eliminarRec(nodo.der, valor);
        } else {
            // Nodo encontrado
            if (nodo.izq == null) return nodo.der;
            if (nodo.der == null) return nodo.izq;
    
            Nodo sucesor = encontrarMinimo(nodo.der);
            nodo.valor = sucesor.valor;
            nodo.der = eliminarRec(nodo.der, sucesor.valor);
        }
        return nodo;
    }    
}

// Añadir ArbolBusquedaBinaria
class ArbolBusqueda extends ArbolBinario {
    @Override
    protected Nodo insertarRec(Nodo nodo, int valor) {
        if (nodo == null) return new Nodo(valor);
        if (valor < nodo.valor) nodo.izq = insertarRec(nodo.izq, valor);
        else nodo.der = insertarRec(nodo.der, valor);
        return nodo;
    }

    @Override
    protected boolean buscarRec(Nodo nodo, int valor) {
        if (nodo == null) return false;
        if (valor == nodo.valor) return true;
        return valor < nodo.valor ? buscarRec(nodo.izq, valor) : buscarRec(nodo.der, valor);
    }

    @Override
    public void eliminar(int valor) {
        raiz = eliminarRec(raiz, valor);
    }

    private Nodo encontrarMinimo(Nodo nodo) {
        while (nodo.izq != null) nodo = nodo.izq;
        return nodo;
    }

    @Override
    protected Nodo eliminarRec(Nodo nodo, int valor) {
        if (nodo == null) return null;

        if (valor < nodo.valor) {
            nodo.izq = eliminarRec(nodo.izq, valor);
        } else if (valor > nodo.valor) {
            nodo.der = eliminarRec(nodo.der, valor);
        } else {
            // nodo.valor == valor
            if (nodo.izq == null) return nodo.der;
            if (nodo.der == null) return nodo.izq;

            Nodo sucesor = encontrarMinimo(nodo.der);
            nodo.valor = sucesor.valor;
            nodo.der = eliminarRec(nodo.der, sucesor.valor);
        }
        return nodo;
    }
}

// Añadir ArbolAVL
class ArbolAVL extends ArbolBusqueda {
    class NodoAVL extends Nodo {
        int altura = 1;
        NodoAVL(int valor) { super(valor); }
    }

    @Override
    protected Nodo insertarRec(Nodo nodo, int valor) {
        if (nodo == null) return new NodoAVL(valor);

        if (valor < nodo.valor)
            nodo.izq = insertarRec(nodo.izq, valor);
        else
            nodo.der = insertarRec(nodo.der, valor);

        return balancear((NodoAVL) nodo);
    }

    private NodoAVL balancear(NodoAVL nodo) {
        actualizarAltura(nodo);
        int balance = getBalance(nodo);

        if (balance > 1) {
            if (getBalance((NodoAVL) nodo.izq) < 0)
                nodo.izq = rotarIzquierda((NodoAVL) nodo.izq);
            return rotarDerecha(nodo);
        }

        if (balance < -1) {
            if (getBalance((NodoAVL) nodo.der) > 0)
                nodo.der = rotarDerecha((NodoAVL) nodo.der);
            return rotarIzquierda(nodo);
        }

        return nodo;
    }

    private int altura(NodoAVL n) {
        return n == null ? 0 : n.altura;
    }

    private void actualizarAltura(NodoAVL n) {
        n.altura = 1 + Math.max(altura((NodoAVL) n.izq), altura((NodoAVL) n.der));
    }

    private int getBalance(NodoAVL n) {
        return n == null ? 0 : altura((NodoAVL) n.izq) - altura((NodoAVL) n.der);
    }

    private NodoAVL rotarDerecha(NodoAVL y) {
        NodoAVL x = (NodoAVL) y.izq;
        NodoAVL T2 = (NodoAVL) x.der;

        x.der = y;
        y.izq = T2;

        actualizarAltura(y);
        actualizarAltura(x);

        return x;
    }

    private NodoAVL rotarIzquierda(NodoAVL x) {
        NodoAVL y = (NodoAVL) x.der;
        NodoAVL T2 = (NodoAVL) y.izq;

        y.izq = x;
        x.der = T2;

        actualizarAltura(x);
        actualizarAltura(y);

        return y;
    }

    @Override
    public NodoVisual getVisualTree() {
        NodoVisual vis = super.getVisualTree();
        asignarAlturaAVL(vis, raiz);
        return vis;
    }

    private void asignarAlturaAVL(NodoVisual vis, Nodo nodo) {
        if (nodo == null || vis == null) return;
        vis.extraInfo = "h=" + ((NodoAVL) nodo).altura;
        asignarAlturaAVL(vis.izq, nodo.izq);
        asignarAlturaAVL(vis.der, nodo.der);
    }

    @Override
    public void eliminar(int valor) {
        raiz = eliminarRec(raiz, valor);
    }

    @Override
    protected Nodo eliminarRec(Nodo nodo, int valor) {
        if (nodo == null) return null;

        if (valor < nodo.valor) {
            nodo.izq = eliminarRec(nodo.izq, valor);
        } else if (valor > nodo.valor) {
            nodo.der = eliminarRec(nodo.der, valor);
        } else {
            if (nodo.izq == null) return nodo.der;
            if (nodo.der == null) return nodo.izq;

            Nodo sucesor = encontrarMinimo(nodo.der);
            nodo.valor = sucesor.valor;
            nodo.der = eliminarRec(nodo.der, sucesor.valor);
        }

        return balancear((NodoAVL) nodo);
    }

    private Nodo encontrarMinimo(Nodo nodo) {
        while (nodo.izq != null) nodo = nodo.izq;
        return nodo;
    }

}

// Añadir ArbolB
class ArbolB implements ArbolBase {
    private static final int T = 2;

    private class NodoB {
        int[] claves = new int[2 * T - 1];
        NodoB[] hijos = new NodoB[2 * T];
        int n = 0;
        boolean hoja = true;
    }

    private NodoB raiz = new NodoB();

    public void insertar(int k) {
        NodoB r = raiz;
        if (r.n == 2 * T - 1) {
            NodoB s = new NodoB();
            s.hoja = false;
            s.hijos[0] = r;
            dividir(s, 0, r);
            insertarNoLleno(s, k);
            raiz = s;
        } else {
            insertarNoLleno(r, k);
        }
    }

    private void insertarNoLleno(NodoB x, int k) {
        int i = x.n - 1;
        if (x.hoja) {
            while (i >= 0 && k < x.claves[i]) {
                x.claves[i + 1] = x.claves[i];
                i--;
            }
            x.claves[i + 1] = k;
            x.n++;
        } else {
            while (i >= 0 && k < x.claves[i]) i--;
            i++;
            if (x.hijos[i].n == 2 * T - 1) {
                dividir(x, i, x.hijos[i]);
                if (k > x.claves[i]) i++;
            }
            insertarNoLleno(x.hijos[i], k);
        }
    }

    private void dividir(NodoB x, int i, NodoB y) {
        NodoB z = new NodoB();
        z.hoja = y.hoja;
        z.n = T - 1;

        for (int j = 0; j < T - 1; j++)
            z.claves[j] = y.claves[j + T];

        if (!y.hoja) {
            for (int j = 0; j < T; j++)
                z.hijos[j] = y.hijos[j + T];
        }

        y.n = T - 1;

        for (int j = x.n; j >= i + 1; j--)
            x.hijos[j + 1] = x.hijos[j];

        x.hijos[i + 1] = z;

        for (int j = x.n - 1; j >= i; j--)
            x.claves[j + 1] = x.claves[j];

        x.claves[i] = y.claves[T - 1];
        x.n++;
    }

    public boolean buscar(int k) {
        return buscar(raiz, k);
    }

    private boolean buscar(NodoB x, int k) {
        int i = 0;
        while (i < x.n && k > x.claves[i]) i++;
        if (i < x.n && k == x.claves[i]) return true;
        if (x.hoja) return false;
        return buscar(x.hijos[i], k);
    }

    @Override
    public void eliminar(int k) {
        eliminar(raiz, k);
        if (raiz.n == 0 && !raiz.hoja) {
            raiz = raiz.hijos[0];
        }
    }

    private void eliminar(NodoB nodo, int k) {
        int idx = 0;
        while (idx < nodo.n && nodo.claves[idx] < k) idx++;
    
        if (idx < nodo.n && nodo.claves[idx] == k) {
            if (nodo.hoja) {
                for (int i = idx; i < nodo.n - 1; i++) {
                    nodo.claves[i] = nodo.claves[i + 1];
                }
                nodo.n--;
            } else {
                if (nodo.hijos[idx].n >= T) {
                    int pred = obtenerPredecesor(nodo, idx);
                    nodo.claves[idx] = pred;
                    eliminar(nodo.hijos[idx], pred);
                } else if (nodo.hijos[idx + 1].n >= T) {
                    int succ = obtenerSucesor(nodo, idx);
                    nodo.claves[idx] = succ;
                    eliminar(nodo.hijos[idx + 1], succ);
                } else {
                    fusionar(nodo, idx);
                    eliminar(nodo.hijos[idx], k);
                }
            }
        } else {
            if (nodo.hoja) return;
    
            boolean enUltimo = (idx == nodo.n);
            if (nodo.hijos[idx].n < T) {
                llenar(nodo, idx);
            }
    
            if (enUltimo && idx > nodo.n)
                eliminar(nodo.hijos[idx - 1], k);
            else
                eliminar(nodo.hijos[idx], k);
        }
    }
    
    private int obtenerPredecesor(NodoB nodo, int idx) {
        NodoB actual = nodo.hijos[idx];
        while (!actual.hoja)
            actual = actual.hijos[actual.n];
        return actual.claves[actual.n - 1];
    }
    
    private int obtenerSucesor(NodoB nodo, int idx) {
        NodoB actual = nodo.hijos[idx + 1];
        while (!actual.hoja)
            actual = actual.hijos[0];
        return actual.claves[0];
    }
    
    private void llenar(NodoB nodo, int idx) {
        if (idx != 0 && nodo.hijos[idx - 1].n >= T)
            tomarDeAnterior(nodo, idx);
        else if (idx != nodo.n && nodo.hijos[idx + 1].n >= T)
            tomarDeSiguiente(nodo, idx);
        else {
            if (idx != nodo.n)
                fusionar(nodo, idx);
            else
                fusionar(nodo, idx - 1);
        }
    }
    
    private void tomarDeAnterior(NodoB nodo, int idx) {
        NodoB hijo = nodo.hijos[idx];
        NodoB hermano = nodo.hijos[idx - 1];
    
        for (int i = hijo.n - 1; i >= 0; i--)
            hijo.claves[i + 1] = hijo.claves[i];
    
        if (!hijo.hoja) {
            for (int i = hijo.n; i >= 0; i--)
                hijo.hijos[i + 1] = hijo.hijos[i];
        }
    
        hijo.claves[0] = nodo.claves[idx - 1];
        if (!hijo.hoja)
            hijo.hijos[0] = hermano.hijos[hermano.n];
    
        nodo.claves[idx - 1] = hermano.claves[hermano.n - 1];
        hijo.n++;
        hermano.n--;
    }
    
    private void tomarDeSiguiente(NodoB nodo, int idx) {
        NodoB hijo = nodo.hijos[idx];
        NodoB hermano = nodo.hijos[idx + 1];
    
        hijo.claves[hijo.n] = nodo.claves[idx];
        if (!hijo.hoja)
            hijo.hijos[hijo.n + 1] = hermano.hijos[0];
    
        nodo.claves[idx] = hermano.claves[0];
        for (int i = 1; i < hermano.n; i++)
            hermano.claves[i - 1] = hermano.claves[i];
    
        if (!hermano.hoja) {
            for (int i = 1; i <= hermano.n; i++)
                hermano.hijos[i - 1] = hermano.hijos[i];
        }
    
        hijo.n++;
        hermano.n--;
    }
    
    private void fusionar(NodoB nodo, int idx) {
        NodoB hijo = nodo.hijos[idx];
        NodoB hermano = nodo.hijos[idx + 1];
    
        hijo.claves[T - 1] = nodo.claves[idx];
    
        for (int i = 0; i < hermano.n; i++)
            hijo.claves[i + T] = hermano.claves[i];
    
        if (!hijo.hoja) {
            for (int i = 0; i <= hermano.n; i++)
                hijo.hijos[i + T] = hermano.hijos[i];
        }
    
        for (int i = idx + 1; i < nodo.n; i++)
            nodo.claves[i - 1] = nodo.claves[i];
        for (int i = idx + 2; i <= nodo.n; i++)
            nodo.hijos[i - 1] = nodo.hijos[i];
    
        hijo.n += hermano.n + 1;
        nodo.n--;
    }

    
    public String inorden() {
        StringBuilder sb = new StringBuilder();
        inorden(raiz, sb);
        return sb.toString();
    }

    private void inorden(NodoB x, StringBuilder sb) {
        int i;
        for (i = 0; i < x.n; i++) {
            if (!x.hoja) inorden(x.hijos[i], sb);
            sb.append(x.claves[i]).append(" ");
        }
        if (!x.hoja) inorden(x.hijos[i], sb);
    }

    public NodoVisual getVisualTree() {
        return convertir(raiz);
    }

    private NodoVisual convertir(NodoB nodo) {
        if (nodo == null || nodo.n == 0) return null;

        NodoVisual vis = new NodoVisual(nodo.claves[0]);
        vis.color = nodo.hoja ? Color.LIGHT_GRAY : Color.CYAN;
        if (nodo.n > 1) {
            vis.extraInfo = "Claves: [";
            for (int i = 0; i < nodo.n; i++) {
                vis.extraInfo += nodo.claves[i];
                if (i < nodo.n - 1) vis.extraInfo += ",";
            }
            vis.extraInfo += "]";
        }

        if (!nodo.hoja) {
            vis.izq = convertir(nodo.hijos[0]);
            if (vis.izq != null) vis.izq.padre = vis;
            if (nodo.n > 1) {
                vis.der = convertir(nodo.hijos[1]);
                if (vis.der != null) vis.der.padre = vis;
            }
        }

        return vis;
    }
}

// Clase principal
public class ArbolesGUI extends JFrame {
    private ArbolBinario ab = new ArbolBinario();
    private ArbolBusqueda abb = new ArbolBusqueda();
    private ArbolAVL avl = new ArbolAVL();
    private ArbolB b = new ArbolB();

    private ArbolBase arbolActivo;

    private PanelDibujo dibujo = new PanelDibujo();
    private JTextArea salida = new JTextArea();
    private JTextField entrada = new JTextField(5);
    private JComboBox<String> tipoArbol;

    public ArbolesGUI() {
        setTitle("Visualizador Árboles");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(dibujo, BorderLayout.CENTER);

        JPanel controles = new JPanel();
        JButton insertar = new JButton("Insertar");
        JButton eliminar = new JButton("Eliminar");
        JButton buscar = new JButton("Buscar");

        tipoArbol = new JComboBox<>(new String[]{"AB", "ABB", "AVL", "B"});

        controles.add(tipoArbol);
        controles.add(new JLabel("Valor:"));
        controles.add(entrada);
        controles.add(insertar);
        controles.add(eliminar);
        controles.add(buscar);

        salida.setEditable(false);
        panel.add(controles, BorderLayout.NORTH);
        panel.add(new JScrollPane(salida), BorderLayout.SOUTH);
        add(panel);

        insertar.addActionListener(e -> operar("insertar"));
        buscar.addActionListener(e -> operar("buscar"));
        eliminar.addActionListener(e -> operar("eliminar"));

    }

    private void operar(String operacion) {
        try {
            int val = Integer.parseInt(entrada.getText());
            actualizarArbolActivo();

            switch (operacion) {
                case "insertar" -> {
                    arbolActivo.insertar(val);
                    salida.append("\nInsertado: " + val);
                    dibujo.setRaizVisual(arbolActivo.getVisualTree());
                }
                case "buscar" -> {
                    salida.append("\nBuscando: " + val);
                    NodoVisual vis = arbolActivo.getVisualTree();
                    dibujo.setRaizVisual(vis);
                    new BusquedaAnimada(vis, val).execute();
                }
                case "eliminar" -> {
                    arbolActivo.eliminar(val);
                    salida.append("\nEliminado: " + val);
                    dibujo.setRaizVisual(arbolActivo.getVisualTree());
                }
            }
        } catch (NumberFormatException ex) {
            salida.append("\nError: número inválido");
        }
    }

    private void actualizarArbolActivo() {
        String tipo = (String) tipoArbol.getSelectedItem();
        arbolActivo = switch (tipo) {
            case "ABB" -> abb;
            case "AVL" -> avl;
            case "B" -> b;
            default -> ab;
        };
    }

    class BusquedaAnimada extends SwingWorker<Void, Void> {
        private NodoVisual raiz;
        private int objetivo;
        private boolean encontrado = false;

        public BusquedaAnimada(NodoVisual raiz, int objetivo) {
            this.raiz = raiz;
            this.objetivo = objetivo;
        }

        @Override
        protected Void doInBackground() throws Exception {
            buscar(raiz);
            return null;
        }

        private void buscar(NodoVisual nodo) throws InterruptedException {
            if (nodo == null || encontrado) return;

            if (nodo.padre != null) {
                if (nodo.padre.izq == nodo) nodo.padre.conexionIzq = true;
                if (nodo.padre.der == nodo) nodo.padre.conexionDer = true;
            }

            SwingUtilities.invokeLater(() -> {
                nodo.color = Color.ORANGE;
                dibujo.repaint();
            });
            Thread.sleep(600);

            if (nodo.valor == objetivo) {
                SwingUtilities.invokeLater(() -> {
                    nodo.color = Color.RED;
                    dibujo.repaint();
                    salida.append(" -> Encontrado\n");
                });
                encontrado = true;
                return;
            }

            buscar(nodo.izq);
            buscar(nodo.der);

            if (!encontrado) {
                SwingUtilities.invokeLater(() -> {
                    nodo.color = Color.LIGHT_GRAY;
                    if (nodo.padre != null) {
                        if (nodo.padre.izq == nodo) nodo.padre.conexionIzq = false;
                        if (nodo.padre.der == nodo) nodo.padre.conexionDer = false;
                    }
                    dibujo.repaint();
                });
                Thread.sleep(200);
            }
        }
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ArbolesGUI().setVisible(true));
    }
}
